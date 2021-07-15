package com.thevoxelbox.voxelsniper.sniper;

import com.fastasyncworldedit.core.Fawe;
import com.fastasyncworldedit.core.beta.implementation.queue.QueueHandler;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.bukkit.BukkitPlayer;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.session.request.Request;
import com.sk89q.worldedit.util.Location;
import com.sk89q.worldedit.world.block.BlockType;
import com.thevoxelbox.voxelsniper.brush.Brush;
import com.thevoxelbox.voxelsniper.brush.PerformerBrush;
import com.thevoxelbox.voxelsniper.brush.property.BrushProperties;
import com.thevoxelbox.voxelsniper.sniper.snipe.Snipe;
import com.thevoxelbox.voxelsniper.sniper.snipe.message.SnipeMessenger;
import com.thevoxelbox.voxelsniper.sniper.toolkit.ToolAction;
import com.thevoxelbox.voxelsniper.sniper.toolkit.Toolkit;
import com.thevoxelbox.voxelsniper.sniper.toolkit.ToolkitProperties;
import com.thevoxelbox.voxelsniper.util.material.Materials;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Sniper {

    private static final String DEFAULT_TOOLKIT_NAME = "default";

    private final UUID uuid;
    private final List<Toolkit> toolkits = new ArrayList<>();
    private boolean enabled = true;

    public Sniper(UUID uuid) {
        this.uuid = uuid;
        Toolkit defaultToolkit = createDefaultToolkit();
        this.toolkits.add(defaultToolkit);
    }

    private Toolkit createDefaultToolkit() {
        Toolkit toolkit = new Toolkit("default");
        toolkit.addToolAction(Material.ARROW, ToolAction.ARROW);
        toolkit.addToolAction(Material.GUNPOWDER, ToolAction.GUNPOWDER);
        return toolkit;
    }

    public Player getPlayer() {
        Player player = Bukkit.getPlayer(this.uuid);
        if (player == null) {
            throw new UnknownSniperPlayerException();
        }
        return player;
    }

    @Nullable
    public Toolkit getCurrentToolkit() {
        Player player = getPlayer();
        PlayerInventory inventory = player.getInventory();
        ItemStack itemInHand = inventory.getItemInMainHand();
        Material itemType = itemInHand.getType();
        if (itemType.isEmpty()) {
            return getToolkit(DEFAULT_TOOLKIT_NAME);
        }
        return getToolkit(itemType);
    }

    public void addToolkit(Toolkit toolkit) {
        this.toolkits.add(toolkit);
    }

    @Nullable
    public Toolkit getToolkit(Material itemType) {
        return this.toolkits.stream()
                .filter(toolkit -> toolkit.hasToolAction(itemType))
                .findFirst()
                .orElse(null);
    }

    @Nullable
    public Toolkit getToolkit(String toolkitName) {
        return this.toolkits.stream()
                .filter(toolkit -> toolkitName.equals(toolkit.getToolkitName()))
                .findFirst()
                .orElse(null);
    }

    public void removeToolkit(Toolkit toolkit) {
        this.toolkits.remove(toolkit);
    }

    /**
     * Sniper execution call.
     *
     * @param action           Action player performed
     * @param usedItem         Item in hand of player
     * @param clickedBlock     Block that the player targeted/interacted with
     * @param clickedBlockFace Face of that targeted Block
     * @return true if command visibly processed, false otherwise.
     */
    //FAWE MODIFIED
    public boolean snipe(
            Player player,
            Action action,
            Material usedItem,
            @Nullable Block clickedBlock,
            BlockFace clickedBlockFace
    ) {
        { //FAWE ADDED
            switch (action) {
                case LEFT_CLICK_AIR:
                case LEFT_CLICK_BLOCK:
                case RIGHT_CLICK_AIR:
                case RIGHT_CLICK_BLOCK:
                    break;
                default:
                    return false;
            }
            if (toolkits.isEmpty()) {
                return false;
            }
        }
        Toolkit toolkit = getToolkit(usedItem);
        if (toolkit == null) {
            return false;
        }
        ToolAction toolAction = toolkit.getToolAction(usedItem);
        if (toolAction == null) {
            return false;
        }
        BrushProperties currentBrushProperties = toolkit.getCurrentBrushProperties();
        String permission = currentBrushProperties.getPermission();
        if (permission != null && !player.hasPermission(permission)) {
            player.sendMessage("You are not allowed to use this brush. You're missing the permission node '" + permission + "'");
            return false;
        }
        { //FAWE ADDED
            BukkitPlayer wePlayer = BukkitAdapter.adapt(player);
            LocalSession session = wePlayer.getSession();
            QueueHandler queue = Fawe.get().getQueueHandler();
            queue.async(() -> {
                synchronized (session) {
                    if (!player.isValid()) {
                        return;
                    }
                    snipeOnCurrentThread(
                            wePlayer,
                            player,
                            action,
                            clickedBlock,
                            clickedBlockFace,
                            toolkit,
                            toolAction,
                            currentBrushProperties
                    );
                }
            });
        }
        return true;
    }

    public synchronized boolean snipeOnCurrentThread(
            com.sk89q.worldedit.entity.Player fp,
            Player player,
            Action action,
            @Nullable Block clickedBlock,
            BlockFace clickedBlockFace,
            Toolkit toolkit,
            ToolAction toolAction,
            BrushProperties currentBrushProperties
    ) {
        LocalSession session = fp.getSession(); //FAWE add
        synchronized (session) {//FAWE add
            EditSession editSession = session.createEditSession(fp); //FAWE add

            try {//FAWE ADD
                ToolkitProperties toolkitProperties = toolkit.getProperties();
                BlockVector3 rayTraceBlock = null;
                {//FAWE add
                    Request.reset();
                    Request.request().setExtent(editSession);
                    if (clickedBlock == null) {
                        int distance = toolkitProperties.getBlockTracerRange() == null ? Math.max(
                                Bukkit.getViewDistance(),
                                3
                        ) * 16 - toolkitProperties.getBrushSize() : toolkitProperties.getBlockTracerRange();
                        Location rayTraceResult = fp.getBlockTrace(distance, true);
                        if (rayTraceResult != null) {
                            rayTraceBlock = rayTraceResult.toBlockPoint();
                        }
                    }
                }
                BlockVector3 targetBlock = clickedBlock == null
                        ? rayTraceBlock
                        : BukkitAdapter.asBlockVector(clickedBlock.getLocation());
                if (player.isSneaking()) {
                    SnipeMessenger messenger = new SnipeMessenger(toolkitProperties, currentBrushProperties, player);
                    if (action == Action.LEFT_CLICK_BLOCK || action == Action.LEFT_CLICK_AIR) {
                        if (toolAction == ToolAction.ARROW) {
                            BlockType material = editSession.getBlockType(
                                    targetBlock.getX(),
                                    targetBlock.getY(),
                                    targetBlock.getZ()
                            );

                            if (Materials.isEmpty(material)) {
                                toolkitProperties.resetBlockData();
                            } else {
                                toolkitProperties.setBlockType(material);
                            }
                            messenger.sendBlockTypeMessage();
                            return true;
                        } else if (toolAction == ToolAction.GUNPOWDER) {
                            Material material = BukkitAdapter.adapt(editSession.getBlockType(
                                    targetBlock.getX(),
                                    targetBlock.getY(),
                                    targetBlock.getZ()
                            ));

                            if (material.isEmpty()) {
                                toolkitProperties.resetBlockData();
                            } else {
                                toolkitProperties.setBlockData(editSession.getBlock(targetBlock));
                            }
                            messenger.sendBlockDataMessage();
                            return true;
                        }
                        return false;
                    } else if (action == Action.RIGHT_CLICK_BLOCK || action == Action.RIGHT_CLICK_AIR) {
                        if (toolAction == ToolAction.ARROW) {
                            if (targetBlock == null) {
                                toolkitProperties.resetReplaceBlockData();
                            } else {
                                toolkitProperties.setReplaceBlockType(editSession.getBlockType(
                                        targetBlock.getX(),
                                        targetBlock.getY(),
                                        targetBlock.getZ()
                                ));
                            }
                            messenger.sendReplaceBlockTypeMessage();
                            return true;
                        } else if (toolAction == ToolAction.GUNPOWDER) {
                            if (targetBlock == null) {
                                toolkitProperties.resetReplaceBlockData();
                            } else {
                                toolkitProperties.setReplaceBlockData(editSession.getBlock(targetBlock));
                            }
                            messenger.sendReplaceBlockDataMessage();
                            return true;
                        }
                        return false;
                    }
                    return false;
                } else {
                    if (action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK) {
                        if (targetBlock.getY() != editSession.getMinY() &&
                                BukkitAdapter.adapt(editSession.getBlockType(
                                        targetBlock.getX(),
                                        targetBlock.getY(),
                                        targetBlock.getZ()
                                )).isEmpty()) {
                            player.sendMessage(ChatColor.RED + "Snipe target block must be visible.");
                            return true;
                        }
                        Brush currentBrush = toolkit.getCurrentBrush();
                        if (currentBrush == null) {
                            return false;
                        }
                        Snipe snipe = new Snipe(this, toolkit, toolkitProperties, currentBrushProperties, currentBrush);
                        if (currentBrush instanceof PerformerBrush) {
                            PerformerBrush performerBrush = (PerformerBrush) currentBrush;
                            performerBrush.initialize(snipe);
                        }
                        BlockVector3 lastBlock = targetBlock.add(
                                clickedBlockFace.getModX(),
                                clickedBlockFace.getModY(),
                                clickedBlockFace.getModZ()
                        );
                        currentBrush.perform(snipe, toolAction, editSession, targetBlock, lastBlock);
                        return true;
                    }
                }
                return false;
            } finally { //FAWE ADD
                session.remember(editSession);
                editSession.flushQueue();
                WorldEdit.getInstance().flushBlockBag(fp, editSession);
            }
        }
    }

    public void sendInfo(CommandSender sender) {
        Toolkit toolkit = getCurrentToolkit();
        if (toolkit == null) {
            sender.sendMessage("Current toolkit: none");
            return;
        }
        sender.sendMessage("Current toolkit: " + toolkit.getToolkitName());
        BrushProperties brushProperties = toolkit.getCurrentBrushProperties();
        Brush brush = toolkit.getCurrentBrush();
        if (brush == null) {
            sender.sendMessage("No brush selected.");
            return;
        }
        ToolkitProperties toolkitProperties = toolkit.getProperties();
        Snipe snipe = new Snipe(this, toolkit, toolkitProperties, brushProperties, brush);
        brush.sendInfo(snipe);
        if (brush instanceof PerformerBrush) {
            PerformerBrush performer = (PerformerBrush) brush;
            performer.sendPerformerInfo(snipe);
        }
    }

    public UUID getUuid() {
        return this.uuid;
    }

    public boolean isEnabled() {
        return this.enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public List<Toolkit> getToolkits() {
        return toolkits;
    }

}

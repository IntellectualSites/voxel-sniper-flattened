package com.thevoxelbox.voxelsniper.command.executor;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.thevoxelbox.voxelsniper.VoxelSniperPlugin;
import com.thevoxelbox.voxelsniper.command.CommandExecutor;
import com.thevoxelbox.voxelsniper.command.TabCompleter;
import com.thevoxelbox.voxelsniper.config.VoxelSniperConfig;
import com.thevoxelbox.voxelsniper.sniper.Sniper;
import com.thevoxelbox.voxelsniper.sniper.SniperRegistry;
import com.thevoxelbox.voxelsniper.sniper.toolkit.BlockTracer;
import com.thevoxelbox.voxelsniper.sniper.toolkit.Toolkit;
import com.thevoxelbox.voxelsniper.sniper.toolkit.ToolkitProperties;
import com.thevoxelbox.voxelsniper.util.message.Messenger;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class VoxelExecutor implements CommandExecutor, TabCompleter {

    private static final List<NamespacedKey> BLOCK_KEYS = Arrays.stream(Material.values())
            .filter(Material::isBlock)
            .map(Material::getKey)
            .collect(Collectors.toList());

    private final VoxelSniperPlugin plugin;

    public VoxelExecutor(VoxelSniperPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void executeCommand(CommandSender sender, String[] arguments) {
        SniperRegistry sniperRegistry = this.plugin.getSniperRegistry();
        Player player = (Player) sender;
        Sniper sniper = sniperRegistry.getSniper(player);
        if (sniper == null) {
            return;
        }
        Toolkit toolkit = sniper.getCurrentToolkit();
        if (toolkit == null) {
            return;
        }
        ToolkitProperties toolkitProperties = toolkit.getProperties();
        if (toolkitProperties == null) {
            return;
        }
        Messenger messenger = new Messenger(sender);
        VoxelSniperConfig config = this.plugin.getVoxelSniperConfig();
        List<Material> liteSniperRestrictedMaterials = config.getLitesniperRestrictedMaterials();
        if (arguments.length == 0) {
            BlockTracer blockTracer = toolkitProperties.createBlockTracer(player);
            Block targetBlock = blockTracer.getTargetBlock();
            if (targetBlock != null) {
                Material targetBlockType = targetBlock.getType();
                if (!sender.hasPermission("voxelsniper.ignorelimitations") && liteSniperRestrictedMaterials.contains(
                        targetBlockType)) {
                    sender.sendMessage("You are not allowed to use " + targetBlockType.name() + ".");
                    return;
                }
                toolkitProperties.setBlockType(BukkitAdapter.asBlockType(targetBlockType));
                messenger.sendBlockTypeMessage(BukkitAdapter.asBlockType(targetBlockType));
            }
            return;
        }
        Material material = Material.matchMaterial(arguments[0]);
        if (material != null && material.isBlock()) {
            if (!sender.hasPermission("voxelsniper.ignorelimitations") && liteSniperRestrictedMaterials.contains(material)) {
                sender.sendMessage("You are not allowed to use " + material.name() + ".");
                return;
            }
            toolkitProperties.setBlockType(BukkitAdapter.asBlockType(material));
            messenger.sendBlockTypeMessage(BukkitAdapter.asBlockType(material));
        } else {
            sender.sendMessage(ChatColor.RED + "You have entered an invalid Item ID.");
        }
    }

    @Override
    public List<String> complete(CommandSender sender, String[] arguments) {
        if (arguments.length == 1) {
            String argument = arguments[0];
            String argumentLowered = argument.toLowerCase();
            return BLOCK_KEYS.stream()
                    .filter(namespacedKey -> {
                        String key = namespacedKey.getKey();
                        return key.startsWith(argumentLowered);
                    })
                    .map(NamespacedKey::toString)
                    .collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

}

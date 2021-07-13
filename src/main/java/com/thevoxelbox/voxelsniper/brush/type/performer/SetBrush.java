package com.thevoxelbox.voxelsniper.brush.type.performer;

import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.world.World;
import com.thevoxelbox.voxelsniper.sniper.snipe.Snipe;
import com.thevoxelbox.voxelsniper.sniper.snipe.message.SnipeMessenger;
import org.bukkit.ChatColor;
import org.jetbrains.annotations.Nullable;

public class SetBrush extends AbstractPerformerBrush {

    private static final int SELECTION_SIZE_MAX = 5000000;

    @Nullable
    private BlockVector3 block;
    private World world;

    @Override
    public void handleArrowAction(Snipe snipe) {
        BlockVector3 targetBlock = getTargetBlock();
        if (set(targetBlock, getEditSession().getWorld(), snipe)) {
            SnipeMessenger messenger = snipe.createMessenger();
            messenger.sendMessage(ChatColor.GRAY + "Point one");
        }
    }

    @Override
    public void handleGunpowderAction(Snipe snipe) {
        BlockVector3 lastBlock = getLastBlock();
        if (set(lastBlock, getEditSession().getWorld(), snipe)) {
            SnipeMessenger messenger = snipe.createMessenger();
            messenger.sendMessage(ChatColor.GRAY + "Point one");
        }
    }

    private boolean set(BlockVector3 block, World world, Snipe snipe) {
        if (this.block == null) {
            this.block = block;
            this.world = world;
            return true;
        }
        SnipeMessenger messenger = snipe.createMessenger();
        String name = this.world.getName();
        String parameterBlockWorldName = world.getName();
        if (!name.equals(parameterBlockWorldName)) {
            messenger.sendMessage(ChatColor.RED + "You selected points in different worlds!");
            this.block = null;
            return true;
        }
        int x1 = this.block.getX();
        int x2 = block.getX();
        int y1 = this.block.getY();
        int y2 = block.getY();
        int z1 = this.block.getZ();
        int z2 = block.getZ();
        int lowX = Math.min(x1, x2);
        int lowY = Math.min(y1, y2);
        int lowZ = Math.min(z1, z2);
        int highX = Math.max(x1, x2);
        int highY = Math.max(y1, y2);
        int highZ = Math.max(z1, z2);
        if (Math.abs(highX - lowX) * Math.abs(highZ - lowZ) * Math.abs(highY - lowY) > SELECTION_SIZE_MAX) {
            messenger.sendMessage(ChatColor.RED + "Selection size above hardcoded limit, please use a smaller selection.");
        } else {
            for (int y = lowY; y <= highY; y++) {
                for (int x = lowX; x <= highX; x++) {
                    for (int z = lowZ; z <= highZ; z++) {
                        this.performer.perform(getEditSession(), x, clampY(y), z, clampY(x, y, z));
                    }
                }
            }
        }
        this.block = null;
        return false;
    }

    @Override
    public void sendInfo(Snipe snipe) {
        this.block = null;
        SnipeMessenger messenger = snipe.createMessenger();
        messenger.sendBrushNameMessage();
    }

}

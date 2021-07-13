package com.thevoxelbox.voxelsniper.brush.type.redstone;

import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.registry.state.Property;
import com.sk89q.worldedit.world.block.BlockState;
import com.sk89q.worldedit.world.block.BlockType;
import com.sk89q.worldedit.world.block.BlockTypes;
import com.thevoxelbox.voxelsniper.brush.type.AbstractBrush;
import com.thevoxelbox.voxelsniper.sniper.snipe.Snipe;
import com.thevoxelbox.voxelsniper.sniper.snipe.message.SnipeMessenger;
import org.bukkit.ChatColor;
import org.jetbrains.annotations.Nullable;

public class SetRedstoneRotateBrush extends AbstractBrush {

    @Nullable
    private BlockVector3 block;

    @Override
    public void handleArrowAction(Snipe snipe) {
        BlockVector3 targetBlock = getTargetBlock();
        if (set(targetBlock)) {
            SnipeMessenger messenger = snipe.createMessenger();
            messenger.sendMessage(ChatColor.GRAY + "Point one");
        }
    }

    @Override
    public void handleGunpowderAction(Snipe snipe) {
        BlockVector3 lastBlock = getLastBlock();
        if (set(lastBlock)) {
            SnipeMessenger messenger = snipe.createMessenger();
            messenger.sendMessage(ChatColor.GRAY + "Point one");
        }
    }

    private boolean set(BlockVector3 block) {
        if (this.block == null) {
            this.block = block;
            return true;
        } else {
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
            for (int y = lowY; y <= highY; y++) {
                for (int x = lowX; x <= highX; x++) {
                    for (int z = lowZ; z <= highZ; z++) {
                        perform(x, clampY(y), z, clampY(x, y, z));
                    }
                }
            }
            this.block = null;
            return false;
        }
    }

    private void perform(int x, int y, int z, BlockState block) {
        BlockType type = block.getBlockType();
        if (type == BlockTypes.REPEATER) {
            Property<Integer> delayProperty = type.getProperty("delay");
            if (delayProperty == null) {
                return;
            }
            int delay = block.getState(delayProperty);
            block = block.with(delayProperty, -delay % 4 + 1 < 5 ? (delay + 1) : (delay - 4));
            setBlockData(x, y, z, block);
        }
    }

    @Override
    public void sendInfo(Snipe snipe) {
        this.block = null;
        SnipeMessenger messenger = snipe.createMessenger();
        messenger.sendBrushNameMessage();
    }

}

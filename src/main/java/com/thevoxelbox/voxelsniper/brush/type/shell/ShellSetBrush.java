package com.thevoxelbox.voxelsniper.brush.type.shell;

import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.world.World;
import com.sk89q.worldedit.world.block.BlockType;
import com.thevoxelbox.voxelsniper.brush.type.AbstractBrush;
import com.thevoxelbox.voxelsniper.sniper.snipe.Snipe;
import com.thevoxelbox.voxelsniper.sniper.snipe.message.SnipeMessenger;
import com.thevoxelbox.voxelsniper.sniper.toolkit.ToolkitProperties;
import org.bukkit.ChatColor;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class ShellSetBrush extends AbstractBrush {

    private static final int MAX_SIZE = 5000000;

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
        SnipeMessenger messenger = snipe.createMessenger();
        ToolkitProperties toolkitProperties = snipe.getToolkitProperties();
        if (this.block == null) {
            this.block = block;
            this.world = world;
            return true;
        } else {
            if (!this.world.getName().equals(world.getName())) {
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
            int size = Math.abs(highX - lowX) * Math.abs(highZ - lowZ) * Math.abs(highY - lowY);
            if (size > MAX_SIZE) {
                messenger.sendMessage(ChatColor.RED + "Selection size above hardcoded limit, please use a smaller selection.");
            } else {
                List<BlockVector3> blocks = new ArrayList<>(size / 2);
                for (int y = lowY; y <= highY; y++) {
                    for (int x = lowX; x <= highX; x++) {
                        for (int z = lowZ; z <= highZ; z++) {
                            BlockType replaceBlockDataType = toolkitProperties.getReplaceBlockType();
                            if (isBlockTypeNotEqual(y, x, z, replaceBlockDataType) && isBlockTypeNotEqual(
                                    y,
                                    x + 1,
                                    z,
                                    replaceBlockDataType
                            ) && isBlockTypeNotEqual(y, x - 1, z, replaceBlockDataType) && isBlockTypeNotEqual(
                                    y,
                                    x,
                                    z + 1,
                                    replaceBlockDataType
                            ) && isBlockTypeNotEqual(y, x, z - 1, replaceBlockDataType) && isBlockTypeNotEqual(
                                    y + 1,
                                    x,
                                    z,
                                    replaceBlockDataType
                            ) && isBlockTypeNotEqual(y - 1, x, z, replaceBlockDataType)) {
                                blocks.add(BlockVector3.at(x, y, z));
                            }
                        }
                    }
                }
                for (BlockVector3 currentBlock : blocks) {
                    BlockType currentBlockType = getBlockType(currentBlock);
                    BlockType blockType = toolkitProperties.getBlockType();
                    if (currentBlockType != blockType) {
                        setBlockType(currentBlock, blockType);
                    }
                }
                messenger.sendMessage(ChatColor.AQUA + "Shell complete.");
            }
            this.block = null;
            return false;
        }
    }

    private boolean isBlockTypeNotEqual(int y, int x, int z, BlockType replaceBlockDataType) {
        BlockType blockType = getBlockType(x, y, z);
        return blockType != replaceBlockDataType;
    }

    @Override
    public void sendInfo(Snipe snipe) {
        snipe.createMessageSender()
                .brushNameMessage()
                .brushSizeMessage()
                .blockTypeMessage()
                .replaceBlockTypeMessage()
                .send();
    }

}

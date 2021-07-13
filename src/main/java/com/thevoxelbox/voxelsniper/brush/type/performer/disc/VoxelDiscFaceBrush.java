package com.thevoxelbox.voxelsniper.brush.type.performer.disc;

import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.util.Direction;
import com.thevoxelbox.voxelsniper.brush.type.performer.AbstractPerformerBrush;
import com.thevoxelbox.voxelsniper.sniper.snipe.Snipe;
import com.thevoxelbox.voxelsniper.sniper.toolkit.ToolkitProperties;

public class VoxelDiscFaceBrush extends AbstractPerformerBrush {

    @Override
    public void handleArrowAction(Snipe snipe) {
        BlockVector3 lastBlock = getLastBlock();
        BlockVector3 targetBlock = getTargetBlock();
        Direction face = getDirection(targetBlock, lastBlock);
        if (face == null) {
            return;
        }
        pre(snipe, face, targetBlock);
    }

    @Override
    public void handleGunpowderAction(Snipe snipe) {
        BlockVector3 lastBlock = getLastBlock();
        BlockVector3 targetBlock = getTargetBlock();
        Direction face = getDirection(targetBlock, lastBlock);
        if (face == null) {
            return;
        }
        pre(snipe, face, lastBlock);
    }

    private void pre(Snipe snipe, Direction blockFace, BlockVector3 targetBlock) {
        switch (blockFace) {
            case NORTH:
            case SOUTH:
                discNorthSouth(snipe, targetBlock);
                break;
            case EAST:
            case WEST:
                discEastWest(snipe, targetBlock);
                break;
            case UP:
            case DOWN:
                disc(snipe, targetBlock);
                break;
            default:
                break;
        }
    }

    private void discNorthSouth(Snipe snipe, BlockVector3 targetBlock) {
        ToolkitProperties toolkitProperties = snipe.getToolkitProperties();
        int brushSize = toolkitProperties.getBrushSize();
        int blockX = targetBlock.getX();
        int blockY = targetBlock.getY();
        int blockZ = targetBlock.getZ();
        for (int x = brushSize; x >= -brushSize; x--) {
            for (int y = brushSize; y >= -brushSize; y--) {
                this.performer.perform(
                        getEditSession(),
                        blockX + x,
                        clampY(blockY + y),
                        blockZ,
                        getBlock(blockX + x, clampY(blockY + y), blockZ)
                );
            }
        }
    }

    private void discEastWest(Snipe snipe, BlockVector3 targetBlock) {
        ToolkitProperties toolkitProperties = snipe.getToolkitProperties();
        int brushSize = toolkitProperties.getBrushSize();
        int blockX = targetBlock.getX();
        int blockY = targetBlock.getY();
        int blockZ = targetBlock.getZ();
        for (int x = brushSize; x >= -brushSize; x--) {
            for (int y = brushSize; y >= -brushSize; y--) {
                this.performer.perform(
                        getEditSession(),
                        blockX,
                        clampY(blockY + x),
                        blockZ + y,
                        clampY(blockX, blockY + y, blockZ)
                );
            }
        }
    }

    private void disc(Snipe snipe, BlockVector3 targetBlock) {
        ToolkitProperties toolkitProperties = snipe.getToolkitProperties();
        int brushSize = toolkitProperties.getBrushSize();
        int blockX = targetBlock.getX();
        int blockY = targetBlock.getY();
        int blockZ = targetBlock.getZ();
        for (int x = brushSize; x >= -brushSize; x--) {
            for (int y = brushSize; y >= -brushSize; y--) {
                this.performer.perform(
                        getEditSession(),
                        blockX + x,
                        clampY(blockY),
                        blockZ + y,
                        getBlock(blockX + x, clampY(blockY), blockZ + y)
                );
            }
        }
    }

    @Override
    public void sendInfo(Snipe snipe) {
        snipe.createMessageSender()
                .brushNameMessage()
                .brushSizeMessage()
                .send();
    }

}

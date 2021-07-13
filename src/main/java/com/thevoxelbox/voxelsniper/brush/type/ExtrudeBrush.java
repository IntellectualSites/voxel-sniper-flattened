package com.thevoxelbox.voxelsniper.brush.type;

import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.util.Direction;
import com.sk89q.worldedit.world.block.BlockState;
import com.thevoxelbox.voxelsniper.sniper.snipe.Snipe;
import com.thevoxelbox.voxelsniper.sniper.snipe.message.SnipeMessenger;
import com.thevoxelbox.voxelsniper.sniper.toolkit.ToolkitProperties;
import org.bukkit.ChatColor;
import org.jetbrains.annotations.Nullable;

public class ExtrudeBrush extends AbstractBrush {

    private double trueCircle;

    @Override
    public void handleCommand(String[] parameters, Snipe snipe) {
        SnipeMessenger messenger = snipe.createMessenger();
        for (String parameter : parameters) {
            try {
                if (parameter.equalsIgnoreCase("info")) {
                    messenger.sendMessage(ChatColor.GOLD + "Extrude brush Parameters:");
                    messenger.sendMessage(ChatColor.AQUA + "/b ex true -- will use a true circle algorithm instead of the skinnier version with classic sniper nubs. /b ex false will switch back. (false is default)");
                    return;
                } else if (parameter.startsWith("true")) {
                    this.trueCircle = 0.5;
                    messenger.sendMessage(ChatColor.AQUA + "True circle mode ON.");
                } else if (parameter.startsWith("false")) {
                    this.trueCircle = 0;
                    messenger.sendMessage(ChatColor.AQUA + "True circle mode OFF.");
                } else {
                    messenger.sendMessage(ChatColor.RED + "Invalid brush parameters! Use the \"info\" parameter to display parameter info.");
                    return;
                }
            } catch (RuntimeException exception) {
                messenger.sendMessage(ChatColor.RED + "Incorrect parameter \"" + parameter + "\"; use the \"info\" parameter.");
            }
        }
    }

    private void extrudeUpOrDown(Snipe snipe, boolean isUp) {
        ToolkitProperties toolkitProperties = snipe.getToolkitProperties();
        int brushSize = toolkitProperties.getBrushSize();
        double brushSizeSquared = Math.pow(brushSize + this.trueCircle, 2);
        for (int x = -brushSize; x <= brushSize; x++) {
            double xSquared = Math.pow(x, 2);
            for (int z = -brushSize; z <= brushSize; z++) {
                if ((xSquared + Math.pow(z, 2)) <= brushSizeSquared) {
                    int direction = (isUp ? 1 : -1);
                    for (int y = 0; y < Math.abs(toolkitProperties.getVoxelHeight()); y++) {
                        int tempY = y * direction;
                        BlockVector3 targetBlock = getTargetBlock();
                        int targetBlockX = targetBlock.getX();
                        int targetBlockY = targetBlock.getY();
                        int targetBlockZ = targetBlock.getZ();
                        perform(
                                targetBlockX + x,
                                targetBlockY + tempY,
                                targetBlockZ + z,
                                clampY(targetBlockX + x, targetBlockY + tempY, targetBlockZ + z),
                                targetBlockX + x,
                                targetBlockY + tempY + direction,
                                targetBlockZ + z,
                                clampY(targetBlockX + x, targetBlockY + tempY + direction, targetBlockZ + z),
                                toolkitProperties
                        );
                    }
                }
            }
        }
    }

    private void extrudeNorthOrSouth(Snipe snipe, boolean isSouth) {
        ToolkitProperties toolkitProperties = snipe.getToolkitProperties();
        int brushSize = toolkitProperties.getBrushSize();
        double brushSizeSquared = Math.pow(brushSize + this.trueCircle, 2);
        for (int x = -brushSize; x <= brushSize; x++) {
            double xSquared = Math.pow(x, 2);
            for (int y = -brushSize; y <= brushSize; y++) {
                if ((xSquared + Math.pow(y, 2)) <= brushSizeSquared) {
                    int direction = (isSouth) ? 1 : -1;
                    for (int z = 0; z < Math.abs(toolkitProperties.getVoxelHeight()); z++) {
                        int tempZ = z * direction;
                        BlockVector3 targetBlock = this.getTargetBlock();
                        perform(
                                targetBlock.getX() + x,
                                targetBlock.getY() + y,
                                targetBlock.getZ() + tempZ,
                                clampY(targetBlock.getX() + x, targetBlock.getY() + y, targetBlock.getZ() + tempZ),
                                targetBlock.getX() + x,
                                targetBlock.getY() + y,
                                targetBlock.getZ() + tempZ + direction,
                                this.clampY(
                                        targetBlock.getX() + x,
                                        targetBlock.getY() + y,
                                        targetBlock.getZ() + tempZ + direction
                                ),
                                toolkitProperties
                        );
                    }
                }
            }
        }
    }

    private void extrudeEastOrWest(Snipe snipe, boolean isEast) {
        ToolkitProperties toolkitProperties = snipe.getToolkitProperties();
        int brushSize = toolkitProperties.getBrushSize();
        double brushSizeSquared = Math.pow(brushSize + this.trueCircle, 2);
        for (int y = -brushSize; y <= brushSize; y++) {
            double ySquared = Math.pow(y, 2);
            for (int z = -brushSize; z <= brushSize; z++) {
                if ((ySquared + Math.pow(z, 2)) <= brushSizeSquared) {
                    int direction = (isEast) ? 1 : -1;
                    for (int x = 0; x < Math.abs(toolkitProperties.getVoxelHeight()); x++) {
                        int tempX = x * direction;
                        BlockVector3 targetBlock = this.getTargetBlock();
                        perform(
                                targetBlock.getX() + tempX,
                                targetBlock.getY() + y,
                                targetBlock.getZ() + z,
                                this.clampY(targetBlock.getX() + tempX, targetBlock.getY() + y, targetBlock.getZ() + z),
                                targetBlock.getX() + tempX + direction,
                                targetBlock.getY() + y,
                                targetBlock.getZ() + z,
                                this.clampY(
                                        targetBlock.getX() + tempX + direction,
                                        targetBlock.getY() + y,
                                        targetBlock.getZ() + z
                                ),
                                toolkitProperties
                        );
                    }
                }
            }
        }
    }

    private void perform(
            int x1,
            int y1,
            int z1,
            BlockState block1,
            int x2,
            int y2,
            int z2,
            BlockState block2,
            ToolkitProperties toolkitProperties
    ) {
        if (toolkitProperties.isVoxelListContains(getBlock(x1, y1, z1))) {
            setBlockType(x2, y2, z2, getBlockType(x1, y1, z1));
            setBlockData(x2, clampY(y2), z2, clampY(x1, y1, z1));
        }
    }

    private void selectExtrudeMethod(Snipe snipe, @Nullable Direction blockFace, boolean towardsUser) {
        ToolkitProperties toolkitProperties = snipe.getToolkitProperties();
        if (blockFace == null || toolkitProperties.getVoxelHeight() == 0) {
            return;
        }
        switch (blockFace) {
            case UP:
                extrudeUpOrDown(snipe, towardsUser);
                break;
            case SOUTH:
                extrudeNorthOrSouth(snipe, towardsUser);
                break;
            case EAST:
                extrudeEastOrWest(snipe, towardsUser);
                break;
            default:
                break;
        }
    }

    @Override
    public void handleArrowAction(Snipe snipe) {
        BlockVector3 targetBlock = getTargetBlock();
        BlockVector3 lastBlock = getLastBlock();
        selectExtrudeMethod(snipe, getDirection(targetBlock, lastBlock), false);
    }

    @Override
    public void handleGunpowderAction(Snipe snipe) {
        BlockVector3 targetBlock = getTargetBlock();
        BlockVector3 lastBlock = getLastBlock();
        selectExtrudeMethod(snipe, getDirection(targetBlock, lastBlock), true);
    }

    @Override
    public void sendInfo(Snipe snipe) {
        SnipeMessenger messenger = snipe.createMessenger();
        messenger.sendBrushNameMessage();
        messenger.sendBrushSizeMessage();
        messenger.sendVoxelHeightMessage();
        messenger.sendVoxelListMessage();
        messenger.sendMessage(ChatColor.AQUA + (Double.compare(this.trueCircle, 0.5) == 0
                ? "True circle mode ON"
                : "True circle mode OFF"));
    }

}

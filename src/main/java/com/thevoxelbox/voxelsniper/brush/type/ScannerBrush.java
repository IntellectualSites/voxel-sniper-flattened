package com.thevoxelbox.voxelsniper.brush.type;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.util.Direction;
import com.sk89q.worldedit.world.block.BlockType;
import com.sk89q.worldedit.world.block.BlockTypes;
import com.thevoxelbox.voxelsniper.sniper.snipe.Snipe;
import com.thevoxelbox.voxelsniper.sniper.snipe.message.SnipeMessenger;
import com.thevoxelbox.voxelsniper.sniper.toolkit.ToolkitProperties;
import com.thevoxelbox.voxelsniper.util.text.NumericParser;
import org.bukkit.ChatColor;

public class ScannerBrush extends AbstractBrush {

    private static final int DEPTH_MIN = 1;
    private static final int DEPTH_DEFAULT = 24;
    private static final int DEPTH_MAX = 64;

    private int depth = DEPTH_DEFAULT;
    private BlockType checkFor = BlockTypes.AIR;

    @Override
    public void handleCommand(String[] parameters, Snipe snipe) {
        SnipeMessenger messenger = snipe.createMessenger();
        for (String parameter : parameters) {
            if (parameter.equalsIgnoreCase("info")) {
                messenger.sendMessage(ChatColor.GOLD + "Scanner brush Parameters:");
                messenger.sendMessage(ChatColor.AQUA + "/b sc d# -- will set the search depth to #. Clamps to 1 - 64.");
                return;
            }
            if (!parameter.isEmpty() && parameter.charAt(0) == 'd') {
                Integer depth = NumericParser.parseInteger(parameter.substring(1));
                if (depth == null) {
                    messenger.sendMessage(ChatColor.RED + "Depth is not a number.");
                    return;
                }
                this.depth = depth < DEPTH_MIN ? DEPTH_MIN : Math.min(depth, DEPTH_MAX);
                messenger.sendMessage(ChatColor.AQUA + "Scanner depth set to " + this.depth);
            } else {
                messenger.sendMessage(ChatColor.RED + "Invalid brush parameters! use the info parameter to display parameter info.");
            }
        }
    }

    @Override
    public void handleArrowAction(Snipe snipe) {
        ToolkitProperties toolkitProperties = snipe.getToolkitProperties();
        this.checkFor = toolkitProperties.getBlockType();
        BlockVector3 targetBlock = getTargetBlock();
        BlockVector3 lastBlock = getLastBlock();
        Direction face = getDirection(targetBlock, lastBlock);
        if (face == null) {
            return;
        }
        scan(snipe, face);
    }

    @Override
    public void handleGunpowderAction(Snipe snipe) {
        ToolkitProperties toolkitProperties = snipe.getToolkitProperties();
        this.checkFor = toolkitProperties.getBlockType();
        BlockVector3 targetBlock = getTargetBlock();
        BlockVector3 lastBlock = getLastBlock();
        Direction face = getDirection(targetBlock, lastBlock);
        if (face == null) {
            return;
        }
        scan(snipe, face);
    }

    private void scan(Snipe snipe, Direction blockFace) {
        SnipeMessenger messenger = snipe.createMessenger();
        BlockVector3 targetBlock = getTargetBlock();
        if (blockFace == Direction.NORTH) {// Scan south
            for (int i = 1; i < this.depth + 1; i++) {
                if (getBlockType(targetBlock.getX() + i, clampY(targetBlock.getY()), targetBlock.getZ()) == this.checkFor) {
                    messenger.sendMessage(ChatColor.GREEN + String.valueOf(this.checkFor) + " found after " + i + " blocks.");
                    return;
                }
            }
            messenger.sendMessage(ChatColor.GRAY + "Nope.");
        } else if (blockFace == Direction.SOUTH) {// Scan north
            for (int i = 1; i < this.depth + 1; i++) {
                if (getBlockType(targetBlock.getX() - i, clampY(targetBlock.getY()), targetBlock.getZ()) == this.checkFor) {
                    messenger.sendMessage(ChatColor.GREEN + String.valueOf(this.checkFor) + " found after " + i + " blocks.");
                    return;
                }
            }
            messenger.sendMessage(ChatColor.GRAY + "Nope.");
        } else if (blockFace == Direction.EAST) {// Scan west
            for (int i = 1; i < this.depth + 1; i++) {
                if (getBlockType(targetBlock.getX(), clampY(targetBlock.getY()), targetBlock.getZ() + i) == this.checkFor) {
                    messenger.sendMessage(ChatColor.GREEN + String.valueOf(this.checkFor) + " found after " + i + " blocks.");
                    return;
                }
            }
            messenger.sendMessage(ChatColor.GRAY + "Nope.");
        } else if (blockFace == Direction.WEST) {// Scan east
            for (int i = 1; i < this.depth + 1; i++) {
                if (getBlockType(targetBlock.getX(), clampY(targetBlock.getY()), targetBlock.getZ() - i) == this.checkFor) {
                    messenger.sendMessage(ChatColor.GREEN + String.valueOf(this.checkFor) + " found after " + i + " blocks.");
                    return;
                }
            }
            messenger.sendMessage(ChatColor.GRAY + "Nope.");
        } else if (blockFace == Direction.UP) {// Scan down
            for (int i = 1; i < this.depth + 1; i++) {
                if ((targetBlock.getY() - i) <= 0) {
                    break;
                }
                if (getBlockType(targetBlock.getX(), clampY(targetBlock.getY() - i), targetBlock.getZ()) == this.checkFor) {
                    messenger.sendMessage(ChatColor.GREEN + String.valueOf(this.checkFor) + " found after " + i + " blocks.");
                    return;
                }
            }
            messenger.sendMessage(ChatColor.GRAY + "Nope.");
        } else if (blockFace == Direction.DOWN) {// Scan up
            for (int i = 1; i < this.depth + 1; i++) {
                EditSession editSession = getEditSession();//FAWE modified
                if ((targetBlock.getY() + i) >= editSession.getMaxY() + 1) {
                    break;
                }
                if (getBlockType(targetBlock.getX(), clampY(targetBlock.getY() + i), targetBlock.getZ()) == this.checkFor) {
                    messenger.sendMessage(ChatColor.GREEN + String.valueOf(this.checkFor) + " found after " + i + " blocks.");
                    return;
                }
            }
            messenger.sendMessage(ChatColor.GRAY + "Nope.");
        }
    }

    @Override
    public void sendInfo(Snipe snipe) {
        SnipeMessenger messenger = snipe.createMessenger();
        messenger.sendBrushNameMessage();
        messenger.sendMessage(ChatColor.GREEN + "Scanner depth set to " + this.depth);
        messenger.sendMessage(ChatColor.GREEN + "Scanner scans for " + this.checkFor + " (change with /v #)");
    }

}

package com.thevoxelbox.voxelsniper.brush.type.performer;

import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.util.Direction;
import com.thevoxelbox.voxelsniper.sniper.snipe.Snipe;
import com.thevoxelbox.voxelsniper.sniper.snipe.message.SnipeMessageSender;
import com.thevoxelbox.voxelsniper.sniper.snipe.message.SnipeMessenger;
import com.thevoxelbox.voxelsniper.util.text.NumericParser;
import org.bukkit.ChatColor;

public class EllipseBrush extends AbstractPerformerBrush {

    private static final double TWO_PI = (2 * Math.PI);
    private static final int SCL_MIN = 1;
    private static final int SCL_MAX = 9999;
    private static final int SCL_DEFAULT = 10;
    private static final int STEPS_MIN = 1;
    private static final int STEPS_MAX = 2000;
    private static final int STEPS_DEFAULT = 200;

    private int xscl;
    private int yscl;
    private int steps;
    private double stepSize;
    private boolean fill;

    @Override
    public void handleCommand(String[] parameters, Snipe snipe) {
        SnipeMessenger messenger = snipe.createMessenger();
        for (String parameter : parameters) {
            if (parameter.equalsIgnoreCase("info")) {
                messenger.sendMessage(ChatColor.GOLD + "Ellipse brush parameters");
                messenger.sendMessage(ChatColor.AQUA + "x[n]: Set X size modifier to n");
                messenger.sendMessage(ChatColor.AQUA + "y[n]: Set Y size modifier to n");
                messenger.sendMessage(ChatColor.AQUA + "t[n]: Set the amount of time steps");
                messenger.sendMessage(ChatColor.AQUA + "fill: Toggles fill mode");
                return;
            } else if (!parameter.isEmpty() && parameter.charAt(0) == 'x') {
                Integer tempXScale = NumericParser.parseInteger(parameter.replace("x", ""));
                if (tempXScale == null) {
                    messenger.sendMessage(ChatColor.RED + "Incorrect parameter \"" + parameter + "\"; use the \"info\" parameter.");
                    return;
                }
                if (tempXScale < SCL_MIN || tempXScale > SCL_MAX) {
                    messenger.sendMessage(ChatColor.AQUA + "Invalid X scale (" + SCL_MIN + "-" + SCL_MAX + ")");
                    continue;
                }
                this.xscl = tempXScale;
                messenger.sendMessage(ChatColor.AQUA + "X-scale modifier set to: " + this.xscl);
            } else if (!parameter.isEmpty() && parameter.charAt(0) == 'y') {
                Integer tempYScale = NumericParser.parseInteger(parameter.replace("y", ""));
                if (tempYScale == null) {
                    messenger.sendMessage(ChatColor.RED + "Incorrect parameter \"" + parameter + "\"; use the \"info\" parameter.");
                    return;
                }
                if (tempYScale < SCL_MIN || tempYScale > SCL_MAX) {
                    messenger.sendMessage(ChatColor.AQUA + "Invalid Y scale (" + SCL_MIN + "-" + SCL_MAX + ")");
                    continue;
                }
                this.yscl = tempYScale;
                messenger.sendMessage(ChatColor.AQUA + "Y-scale modifier set to: " + this.yscl);
            } else if (!parameter.isEmpty() && parameter.charAt(0) == 't') {
                Integer tempSteps = NumericParser.parseInteger(parameter.replace("t", ""));
                if (tempSteps == null) {
                    messenger.sendMessage(ChatColor.RED + "Incorrect parameter \"" + parameter + "\"; use the \"info\" parameter.");
                    return;
                }
                if (tempSteps < STEPS_MIN || tempSteps > STEPS_MAX) {
                    messenger.sendMessage(ChatColor.AQUA + "Invalid step number (" + STEPS_MIN + "-" + STEPS_MAX + ")");
                    continue;
                }
                this.steps = tempSteps;
                messenger.sendMessage(ChatColor.AQUA + "Render step number set to: " + this.steps);
            } else if (parameter.equalsIgnoreCase("fill")) {
                if (this.fill) {
                    this.fill = false;
                    messenger.sendMessage(ChatColor.AQUA + "Fill mode is disabled");
                } else {
                    this.fill = true;
                    messenger.sendMessage(ChatColor.AQUA + "Fill mode is enabled");
                }
            } else {
                messenger.sendMessage(ChatColor.RED + "Invalid brush parameters! Use the \"info\" parameter to display parameter info.");
            }
        }
    }

    @Override
    public void handleArrowAction(Snipe snipe) {
        BlockVector3 targetBlock = getTargetBlock();
        execute(snipe, targetBlock);
    }

    @Override
    public void handleGunpowderAction(Snipe snipe) {
        BlockVector3 lastBlock = getLastBlock();
        execute(snipe, lastBlock);
    }

    private void execute(Snipe snipe, BlockVector3 targetBlock) {
        this.stepSize = TWO_PI / this.steps;
        if (this.fill) {
            ellipseFill(snipe, targetBlock);
        } else {
            ellipse(snipe, targetBlock);
        }
    }

    private void ellipse(Snipe snipe, BlockVector3 targetBlock) {
        int blockX = targetBlock.getX();
        int blockY = targetBlock.getY();
        int blockZ = targetBlock.getZ();
        try {
            for (double steps = 0; (steps <= TWO_PI); steps += this.stepSize) {
                int x = (int) Math.round(this.xscl * Math.cos(steps));
                int y = (int) Math.round(this.yscl * Math.sin(steps));
                BlockVector3 lastBlock = getLastBlock();
                Direction face = getDirection(getTargetBlock(), lastBlock);
                if (face != null) {
                    switch (face) {
                        case NORTH:
                        case SOUTH:
                            this.performer.perform(
                                    getEditSession(),
                                    blockX,
                                    blockY + x,
                                    blockZ + y,
                                    getBlock(blockX, blockY + x, blockZ + y)
                            );
                            break;
                        case EAST:
                        case WEST:
                            this.performer.perform(
                                    getEditSession(),
                                    blockX + x,
                                    blockY + y,
                                    blockZ,
                                    getBlock(blockX + x, blockY + y, blockZ)
                            );
                            break;
                        case UP:
                        case DOWN:
                            this.performer.perform(
                                    getEditSession(),
                                    blockX + x,
                                    blockY,
                                    blockZ + y,
                                    getBlock(blockX + x, blockY, blockZ + y)
                            );
                            break;
                        default:
                            break;
                    }
                }
                if (steps >= TWO_PI) {
                    break;
                }
            }
        } catch (RuntimeException exception) {
            SnipeMessenger messenger = snipe.createMessenger();
            messenger.sendMessage(ChatColor.RED + "Invalid target.");
        }
    }

    private void ellipseFill(Snipe snipe, BlockVector3 targetBlock) {
        int ix = this.xscl;
        int iy = this.yscl;
        int blockX = targetBlock.getX();
        int blockY = targetBlock.getY();
        int blockZ = targetBlock.getZ();
        this.performer.perform(getEditSession(), blockX, blockY, blockZ, getBlock(blockX, blockY, blockZ));
        try {
            if (ix >= iy) { // Need this unless you want weird holes
                for (iy = this.yscl; iy > 0; iy--) {
                    for (double steps = 0; (steps <= TWO_PI); steps += this.stepSize) {
                        int x = (int) Math.round(ix * Math.cos(steps));
                        int y = (int) Math.round(iy * Math.sin(steps));
                        BlockVector3 lastBlock = getLastBlock();
                        Direction face = getDirection(getTargetBlock(), lastBlock);
                        if (face != null) {
                            switch (face) {
                                case NORTH:
                                case SOUTH:
                                    this.performer.perform(
                                            getEditSession(),
                                            blockX,
                                            blockY + x,
                                            blockZ + y,
                                            getBlock(blockX, blockY + x, blockZ + y)
                                    );
                                    break;
                                case EAST:
                                case WEST:
                                    this.performer.perform(
                                            getEditSession(),
                                            blockX + x,
                                            blockY + y,
                                            blockZ,
                                            getBlock(blockX + x, blockY + y, blockZ)
                                    );
                                    break;
                                case UP:
                                case DOWN:
                                    this.performer.perform(
                                            getEditSession(),
                                            blockX + x,
                                            blockY,
                                            blockZ + y,
                                            getBlock(blockX + x, blockY, blockZ + y)
                                    );
                                    break;
                                default:
                                    break;
                            }
                        }
                        if (steps >= TWO_PI) {
                            break;
                        }
                    }
                    ix--;
                }
            } else {
                for (ix = this.xscl; ix > 0; ix--) {
                    for (double steps = 0; (steps <= TWO_PI); steps += this.stepSize) {
                        int x = (int) Math.round(ix * Math.cos(steps));
                        int y = (int) Math.round(iy * Math.sin(steps));
                        BlockVector3 lastBlock = getLastBlock();
                        Direction face = getDirection(getTargetBlock(), lastBlock);
                        if (face != null) {
                            switch (face) {
                                case NORTH:
                                case SOUTH:
                                    this.performer.perform(
                                            getEditSession(),
                                            blockX,
                                            blockY + x,
                                            blockZ + y,
                                            getBlock(blockX, blockY + x, blockZ + y)
                                    );
                                    break;
                                case EAST:
                                case WEST:
                                    this.performer.perform(
                                            getEditSession(),
                                            blockX + x,
                                            blockY + y,
                                            blockZ,
                                            getBlock(blockX + x, blockY + y, blockZ)
                                    );
                                    break;
                                case UP:
                                case DOWN:
                                    this.performer.perform(
                                            getEditSession(),
                                            blockX + x,
                                            blockY,
                                            blockZ + y,
                                            getBlock(blockX + x, blockY, blockZ + y)
                                    );
                                    break;
                                default:
                                    break;
                            }
                        }
                        if (steps >= TWO_PI) {
                            break;
                        }
                    }
                    iy--;
                }
            }
        } catch (RuntimeException exception) {
            SnipeMessenger messenger = snipe.createMessenger();
            messenger.sendMessage(ChatColor.RED + "Invalid target.");
        }
    }

    @Override
    public void sendInfo(Snipe snipe) {
        if (this.xscl < SCL_MIN || this.xscl > SCL_MAX) {
            this.xscl = SCL_DEFAULT;
        }
        if (this.yscl < SCL_MIN || this.yscl > SCL_MAX) {
            this.yscl = SCL_DEFAULT;
        }
        if (this.steps < STEPS_MIN || this.steps > STEPS_MAX) {
            this.steps = STEPS_DEFAULT;
        }
        SnipeMessageSender messageSender = snipe.createMessageSender();
        messageSender.brushNameMessage()
                .message(ChatColor.AQUA + "X-size set to: " + ChatColor.DARK_AQUA + this.xscl)
                .message(ChatColor.AQUA + "Y-size set to: " + ChatColor.DARK_AQUA + this.yscl)
                .message(ChatColor.AQUA + "Render step number set to: " + ChatColor.DARK_AQUA + this.steps)
                .message(ChatColor.AQUA + "Fill mode is " + (this.fill ? "enabled" : "disabled"))
                .send();
    }

}

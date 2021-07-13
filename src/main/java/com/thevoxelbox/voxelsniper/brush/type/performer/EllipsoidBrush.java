package com.thevoxelbox.voxelsniper.brush.type.performer;

import com.sk89q.worldedit.math.BlockVector3;
import com.thevoxelbox.voxelsniper.sniper.snipe.Snipe;
import com.thevoxelbox.voxelsniper.sniper.snipe.message.SnipeMessenger;
import org.bukkit.ChatColor;

public class EllipsoidBrush extends AbstractPerformerBrush {

    private double xRad;
    private double yRad;
    private double zRad;
    private boolean istrue;

    @Override
    public void handleCommand(String[] parameters, Snipe snipe) {
        this.istrue = false;
        SnipeMessenger messenger = snipe.createMessenger();
        for (String parameter : parameters) {
            try {
                if (parameter.equalsIgnoreCase("info")) {
                    messenger.sendMessage(ChatColor.GOLD + "Ellipse brush parameters");
                    messenger.sendMessage(ChatColor.AQUA + "x[n]: Set X radius to n");
                    messenger.sendMessage(ChatColor.AQUA + "y[n]: Set Y radius to n");
                    messenger.sendMessage(ChatColor.AQUA + "z[n]: Set Z radius to n");
                    return;
                } else if (!parameter.isEmpty() && parameter.charAt(0) == 'x') {
                    this.xRad = Integer.parseInt(parameter.replace("x", ""));
                    messenger.sendMessage(ChatColor.AQUA + "X radius set to: " + this.xRad);
                } else if (!parameter.isEmpty() && parameter.charAt(0) == 'y') {
                    this.yRad = Integer.parseInt(parameter.replace("y", ""));
                    messenger.sendMessage(ChatColor.AQUA + "Y radius set to: " + this.yRad);
                } else if (!parameter.isEmpty() && parameter.charAt(0) == 'z') {
                    this.zRad = Integer.parseInt(parameter.replace("z", ""));
                    messenger.sendMessage(ChatColor.AQUA + "Z radius set to: " + this.zRad);
                } else if (parameter.equalsIgnoreCase("true")) {
                    this.istrue = true;
                } else {
                    messenger.sendMessage(ChatColor.RED + "Invalid brush parameters! Use the \"info\" parameter to display parameter info.");
                }
            } catch (NumberFormatException exception) {
                messenger.sendMessage(ChatColor.RED + "Incorrect parameter \"" + parameter + "\"; use the \"info\" parameter.");
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
        int blockX = targetBlock.getX();
        int blockY = targetBlock.getY();
        int blockZ = targetBlock.getZ();
        this.performer.perform(getEditSession(), blockX, blockY, blockZ, getBlock(blockX, blockY, blockZ));
        double trueOffset = this.istrue ? 0.5 : 0;
        for (double x = 0; x <= this.xRad; x++) {
            double xSquared = (x / (this.xRad + trueOffset)) * (x / (this.xRad + trueOffset));
            for (double z = 0; z <= this.zRad; z++) {
                double zSquared = (z / (this.zRad + trueOffset)) * (z / (this.zRad + trueOffset));
                for (double y = 0; y <= this.yRad; y++) {
                    double ySquared = (y / (this.yRad + trueOffset)) * (y / (this.yRad + trueOffset));
                    if (xSquared + ySquared + zSquared <= 1) {
                        this.performer.perform(
                                getEditSession(),
                                (int) (blockX + x),
                                clampY((int) (blockY + y)),
                                (int) (blockZ + z),
                                clampY((int) (blockX + x), (int) (blockY + y), (int) (blockZ + z))
                        );
                        this.performer.perform(
                                getEditSession(),
                                (int) (blockX + x),
                                clampY((int) (blockY + y)),
                                (int) (blockZ - z),
                                clampY((int) (blockX + x), (int) (blockY + y), (int) (blockZ - z))
                        );
                        this.performer.perform(
                                getEditSession(),
                                (int) (blockX + x),
                                clampY((int) (blockY - y)),
                                (int) (blockZ + z),
                                clampY((int) (blockX + x), (int) (blockY - y), (int) (blockZ + z))
                        );
                        this.performer.perform(
                                getEditSession(),
                                (int) (blockX + x),
                                clampY((int) (blockY - y)),
                                (int) (blockZ - z),
                                clampY((int) (blockX + x), (int) (blockY - y), (int) (blockZ - z))
                        );
                        this.performer.perform(
                                getEditSession(),
                                (int) (blockX - x),
                                clampY((int) (blockY + y)),
                                (int) (blockZ + z),
                                clampY((int) (blockX - x), (int) (blockY + y), (int) (blockZ + z))
                        );
                        this.performer.perform(
                                getEditSession(),
                                (int) (blockX - x),
                                clampY((int) (blockY + y)),
                                (int) (blockZ - z),
                                clampY((int) (blockX - x), (int) (blockY + y), (int) (blockZ - z))
                        );
                        this.performer.perform(
                                getEditSession(),
                                (int) (blockX - x),
                                clampY((int) (blockY - y)),
                                (int) (blockZ + z),
                                clampY((int) (blockX - x), (int) (blockY - y), (int) (blockZ + z))
                        );
                        this.performer.perform(
                                getEditSession(),
                                (int) (blockX - x),
                                clampY((int) (blockY - y)),
                                (int) (blockZ - z),
                                clampY((int) (blockX - x), (int) (blockY - y), (int) (blockZ - z))
                        );
                    }
                }
            }
        }
    }

    @Override
    public void sendInfo(Snipe snipe) {
        snipe.createMessageSender()
                .brushNameMessage()
                .message(ChatColor.AQUA + "X-size set to: " + ChatColor.DARK_AQUA + this.xRad)
                .message(ChatColor.AQUA + "Y-size set to: " + ChatColor.DARK_AQUA + this.yRad)
                .message(ChatColor.AQUA + "Z-size set to: " + ChatColor.DARK_AQUA + this.zRad)
                .send();
    }

}

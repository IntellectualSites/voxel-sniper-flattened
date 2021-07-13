package com.thevoxelbox.voxelsniper.brush.type.performer;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.math.BlockVector3;
import com.thevoxelbox.voxelsniper.sniper.snipe.Snipe;
import com.thevoxelbox.voxelsniper.sniper.snipe.message.SnipeMessenger;
import com.thevoxelbox.voxelsniper.sniper.toolkit.ToolkitProperties;
import com.thevoxelbox.voxelsniper.util.text.NumericParser;
import org.bukkit.ChatColor;

public class CylinderBrush extends AbstractPerformerBrush {

    private double trueCircle;

    @Override
    public void handleCommand(String[] parameters, Snipe snipe) {
        SnipeMessenger messenger = snipe.createMessenger();
        ToolkitProperties toolkitProperties = snipe.getToolkitProperties();
        for (String parameter : parameters) {
            if (parameter.equalsIgnoreCase("info")) {
                messenger.sendMessage(ChatColor.GOLD + "Cylinder Brush Parameters:");
                messenger.sendMessage(ChatColor.AQUA + "/b c h[number] -- set the cylinder v.voxelHeight.  Default is 1.");
                messenger.sendMessage(ChatColor.DARK_AQUA + "/b c true -- will use a true circle algorithm instead of the skinnier version with classic sniper nubs. /b b false will switch back. (false is default)");
                messenger.sendMessage(ChatColor.DARK_BLUE + "/b c c[number] -- set the origin of the cylinder compared to the target block. Positive numbers will move the cylinder upward, negative will move it downward.");
                return;
            }
            if (parameter.startsWith("true")) {
                this.trueCircle = 0.5;
                messenger.sendMessage(ChatColor.AQUA + "True circle mode ON.");
            } else if (parameter.startsWith("false")) {
                this.trueCircle = 0;
                messenger.sendMessage(ChatColor.AQUA + "True circle mode OFF.");
            } else if (!parameter.isEmpty() && parameter.charAt(0) == 'h') {
                Integer height = NumericParser.parseInteger(parameter.replace("h", ""));
                if (height == null) {
                    return;
                }
                toolkitProperties.setVoxelHeight(height);
                messenger.sendMessage(ChatColor.AQUA + "Cylinder v.voxelHeight set to: " + toolkitProperties.getVoxelHeight());
            } else if (!parameter.isEmpty() && parameter.charAt(0) == 'c') {
                Integer center = NumericParser.parseInteger(parameter.replace("c", ""));
                if (center == null) {
                    return;
                }
                toolkitProperties.setCylinderCenter(center);
                messenger.sendMessage(ChatColor.AQUA + "Cylinder origin set to: " + toolkitProperties.getCylinderCenter());
            } else {
                messenger.sendMessage(ChatColor.RED + "Invalid brush parameters! use the info parameter to display parameter info.");
            }
        }
    }

    @Override
    public void handleArrowAction(Snipe snipe) {
        BlockVector3 targetBlock = getTargetBlock();
        cylinder(snipe, targetBlock);
    }

    @Override
    public void handleGunpowderAction(Snipe snipe) {
        BlockVector3 lastBlock = getLastBlock();
        cylinder(snipe, lastBlock);
    }

    private void cylinder(Snipe snipe, BlockVector3 targetBlock) {
        ToolkitProperties toolkitProperties = snipe.getToolkitProperties();
        SnipeMessenger messenger = snipe.createMessenger();
        int brushSize = toolkitProperties.getBrushSize();
        int yStartingPoint = targetBlock.getY() + toolkitProperties.getCylinderCenter();
        int yEndPoint = targetBlock.getY() + toolkitProperties.getVoxelHeight() + toolkitProperties.getCylinderCenter();
        if (yEndPoint < yStartingPoint) {
            yEndPoint = yStartingPoint;
        }
        EditSession editSession = getEditSession();
        if (yStartingPoint < 0) {
            yStartingPoint = 0;
            messenger.sendMessage(ChatColor.DARK_PURPLE + "Warning: off-world start position.");
        } else if (yStartingPoint > editSession.getMaxY()) {
            yStartingPoint = editSession.getMaxY();
            messenger.sendMessage(ChatColor.DARK_PURPLE + "Warning: off-world start position.");
        }
        if (yEndPoint < 0) {
            yEndPoint = 0;
            messenger.sendMessage(ChatColor.DARK_PURPLE + "Warning: off-world end position.");
        } else if (yEndPoint > editSession.getMaxY()) {
            yEndPoint = editSession.getMaxY();
            messenger.sendMessage(ChatColor.DARK_PURPLE + "Warning: off-world end position.");
        }
        int blockX = targetBlock.getX();
        int blockZ = targetBlock.getZ();
        double bSquared = Math.pow(brushSize + this.trueCircle, 2);
        for (int y = yEndPoint; y >= yStartingPoint; y--) {
            for (int x = brushSize; x >= 0; x--) {
                double xSquared = Math.pow(x, 2);
                for (int z = brushSize; z >= 0; z--) {
                    if ((xSquared + Math.pow(z, 2)) <= bSquared) {
                        this.performer.perform(
                                getEditSession(),
                                blockX + x,
                                clampY(y),
                                blockZ + z,
                                this.clampY(blockX + x, y, blockZ + z)
                        );
                        this.performer.perform(
                                getEditSession(),
                                blockX + x,
                                clampY(y),
                                blockZ - z,
                                this.clampY(blockX + x, y, blockZ - z)
                        );
                        this.performer.perform(
                                getEditSession(),
                                blockX - x,
                                clampY(y),
                                blockZ + z,
                                this.clampY(blockX - x, y, blockZ + z)
                        );
                        this.performer.perform(
                                getEditSession(),
                                blockX - x,
                                clampY(y),
                                blockZ - z,
                                this.clampY(blockX - x, y, blockZ - z)
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
                .brushSizeMessage()
                .voxelHeightMessage()
                .cylinderCenterMessage()
                .send();
    }

}

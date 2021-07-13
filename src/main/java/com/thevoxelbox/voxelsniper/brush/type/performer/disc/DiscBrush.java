package com.thevoxelbox.voxelsniper.brush.type.performer.disc;

import com.sk89q.worldedit.math.BlockVector3;
import com.thevoxelbox.voxelsniper.brush.type.performer.AbstractPerformerBrush;
import com.thevoxelbox.voxelsniper.sniper.snipe.Snipe;
import com.thevoxelbox.voxelsniper.sniper.snipe.message.SnipeMessenger;
import com.thevoxelbox.voxelsniper.sniper.toolkit.ToolkitProperties;
import com.thevoxelbox.voxelsniper.util.Vectors;
import org.bukkit.ChatColor;
import org.bukkit.util.Vector;

public class DiscBrush extends AbstractPerformerBrush {

    private double trueCircle;

    @Override
    public void handleCommand(String[] parameters, Snipe snipe) {
        SnipeMessenger messenger = snipe.createMessenger();
        for (String s : parameters) {
            String parameter = s.toLowerCase();
            if (parameter.equalsIgnoreCase("info")) {
                messenger.sendMessage(ChatColor.GOLD + "Disc Brush Parameters:");
                messenger.sendMessage(ChatColor.AQUA + "/b d true|false" + " -- toggles useing the true circle algorithm instead of the skinnier version with classic sniper nubs. (false is default)");
                return;
            } else if (parameter.startsWith("true")) {
                this.trueCircle = 0.5;
                messenger.sendMessage(ChatColor.AQUA + "True circle mode ON.");
            } else if (parameter.startsWith("false")) {
                this.trueCircle = 0;
                messenger.sendMessage(ChatColor.AQUA + "True circle mode OFF.");
            } else {
                messenger.sendMessage(ChatColor.RED + "Invalid brush parameters! use the info parameter to display parameter info.");
            }
        }
    }

    @Override
    public void handleArrowAction(Snipe snipe) {
        BlockVector3 targetBlock = getTargetBlock();
        disc(snipe, targetBlock);
    }

    @Override
    public void handleGunpowderAction(Snipe snipe) {
        BlockVector3 lastBlock = getLastBlock();
        disc(snipe, lastBlock);
    }

    private void disc(Snipe snipe, BlockVector3 targetBlock) {
        ToolkitProperties toolkitProperties = snipe.getToolkitProperties();
        int brushSize = toolkitProperties.getBrushSize();
        double radiusSquared = (brushSize + this.trueCircle) * (brushSize + this.trueCircle);
        Vector centerPoint = Vectors.toBukkit(targetBlock);
        Vector currentPoint = new Vector().copy(centerPoint);
        for (int x = -brushSize; x <= brushSize; x++) {
            currentPoint.setX(centerPoint.getX() + x);
            for (int z = -brushSize; z <= brushSize; z++) {
                currentPoint.setZ(centerPoint.getZ() + z);
                if (centerPoint.distanceSquared(currentPoint) <= radiusSquared) {
                    this.performer.perform(
                            getEditSession(),
                            currentPoint.getBlockX(),
                            clampY(currentPoint.getBlockY()),
                            currentPoint.getBlockZ(),
                            clampY(currentPoint.getBlockX(), currentPoint.getBlockY(), currentPoint.getBlockZ())
                    );
                }
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

package com.thevoxelbox.voxelsniper.brush.type;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.world.block.BlockTypes;
import com.thevoxelbox.voxelsniper.sniper.snipe.Snipe;
import com.thevoxelbox.voxelsniper.sniper.snipe.message.SnipeMessenger;
import com.thevoxelbox.voxelsniper.sniper.toolkit.ToolkitProperties;
import com.thevoxelbox.voxelsniper.util.math.MathHelper;
import org.bukkit.ChatColor;
import org.bukkit.Material;

public class CleanSnowBrush extends AbstractBrush {

    private double trueCircle;

    @Override
    public final void handleCommand(String[] parameters, Snipe snipe) {
        SnipeMessenger messenger = snipe.createMessenger();
        for (String parameter : parameters) {
            if (parameter.equalsIgnoreCase("info")) {
                messenger.sendMessage(ChatColor.GOLD + "Clean Snow Brush Parameters:");
                messenger.sendMessage(ChatColor.AQUA + "/b cls true -- will use a true sphere algorithm instead of the skinnier version with classic sniper nubs. /b cls false will switch back. (false is default)");
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
        cleanSnow(snipe);
    }

    @Override
    public void handleGunpowderAction(Snipe snipe) {
        cleanSnow(snipe);
    }

    private void cleanSnow(Snipe snipe) {
        ToolkitProperties toolkitProperties = snipe.getToolkitProperties();
        int brushSize = toolkitProperties.getBrushSize();
        double brushSizeSquared = Math.pow(brushSize + this.trueCircle, 2);
        for (int y = (brushSize + 1) * 2; y >= 0; y--) {
            double ySquared = MathHelper.square(y - brushSize);
            for (int x = (brushSize + 1) * 2; x >= 0; x--) {
                double xSquared = MathHelper.square(x - brushSize);
                for (int z = (brushSize + 1) * 2; z >= 0; z--) {
                    if (xSquared + MathHelper.square(z - brushSize) + ySquared <= brushSizeSquared) {
                        BlockVector3 targetBlock = getTargetBlock();
                        int targetBlockX = targetBlock.getX();
                        int targetBlockY = targetBlock.getY();
                        int targetBlockZ = targetBlock.getZ();
                        if (BukkitAdapter.adapt(clampY(
                                targetBlockX + x - brushSize,
                                targetBlockY + z - brushSize,
                                targetBlockZ + y - brushSize
                        ).getBlockType()) == Material.SNOW && (BukkitAdapter.adapt(clampY(
                                targetBlockX + x - brushSize,
                                targetBlockY + z - brushSize - 1,
                                targetBlockZ + y - brushSize
                        ).getBlockType()) == Material.SNOW || clampY(
                                targetBlockX + x - brushSize,
                                targetBlockY + z - brushSize - 1,
                                targetBlockZ + y - brushSize
                        ).isAir())) {
                            setBlockData(
                                    targetBlockZ + y - brushSize,
                                    targetBlockX + x - brushSize,
                                    targetBlockY + z - brushSize,
                                    BlockTypes.AIR.getDefaultState()
                            );
                        }
                    }
                }
            }
        }
    }

    @Override
    public void sendInfo(Snipe snipe) {
        SnipeMessenger messenger = snipe.createMessenger();
        messenger.sendBrushNameMessage();
        messenger.sendBrushSizeMessage();
    }

}

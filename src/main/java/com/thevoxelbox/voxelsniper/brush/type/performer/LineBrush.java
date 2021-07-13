package com.thevoxelbox.voxelsniper.brush.type.performer;

import com.fastasyncworldedit.core.util.TaskManager;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.thevoxelbox.voxelsniper.sniper.snipe.Snipe;
import com.thevoxelbox.voxelsniper.sniper.snipe.message.SnipeMessenger;
import com.thevoxelbox.voxelsniper.util.Vectors;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.util.BlockIterator;
import org.bukkit.util.NumberConversions;
import org.bukkit.util.Vector;

public class LineBrush extends AbstractPerformerBrush {

    private static final Vector HALF_BLOCK_OFFSET = new Vector(0.5, 0.5, 0.5);
    private Vector originCoordinates;
    private Vector targetCoordinates = new Vector();
    private World targetWorld;

    @Override
    public void handleCommand(String[] parameters, Snipe snipe) {
        if (parameters[0].equalsIgnoreCase("info")) {
            SnipeMessenger messenger = snipe.createMessenger();
            messenger.sendMessage(ChatColor.GOLD + "Line Brush instructions: Right click first point with the arrow. Right click with powder to draw a line to set the second point.");
        }
    }

    @Override
    public void handleArrowAction(Snipe snipe) {
        BlockVector3 targetBlock = getTargetBlock();
        this.originCoordinates = Vectors.toBukkit(targetBlock);
        this.targetWorld = TaskManager.IMP.sync(() -> BukkitAdapter.adapt(getEditSession().getWorld()));
        SnipeMessenger messenger = snipe.createMessenger();
        messenger.sendMessage(ChatColor.DARK_PURPLE + "First point selected.");
    }

    @Override
    public void handleGunpowderAction(Snipe snipe) {
        BlockVector3 targetBlock = getTargetBlock();
        World world = BukkitAdapter.adapt(getEditSession().getWorld());
        if (this.originCoordinates == null || !world.equals(this.targetWorld)) {
            SnipeMessenger messenger = snipe.createMessenger();
            messenger.sendMessage(ChatColor.RED + "Warning: You did not select a first coordinate with the arrow");
        } else {
            this.targetCoordinates = Vectors.toBukkit(targetBlock);
            linePowder(snipe);
        }
    }

    private void linePowder(Snipe snipe) {
        Vector originClone = new Vector().
                copy(this.originCoordinates)
                .add(HALF_BLOCK_OFFSET);
        Vector targetClone = new Vector().
                copy(this.targetCoordinates)
                .add(HALF_BLOCK_OFFSET);
        Vector direction = new Vector().
                copy(targetClone)
                .subtract(originClone);
        double length = this.targetCoordinates.distance(this.originCoordinates);
        if (length == 0) {
            this.performer.perform(
                    getEditSession(),
                    targetCoordinates.getBlockX(),
                    targetCoordinates.getBlockY(),
                    targetCoordinates.getBlockZ(),
                    getBlock(targetCoordinates.getBlockX(), targetCoordinates.getBlockY(), targetCoordinates.getBlockZ())
            );
        } else {
            BlockIterator blockIterator = new BlockIterator(
                    this.targetWorld,
                    originClone,
                    direction,
                    0,
                    NumberConversions.round(length)
            );
            while (blockIterator.hasNext()) {
                Block currentBlock = blockIterator.next();
                this.performer.perform(
                        getEditSession(),
                        currentBlock.getX(),
                        currentBlock.getY(),
                        currentBlock.getZ(),
                        getBlock(currentBlock.getX(), currentBlock.getY(), currentBlock.getZ())
                );
            }
        }
    }

    @Override
    public void sendInfo(Snipe snipe) {
        SnipeMessenger messenger = snipe.createMessenger();
        messenger.sendBrushNameMessage();
    }

}

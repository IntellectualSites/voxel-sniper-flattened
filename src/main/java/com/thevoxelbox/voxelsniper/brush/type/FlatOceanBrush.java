package com.thevoxelbox.voxelsniper.brush.type;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.world.block.BlockTypes;
import com.thevoxelbox.voxelsniper.sniper.snipe.Snipe;
import com.thevoxelbox.voxelsniper.sniper.snipe.message.SnipeMessenger;
import org.bukkit.ChatColor;

public class FlatOceanBrush extends AbstractBrush {

    private static final int DEFAULT_WATER_LEVEL = 29;
    private static final int DEFAULT_FLOOR_LEVEL = 8;

    private int waterLevel = DEFAULT_WATER_LEVEL;
    private int floorLevel = DEFAULT_FLOOR_LEVEL;

    @Override
    public void handleCommand(String[] parameters, Snipe snipe) {
        SnipeMessenger messenger = snipe.createMessenger();
        for (String parameter : parameters) {
            if (parameter.equalsIgnoreCase("info")) {
                messenger.sendMessage(ChatColor.GREEN + "yo[number] to set the Level to which the water will rise.");
                messenger.sendMessage(ChatColor.GREEN + "yl[number] to set the Level to which the ocean floor will rise.");
            }
            if (parameter.startsWith("yo")) {
                int newWaterLevel = Integer.parseInt(parameter.replace("yo", ""));
                if (newWaterLevel < this.floorLevel) {
                    newWaterLevel = this.floorLevel + 1;
                }
                this.waterLevel = newWaterLevel;
                messenger.sendMessage(ChatColor.GREEN + "Water Level set to " + this.waterLevel);
            } else if (parameter.startsWith("yl")) {
                int newFloorLevel = Integer.parseInt(parameter.replace("yl", ""));
                if (newFloorLevel > this.waterLevel) {
                    newFloorLevel = this.waterLevel - 1;
                    if (newFloorLevel == 0) {
                        newFloorLevel = 1;
                        this.waterLevel = 2;
                    }
                }
                this.floorLevel = newFloorLevel;
                messenger.sendMessage(ChatColor.GREEN + "Ocean floor Level set to " + this.floorLevel);
            }
        }
    }

    @Override
    public void handleArrowAction(Snipe snipe) {
        flatOceanAtTarget();
    }

    @Override
    public void handleGunpowderAction(Snipe snipe) {
        flatOceanAtTarget();
        flatOceanAtTarget(CHUNK_SIZE, 0);
        flatOceanAtTarget(CHUNK_SIZE, CHUNK_SIZE);
        flatOceanAtTarget(0, CHUNK_SIZE);
        flatOceanAtTarget(-CHUNK_SIZE, CHUNK_SIZE);
        flatOceanAtTarget(-CHUNK_SIZE, 0);
        flatOceanAtTarget(-CHUNK_SIZE, -CHUNK_SIZE);
        flatOceanAtTarget(0, -CHUNK_SIZE);
        flatOceanAtTarget(CHUNK_SIZE, -CHUNK_SIZE);
    }

    private void flatOceanAtTarget(int additionalX, int additionalZ) {
        BlockVector3 targetBlock = getTargetBlock();
        int blockX = targetBlock.getX();
        int blockZ = targetBlock.getZ();
        flatOcean((blockX + additionalX) >> 4, (blockZ + additionalZ) >> 4);
    }

    private void flatOceanAtTarget() {
        BlockVector3 targetBlock = getTargetBlock();
        flatOcean(targetBlock.getX() >> 4, targetBlock.getZ() >> 4);
    }

    private void flatOcean(int chunkX, int chunkZ) {
        EditSession editSession = getEditSession();
        int blockX = chunkX << 4;
        int blockZ = chunkZ << 4;
        for (int x = 0; x < CHUNK_SIZE; x++) {
            for (int z = 0; z < CHUNK_SIZE; z++) {
                for (int y = 0; y < editSession.getMaxY() + 1; y++) {
                    if (y <= this.floorLevel) {
                        setBlockType(blockX + x, y, blockZ + z, BlockTypes.DIRT);
                    } else if (y <= this.waterLevel) {
                        setBlockType(blockX + x, y, blockZ + z, BlockTypes.WATER);
                    } else {
                        setBlockType(blockX + x, y, blockZ + z, BlockTypes.AIR);
                    }
                }
            }
        }
    }

    @Override
    public void sendInfo(Snipe snipe) {
        SnipeMessenger messenger = snipe.createMessenger();
        messenger.sendBrushNameMessage();
        messenger.sendMessage(ChatColor.GREEN + "Water level set to " + this.waterLevel);
        messenger.sendMessage(ChatColor.GREEN + "Ocean floor level set to " + this.floorLevel);
    }

}

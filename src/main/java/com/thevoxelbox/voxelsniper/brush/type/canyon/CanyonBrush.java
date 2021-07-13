package com.thevoxelbox.voxelsniper.brush.type.canyon;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.world.block.BlockType;
import com.sk89q.worldedit.world.block.BlockTypes;
import com.thevoxelbox.voxelsniper.brush.type.AbstractBrush;
import com.thevoxelbox.voxelsniper.sniper.snipe.Snipe;
import com.thevoxelbox.voxelsniper.sniper.snipe.message.SnipeMessenger;
import org.bukkit.ChatColor;

public class CanyonBrush extends AbstractBrush {

    private static final int SHIFT_LEVEL_MIN = 10;
    private static final int SHIFT_LEVEL_MAX = 60;
    private int yLevel = 10;

    @Override
    public void handleCommand(String[] parameters, Snipe snipe) {
        SnipeMessenger messenger = snipe.createMessenger();
        String firstParameter = parameters[0];
        if (firstParameter.equalsIgnoreCase("info")) {
            messenger.sendMessage(ChatColor.GREEN + "y[number] to set the Level to which the land will be shifted down");
        }
        if (!firstParameter.isEmpty() && firstParameter.charAt(0) == 'y') {
            int y = Integer.parseInt(firstParameter.replace("y", ""));
            if (y < SHIFT_LEVEL_MIN) {
                y = SHIFT_LEVEL_MIN;
            } else if (y > SHIFT_LEVEL_MAX) {
                y = SHIFT_LEVEL_MAX;
            }
            this.yLevel = y;
            messenger.sendMessage(ChatColor.GREEN + "Shift Level set to " + this.yLevel);
        }
    }

    @Override
    public void handleArrowAction(Snipe snipe) {
        BlockVector3 targetBlock = getTargetBlock();
        canyon(targetBlock.getX() >> 4, targetBlock.getZ() >> 4);
    }

    @Override
    public void handleGunpowderAction(Snipe snipe) {
        BlockVector3 targetBlock = getTargetBlock();
        int chunkX = targetBlock.getX() >> 4;
        int chunkZ = targetBlock.getZ() >> 4;
        for (int x = chunkX - 1; x <= chunkX + 1; x++) {
            for (int z = chunkZ - 1; z <= chunkX + 1; z++) {
                canyon(x, z);
            }
        }
    }

    protected void canyon(int chunkX, int chunkZ) {
        EditSession editSession = getEditSession();
        int blockX = chunkX << 4;
        int blockZ = chunkZ << 4;
        for (int x = 0; x < CHUNK_SIZE; x++) {
            for (int z = 0; z < CHUNK_SIZE; z++) {
                int currentYLevel = this.yLevel;
                for (int y = 63; y < editSession.getMaxY() + 1; y++) {
                    BlockType blockType = getBlockType(blockX + x, y, blockZ + z);
                    setBlockType(blockX + x, currentYLevel, blockZ + z, blockType);
                    setBlockType(blockX + x, y, blockZ + z, BlockTypes.AIR);
                    currentYLevel++;
                }
                setBlockType(blockX + x, 0, blockZ + z, BlockTypes.BEDROCK);
                for (int y = 1; y < SHIFT_LEVEL_MIN; y++) {
                    setBlockType(blockX + x, y, blockZ + z, BlockTypes.STONE);
                }
            }
        }
    }

    @Override
    public void sendInfo(Snipe snipe) {
        snipe.createMessageSender()
                .brushNameMessage()
                .message(ChatColor.GREEN + "Shift Level set to " + this.yLevel)
                .send();
    }

    public int getYLevel() {
        return this.yLevel;
    }

}

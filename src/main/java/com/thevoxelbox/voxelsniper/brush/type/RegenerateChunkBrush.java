package com.thevoxelbox.voxelsniper.brush.type;

import com.sk89q.worldedit.math.BlockVector3;
import com.thevoxelbox.voxelsniper.sniper.snipe.Snipe;
import com.thevoxelbox.voxelsniper.sniper.snipe.message.SnipeMessenger;
import org.bukkit.ChatColor;

/**
 * Regenerates the target chunk.
 */
public class RegenerateChunkBrush extends AbstractBrush {

    @Override
    public void handleArrowAction(Snipe snipe) {
        generateChunk(snipe);
    }

    @Override
    public void handleGunpowderAction(Snipe snipe) {
        generateChunk(snipe);
    }

    private void generateChunk(Snipe snipe) {
        BlockVector3 targetBlock = getTargetBlock();
        int chunkX = targetBlock.getX() >> 4;
        int chunkZ = targetBlock.getZ() >> 4;
        SnipeMessenger messenger = snipe.createMessenger();
        messenger.sendMessage("Generating that chunk! " + chunkX + " " + chunkZ);
        if (regenerateChunk(chunkX, chunkZ)) {
            messenger.sendMessage(ChatColor.GREEN + "Successfully generated that chunk! " + chunkX + " " + chunkZ);
        } else {
            messenger.sendMessage(ChatColor.RED + "Failed to generate that chunk! " + chunkX + " " + chunkZ);
        }
    }

    @Override
    public void sendInfo(Snipe snipe) {
        SnipeMessenger messenger = snipe.createMessenger();
        messenger.sendBrushNameMessage();
        messenger.sendMessage(ChatColor.LIGHT_PURPLE + "Tread lightly.");
        messenger.sendMessage(ChatColor.LIGHT_PURPLE + "This brush will melt your spleen and sell your kidneys.");
    }

}

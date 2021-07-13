package com.thevoxelbox.voxelsniper.brush.type.canyon;

import com.sk89q.worldedit.math.BlockVector3;
import com.thevoxelbox.voxelsniper.sniper.snipe.Snipe;
import com.thevoxelbox.voxelsniper.sniper.snipe.message.SnipeMessenger;
import org.bukkit.ChatColor;

public class CanyonSelectionBrush extends CanyonBrush {

    private boolean first = true;
    private int fx;
    private int fz;

    @Override
    public void handleArrowAction(Snipe snipe) {
        execute(snipe);
    }

    @Override
    public void handleGunpowderAction(Snipe snipe) {
        execute(snipe);
    }

    private void execute(Snipe snipe) {
        SnipeMessenger messenger = snipe.createMessenger();
        BlockVector3 targetBlock = getTargetBlock();
        int chunkX = targetBlock.getX() >> 4;
        int chunkZ = targetBlock.getZ() >> 4;
        if (this.first) {
            this.fx = chunkX;
            this.fz = chunkZ;
            messenger.sendMessage(ChatColor.YELLOW + "First point selected!");
        } else {
            messenger.sendMessage(ChatColor.YELLOW + "Second point selected!");
            selection(
                    Math.min(this.fx, chunkX),
                    Math.min(this.fz, chunkZ),
                    Math.max(this.fx, chunkX),
                    Math.max(this.fz, chunkZ)
            );
        }
        this.first = !this.first;
    }

    private void selection(int lowX, int lowZ, int highX, int highZ) {
        for (int x = lowX; x <= highX; x++) {
            for (int z = lowZ; z <= highZ; z++) {
                canyon(x, z);
            }
        }
    }

    @Override
    public void sendInfo(Snipe snipe) {
        snipe.createMessageSender()
                .brushNameMessage()
                .message(ChatColor.GREEN + "Shift Level set to " + this.getYLevel())
                .send();
    }

}

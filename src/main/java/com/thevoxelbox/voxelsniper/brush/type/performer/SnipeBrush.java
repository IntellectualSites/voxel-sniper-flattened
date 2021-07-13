package com.thevoxelbox.voxelsniper.brush.type.performer;

import com.sk89q.worldedit.math.BlockVector3;
import com.thevoxelbox.voxelsniper.sniper.snipe.Snipe;
import com.thevoxelbox.voxelsniper.sniper.snipe.message.SnipeMessenger;

public class SnipeBrush extends AbstractPerformerBrush {

    @Override
    public void handleArrowAction(Snipe snipe) {
        BlockVector3 targetBlock = getTargetBlock();
        this.performer.perform(
                getEditSession(),
                targetBlock.getX(),
                targetBlock.getY(),
                targetBlock.getZ(),
                getBlock(targetBlock.getX(), targetBlock.getY(), targetBlock.getZ())
        );
    }

    @Override
    public void handleGunpowderAction(Snipe snipe) {
        BlockVector3 lastBlock = getLastBlock();
        this.performer.perform(
                getEditSession(),
                lastBlock.getX(),
                lastBlock.getY(),
                lastBlock.getZ(),
                getBlock(lastBlock.getX(), lastBlock.getY(), lastBlock.getZ())
        );
    }

    @Override
    public void sendInfo(Snipe snipe) {
        SnipeMessenger messenger = snipe.createMessenger();
        messenger.sendBrushNameMessage();
    }

}

package com.thevoxelbox.voxelsniper.brush.type;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.math.BlockVector3;
import com.thevoxelbox.voxelsniper.brush.type.blend.BlendBallBrush;
import com.thevoxelbox.voxelsniper.sniper.snipe.Snipe;
import com.thevoxelbox.voxelsniper.sniper.toolkit.ToolAction;

public class ErodeBlendBrush extends AbstractBrush {

    private final BlendBallBrush blendBall;
    private final ErodeBrush erode;

    public ErodeBlendBrush() {
        this.blendBall = new BlendBallBrush();
        this.erode = new ErodeBrush();
    }

    @Override
    public void handleCommand(String[] parameters, Snipe snipe) {
        this.blendBall.handleCommand(parameters, snipe);
        this.erode.handleCommand(parameters, snipe);
    }

    @Override
    public void handleArrowAction(Snipe snipe) {
        EditSession editSession = getEditSession();
        BlockVector3 targetBlock = getTargetBlock();
        BlockVector3 lastBlock = getLastBlock();

        this.blendBall.setAirExcluded(false);
        this.blendBall.perform(snipe, ToolAction.ARROW, editSession, targetBlock, lastBlock);
        this.erode.perform(snipe, ToolAction.ARROW, editSession, targetBlock, lastBlock);
    }

    @Override
    public void handleGunpowderAction(Snipe snipe) {
        EditSession editSession = getEditSession();
        BlockVector3 targetBlock = getTargetBlock();
        BlockVector3 lastBlock = getLastBlock();

        this.blendBall.setAirExcluded(false);
        this.blendBall.perform(snipe, ToolAction.GUNPOWDER, editSession, targetBlock, lastBlock);
        this.erode.perform(snipe, ToolAction.GUNPOWDER, editSession, targetBlock, lastBlock);
    }

    @Override
    public void sendInfo(Snipe snipe) {
        this.blendBall.sendInfo(snipe);
        this.erode.sendInfo(snipe);
    }

}

package com.thevoxelbox.voxelsniper.performer.type.combo;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.world.block.BlockState;
import com.thevoxelbox.voxelsniper.performer.type.AbstractPerformer;
import com.thevoxelbox.voxelsniper.sniper.snipe.performer.PerformerSnipe;
import com.thevoxelbox.voxelsniper.sniper.toolkit.ToolkitProperties;

public class ComboNoPhysicsPerformer extends AbstractPerformer {

    private BlockState blockData;

    @Override
    public void initialize(PerformerSnipe snipe) {
        ToolkitProperties toolkitProperties = snipe.getToolkitProperties();
        this.blockData = toolkitProperties.getBlockData();
    }

    @Override
    public void perform(EditSession editSession, int x, int y, int z, BlockState block) {
        setBlockData(editSession, x, y, z, this.blockData);
    }

    @Override
    public void sendInfo(PerformerSnipe snipe) {
        snipe.createMessageSender()
                .performerNameMessage()
                .blockTypeMessage()
                .blockDataMessage()
                .send();
    }

}

package com.thevoxelbox.voxelsniper.performer;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.world.block.BlockState;
import com.thevoxelbox.voxelsniper.sniper.snipe.performer.PerformerSnipe;

public interface Performer {

    void initialize(PerformerSnipe snipe);

    void perform(EditSession editSession, int x, int y, int z, BlockState block);

    void sendInfo(PerformerSnipe snipe);

}

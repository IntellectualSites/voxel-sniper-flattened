package com.thevoxelbox.voxelsniper.util.painter;

import com.sk89q.worldedit.math.BlockVector3;

public interface Painter {

    void paint();

    BlockVector3 getCenter();

    BlockSetter getBlockSetter();

}

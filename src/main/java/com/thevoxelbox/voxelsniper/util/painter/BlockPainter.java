package com.thevoxelbox.voxelsniper.util.painter;

import com.sk89q.worldedit.math.BlockVector3;

import java.util.ArrayList;
import java.util.List;

public class BlockPainter implements Painter {

    private final BlockVector3 center;
    private final BlockSetter blockSetter;
    private final List<BlockVector3> shifts = new ArrayList<>();

    public BlockPainter(BlockVector3 center, BlockSetter blockSetter) {
        this.center = center;
        this.blockSetter = blockSetter;
    }

    public BlockPainter at(int xShift, int yShift, int zShift) {
        BlockVector3 shift = BlockVector3.at(xShift, yShift, zShift);
        return at(shift);
    }

    public BlockPainter at(BlockVector3 shift) {
        this.shifts.add(shift);
        return this;
    }

    @Override
    public void paint() {
        this.shifts.forEach(this::paintBlock);
    }

    private void paintBlock(BlockVector3 shift) {
        BlockVector3 position = this.center.add(shift);
        this.blockSetter.setBlockAt(position);
    }

    @Override
    public BlockVector3 getCenter() {
        return this.center;
    }

    @Override
    public BlockSetter getBlockSetter() {
        return this.blockSetter;
    }

}

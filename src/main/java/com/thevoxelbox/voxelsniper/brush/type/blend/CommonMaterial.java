package com.thevoxelbox.voxelsniper.brush.type.blend;

import com.sk89q.worldedit.world.block.BlockType;
import org.jetbrains.annotations.Nullable;

class CommonMaterial {

    @Nullable
    private BlockType type;
    private int frequency;

    @Nullable
    public BlockType getBlockType() {
        return this.type;
    }

    public void setBlockType(@Nullable BlockType type) {
        this.type = type;
    }

    public int getFrequency() {
        return this.frequency;
    }

    public void setFrequency(int frequency) {
        this.frequency = frequency;
    }

}

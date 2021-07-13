package com.thevoxelbox.voxelsniper.util.material;

import com.sk89q.worldedit.world.block.BlockType;

public final class Materials {

    private Materials() {
        throw new UnsupportedOperationException("Cannot create instance of this class");
    }

    public static boolean isEmpty(BlockType blockType) {
        return MaterialSets.AIRS.contains(blockType);
    }

    public static boolean isLiquid(BlockType blockType) {
        return MaterialSets.LIQUIDS.contains(blockType);
    }

}

package com.thevoxelbox.voxelsniper.util.material;

import com.sk89q.worldedit.world.block.BlockState;
import com.sk89q.worldedit.world.block.BlockType;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class MaterialSet implements Iterable<BlockType> {

    private final Set<BlockType> blockTypes;

    public MaterialSet(Collection<BlockType> blockTypes) {
        this.blockTypes = new HashSet<>(blockTypes);
    }

    public static MaterialSetBuilder builder() {
        return new MaterialSetBuilder();
    }

    public boolean contains(BlockState block) {
        BlockType type = block.getBlockType();
        return contains(type);
    }

    public boolean contains(BlockType blockType) {
        return this.blockTypes.contains(blockType);
    }

    @Override
    public Iterator<BlockType> iterator() {
        return this.blockTypes.iterator();
    }

    public Set<BlockType> getBlockTypes() {
        return Collections.unmodifiableSet(this.blockTypes);
    }

}

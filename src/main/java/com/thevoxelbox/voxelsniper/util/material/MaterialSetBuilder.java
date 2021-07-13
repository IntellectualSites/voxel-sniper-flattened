package com.thevoxelbox.voxelsniper.util.material;

import com.sk89q.worldedit.world.block.BlockCategories;
import com.sk89q.worldedit.world.block.BlockCategory;
import com.sk89q.worldedit.world.block.BlockType;
import com.sk89q.worldedit.world.block.BlockTypes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class MaterialSetBuilder {

    private final List<BlockType> blockTypes = new ArrayList<>(1);

    public MaterialSetBuilder add(BlockType blockType) {
        this.blockTypes.add(blockType);
        return this;
    }

    public MaterialSetBuilder add(String blockType) {
        BlockType optionalBlockType = BlockTypes.get(blockType.toLowerCase(Locale.ROOT));
        if (optionalBlockType != null) {
            this.blockTypes.add(optionalBlockType);
        }
        return this;
    }

    public MaterialSetBuilder with(BlockType... blockTypes) {
        List<BlockType> list = Arrays.asList(blockTypes);
        this.blockTypes.addAll(list);
        return this;
    }

    public MaterialSetBuilder with(Collection<BlockType> materials) {
        this.blockTypes.addAll(materials);
        return this;
    }

    public MaterialSetBuilder with(MaterialSet materialSet) {
        Set<BlockType> materials = materialSet.getBlockTypes();
        this.blockTypes.addAll(materials);
        return this;
    }

    public MaterialSetBuilder with(BlockCategory blockCategory) {
        Set<BlockType> blockTypes = blockCategory.getAll();
        this.blockTypes.addAll(blockTypes);
        return this;
    }

    public MaterialSetBuilder with(String blockCategory) {
        BlockCategory optionalBlockCategory = BlockCategories.get(blockCategory.toLowerCase(Locale.ROOT));
        if (optionalBlockCategory != null) {
            Set<BlockType> blockTypes = optionalBlockCategory.getAll();
            this.blockTypes.addAll(blockTypes);
        }
        return this;
    }

    public MaterialSet build() {
        return new MaterialSet(this.blockTypes);
    }

}

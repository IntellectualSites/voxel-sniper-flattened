package com.thevoxelbox.voxelsniper.util.painter.setter;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;

public class BlockDataSetterBuilder {

    private World world;
    private BlockData blockData;
    private boolean applyPhysics;

    public BlockDataSetterBuilder world(Block block) {
        World world = block.getWorld();
        return world(world);
    }

    public BlockDataSetterBuilder world(Location location) {
        World world = location.getWorld();
        return world(world);
    }

    public BlockDataSetterBuilder world(World world) {
        this.world = world;
        return this;
    }

    public BlockDataSetterBuilder blockData(Material material) {
        BlockData blockData = material.createBlockData();
        return blockData(blockData);
    }

    public BlockDataSetterBuilder blockData(BlockData blockData) {
        this.blockData = blockData;
        return this;
    }

    public BlockDataSetterBuilder applyPhysics() {
        this.applyPhysics = true;
        return this;
    }

    public BlockDataSetter build() {
        if (this.world == null) {
            throw new RuntimeException("World must be specified");
        }
        if (this.blockData == null) {
            throw new RuntimeException("Block data must be specified");
        }
        return new BlockDataSetter(this.world, this.blockData, this.applyPhysics);
    }

}

package com.thevoxelbox.voxelsniper.util.painter.setter;

import com.sk89q.worldedit.math.BlockVector3;
import com.thevoxelbox.voxelsniper.util.painter.BlockSetter;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;

public class BlockDataSetter implements BlockSetter {

    private final World world;
    private final BlockData blockData;
    private final boolean applyPhysics;

    public BlockDataSetter(World world, BlockData blockData, boolean applyPhysics) {
        this.world = world;
        this.blockData = blockData;
        this.applyPhysics = applyPhysics;
    }

    public static BlockDataSetterBuilder builder() {
        return new BlockDataSetterBuilder();
    }

    @Override
    public void setBlockAt(BlockVector3 position) {
        int x = position.getX();
        int y = position.getY();
        int z = position.getZ();
        Block block = this.world.getBlockAt(x, y, z);
        block.setBlockData(this.blockData, this.applyPhysics);
    }

    public World getWorld() {
        return this.world;
    }

    public BlockData getBlockData() {
        return this.blockData;
    }

    public boolean isApplyPhysics() {
        return this.applyPhysics;
    }

}

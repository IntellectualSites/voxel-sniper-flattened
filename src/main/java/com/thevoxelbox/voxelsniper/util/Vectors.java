package com.thevoxelbox.voxelsniper.util;

import com.sk89q.worldedit.math.BlockVector3;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.util.Vector;

public final class Vectors {

    private Vectors() {
        throw new UnsupportedOperationException("Cannot create an instance of this class");
    }

    public static BlockVector3 of(Block block) {
        return BlockVector3.at(block.getX(), block.getY(), block.getZ());
    }

    public static BlockVector3 of(Location location) {
        return BlockVector3.at(location.getBlockX(), location.getBlockY(), location.getBlockZ());
    }

    public static BlockVector3 of(Vector vector) {
        return BlockVector3.at(vector.getBlockX(), vector.getBlockY(), vector.getBlockZ());
    }

    public static Vector toBukkit(BlockVector3 vector) {
        return new Vector(vector.getX(), vector.getY(), vector.getZ());
    }

}

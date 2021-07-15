package com.thevoxelbox.voxelsniper.brush.type;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.util.Direction;
import com.sk89q.worldedit.util.TreeGenerator;
import com.sk89q.worldedit.world.RegenOptions;
import com.sk89q.worldedit.world.block.BaseBlock;
import com.sk89q.worldedit.world.block.BlockState;
import com.sk89q.worldedit.world.block.BlockType;
import com.thevoxelbox.voxelsniper.brush.Brush;
import com.thevoxelbox.voxelsniper.sniper.Sniper;
import com.thevoxelbox.voxelsniper.sniper.snipe.Snipe;
import com.thevoxelbox.voxelsniper.sniper.toolkit.ToolAction;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.entity.Player;

public abstract class AbstractBrush implements Brush {

    protected static final int CHUNK_SIZE = 16;

    private EditSession editSession;
    private BlockVector3 targetBlock;
    private BlockVector3 lastBlock;

    @Override
    public void handleCommand(String[] parameters, Snipe snipe) {
        Sniper sniper = snipe.getSniper();
        Player player = sniper.getPlayer();
        player.sendMessage(ChatColor.RED + "This brush does not accept additional parameters.");
    }

    @Override
    public void perform(
            Snipe snipe,
            ToolAction action,
            EditSession editSession,
            BlockVector3 targetBlock,
            BlockVector3 lastBlock
    ) {
        this.editSession = editSession;
        this.targetBlock = targetBlock;
        this.lastBlock = lastBlock;
        if (action == ToolAction.ARROW) {
            handleArrowAction(snipe);
        } else if (action == ToolAction.GUNPOWDER) {
            handleGunpowderAction(snipe);
        }
    }

    public int clampY(int y) {
        int clampedY = y;
        if (clampedY < 0) {
            clampedY = 0;
        } else {
            int maxHeight = editSession.getMaxY() + 1;
            if (clampedY > maxHeight) {
                clampedY = maxHeight;
            }
        }
        return clampedY;
    }

    public BlockState clampY(BlockVector3 position) {
        int x = position.getX();
        int y = position.getY();
        int z = position.getZ();
        return clampY(x, y, z);
    }

    public BlockState clampY(int x, int y, int z) {
        return getBlock(x, clampY(y), z);
    }

    public void setBiome(int x, int y, int z, Biome biome) {
        try {
            editSession.setBiome(x, y, z, BukkitAdapter.adapt(biome));
        } catch (WorldEditException e) {
            throw new RuntimeException(e);
        }
    }

    public int getHighestTerrainBlock(int x, int z, int minY, int maxY) {
        try {
            return editSession.getHighestTerrainBlock(x, z, minY, maxY);
        } catch (WorldEditException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean regenerateChunk(int chunkX, int chunkZ) {
        try {
            World world = BukkitAdapter.adapt(editSession.getWorld());
            int minX = chunkX << 4;
            int minZ = chunkZ << 4;
            RegenOptions regenOptions = RegenOptions.builder()
                    .seed(world.getSeed())
                    .regenBiomes(true)
                    .biomeType(null)
                    .build();
            return editSession.getWorld().regenerate(
                    new CuboidRegion(
                            editSession.getWorld(),
                            BlockVector3.at(minX, editSession.getMinY(), minZ),
                            BlockVector3.at(minX + 15, editSession.getMaxY(), minZ + 15)
                    ),
                    editSession,
                    regenOptions
            );
        } catch (WorldEditException e) {
            throw new RuntimeException(e);
        }
    }

    public void refreshChunk(int chunkX, int chunkZ) {
        try {
            editSession.getWorld().refreshChunk(chunkX, chunkZ);
        } catch (WorldEditException e) {
            throw new RuntimeException(e);
        }
    }

    public void generateTree(TreeGenerator.TreeType treeType, BlockVector3 location) {
        try {
            editSession.getWorld().generateTree(treeType, editSession, location);
        } catch (WorldEditException e) {
            throw new RuntimeException(e);
        }
    }

    public Direction getDirection(BlockVector3 first, BlockVector3 second) {
        Direction[] directions = Direction.values();
        for (Direction direction : directions) {
            if (first.getX() + direction.getX() == second.getX()
                    && first.getY() + direction.getY() == second.getY()
                    && first.getZ() + direction.getZ() == second.getZ()) {
                return direction;
            }
        }
        return null;
    }

    public BlockType getBlockType(BlockVector3 position) {
        int x = position.getX();
        int y = position.getY();
        int z = position.getZ();
        return getBlockType(x, y, z);
    }

    public BlockType getBlockType(int x, int y, int z) {
        BlockState block = getBlock(x, y, z);
        return block.getBlockType();
    }

    public void setBlockType(BlockVector3 position, BlockType type) {
        int x = position.getX();
        int y = position.getY();
        int z = position.getZ();
        setBlockType(x, y, z, type);
    }

    public void setBlockType(int x, int y, int z, BlockType type) {
        try {
            editSession.setBlock(x, y, z, type.getDefaultState());
        } catch (WorldEditException e) {
            throw new RuntimeException(e);
        }
    }

    public void setBlockData(BlockVector3 position, BlockState blockState) {
        int x = position.getX();
        int y = position.getY();
        int z = position.getZ();
        setBlockData(x, y, z, blockState);
    }

    public void setBlockData(int x, int y, int z, BlockState blockState) {
        try {
            editSession.setBlock(x, y, z, blockState);
        } catch (WorldEditException e) {
            throw new RuntimeException(e);
        }
    }

    public BaseBlock getFullBlock(BlockVector3 position) {
        int x = position.getX();
        int y = position.getY();
        int z = position.getZ();
        return getFullBlock(x, y, z);
    }

    public BaseBlock getFullBlock(int x, int y, int z) {
        return editSession.getFullBlock(x, y, z);
    }

    public BlockState getBlock(BlockVector3 position) {
        int x = position.getX();
        int y = position.getY();
        int z = position.getZ();
        return getBlock(x, y, z);
    }

    public BlockState getBlock(int x, int y, int z) {
        return editSession.getBlock(x, y, z);
    }

    public void setBlock(BlockVector3 position, BaseBlock block) {
        int x = position.getX();
        int y = position.getY();
        int z = position.getZ();
        setBlock(x, y, z, block);
    }

    public void setBlock(int x, int y, int z, BaseBlock block) {
        try {
            editSession.setBlock(x, y, z, block);
        } catch (WorldEditException e) {
            throw new RuntimeException(e);
        }
    }

    public EditSession getEditSession() {
        return editSession;
    }

    public BlockVector3 getTargetBlock() {
        return this.targetBlock;
    }

    public void setTargetBlock(BlockVector3 targetBlock) {
        this.targetBlock = targetBlock;
    }

    /**
     * @return Block before target Block.
     */
    public BlockVector3 getLastBlock() {
        return this.lastBlock;
    }

    public void setLastBlock(BlockVector3 lastBlock) {
        this.lastBlock = lastBlock;
    }

}

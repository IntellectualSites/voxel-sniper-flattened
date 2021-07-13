package com.thevoxelbox.voxelsniper.sniper.toolkit;

import com.sk89q.worldedit.world.block.BlockState;
import com.sk89q.worldedit.world.block.BlockType;
import com.sk89q.worldedit.world.block.BlockTypes;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ToolkitProperties {

    private static final BlockType DEFAULT_BLOCK_MATERIAL = BlockTypes.AIR;
    private static final BlockType DEFAULT_REPLACE_BLOCK_MATERIAL = BlockTypes.AIR;
    private static final int DEFAULT_BRUSH_SIZE = 3;
    private static final int DEFAULT_VOXEL_HEIGHT = 1;
    private static final int DEFAULT_CYLINDER_CENTER = 0;
    private final List<BlockState> voxelList = new ArrayList<>();
    private BlockState blockData;
    private BlockState replaceBlockData;
    private int brushSize;
    private int voxelHeight;
    private int cylinderCenter;
    @Nullable
    private Integer blockTracerRange;
    private boolean lightningEnabled;

    public ToolkitProperties() {
        this.blockData = DEFAULT_BLOCK_MATERIAL.getDefaultState();
        this.replaceBlockData = DEFAULT_REPLACE_BLOCK_MATERIAL.getDefaultState();
        this.brushSize = DEFAULT_BRUSH_SIZE;
        this.voxelHeight = DEFAULT_VOXEL_HEIGHT;
    }

    public void reset() {
        resetBlockData();
        resetReplaceBlockData();
        this.brushSize = DEFAULT_BRUSH_SIZE;
        this.voxelHeight = DEFAULT_VOXEL_HEIGHT;
        this.cylinderCenter = DEFAULT_CYLINDER_CENTER;
        this.blockTracerRange = null;
        this.lightningEnabled = false;
        this.voxelList.clear();
    }

    public void resetBlockData() {
        this.blockData = DEFAULT_BLOCK_MATERIAL.getDefaultState();
    }

    public void resetReplaceBlockData() {
        this.replaceBlockData = DEFAULT_REPLACE_BLOCK_MATERIAL.getDefaultState();
    }

    public BlockType getBlockType() {
        return this.blockData.getBlockType();
    }

    public void setBlockType(BlockType type) {
        this.blockData = type.getDefaultState();
    }

    public BlockType getReplaceBlockType() {
        return this.replaceBlockData.getBlockType();
    }

    public void setReplaceBlockType(BlockType type) {
        this.replaceBlockData = type.getDefaultState();
    }

    public BlockTracer createBlockTracer(Player player) {
        int distance = this.blockTracerRange == null
                ? Math.max(Bukkit.getViewDistance(), 3) * 16 - this.brushSize
                : this.blockTracerRange;
        return new BlockTracer(player, distance);
    }

    public void addToVoxelList(BlockState blockData) {
        this.voxelList.add(blockData);
    }

    public void removeFromVoxelList(BlockState blockData) {
        this.voxelList.remove(blockData);
    }

    public void clearVoxelList() {
        this.voxelList.clear();
    }

    public boolean isVoxelListContains(BlockState blockData) {
        return this.voxelList.contains(blockData);
    }

    public BlockState getBlockData() {
        return this.blockData;
    }

    public void setBlockData(BlockState blockData) {
        this.blockData = blockData;
    }

    public BlockState getReplaceBlockData() {
        return this.replaceBlockData;
    }

    public void setReplaceBlockData(BlockState replaceBlockData) {
        this.replaceBlockData = replaceBlockData;
    }

    public int getBrushSize() {
        return this.brushSize;
    }

    public void setBrushSize(int brushSize) {
        this.brushSize = brushSize;
    }

    public int getVoxelHeight() {
        return this.voxelHeight;
    }

    public void setVoxelHeight(int voxelHeight) {
        this.voxelHeight = voxelHeight;
    }

    public int getCylinderCenter() {
        return this.cylinderCenter;
    }

    public void setCylinderCenter(int cylinderCenter) {
        this.cylinderCenter = cylinderCenter;
    }

    @Nullable
    public Integer getBlockTracerRange() {
        return this.blockTracerRange;
    }

    public void setBlockTracerRange(@Nullable Integer blockTracerRange) {
        this.blockTracerRange = blockTracerRange;
    }

    public boolean isLightningEnabled() {
        return this.lightningEnabled;
    }

    public void setLightningEnabled(boolean lightningEnabled) {
        this.lightningEnabled = lightningEnabled;
    }

    public List<BlockState> getVoxelList() {
        return Collections.unmodifiableList(this.voxelList);
    }

}

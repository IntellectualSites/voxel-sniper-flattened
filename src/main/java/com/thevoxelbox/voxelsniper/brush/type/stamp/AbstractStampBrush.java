package com.thevoxelbox.voxelsniper.brush.type.stamp;

import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.world.block.BlockCategories;
import com.sk89q.worldedit.world.block.BlockState;
import com.sk89q.worldedit.world.block.BlockType;
import com.sk89q.worldedit.world.block.BlockTypes;
import com.thevoxelbox.voxelsniper.brush.type.AbstractBrush;
import com.thevoxelbox.voxelsniper.sniper.snipe.Snipe;
import com.thevoxelbox.voxelsniper.sniper.snipe.message.SnipeMessenger;
import com.thevoxelbox.voxelsniper.util.material.MaterialSet;
import com.thevoxelbox.voxelsniper.util.material.MaterialSets;
import com.thevoxelbox.voxelsniper.util.material.Materials;
import org.bukkit.ChatColor;

import java.util.HashSet;
import java.util.Set;

public abstract class AbstractStampBrush extends AbstractBrush {

    protected final Set<StampBrushBlockWrapper> clone = new HashSet<>();
    protected final Set<StampBrushBlockWrapper> fall = new HashSet<>();
    protected final Set<StampBrushBlockWrapper> drop = new HashSet<>();
    protected final Set<StampBrushBlockWrapper> solid = new HashSet<>();
    protected boolean sorted;
    protected StampType stamp = StampType.DEFAULT;

    @Override
    public void handleArrowAction(Snipe snipe) {
        switch (this.stamp) {
            case DEFAULT:
                stamp();
                break;
            case NO_AIR:
                stampNoAir();
                break;
            case FILL:
                stampFill();
                break;
            default:
                SnipeMessenger messenger = snipe.createMessenger();
                messenger.sendMessage(ChatColor.DARK_RED + "Error while stamping! Report");
                break;
        }
    }

    public void reSort() {
        this.sorted = false;
    }

    protected boolean falling(BlockType type) {
        return MaterialSets.FALLING.contains(type);
    }

    protected boolean fallsOff(BlockType type) {
        MaterialSet fallsOff = MaterialSet.builder()
                .with(BlockCategories.SAPLINGS)
                .with(BlockCategories.DOORS)
                .with(BlockCategories.RAILS)
                .with(BlockCategories.BUTTONS)
                .with(BlockCategories.SIGNS)
                .with(BlockCategories.PRESSURE_PLATES)
                .with(BlockCategories.FLOWERS)
                .with(MaterialSets.MUSHROOMS)
                .with(MaterialSets.TORCHES)
                .with(MaterialSets.REDSTONE_TORCHES)
                .add(BlockTypes.FIRE)
                .add(BlockTypes.REDSTONE_WIRE)
                .add(BlockTypes.WHEAT)
                .add(BlockTypes.LADDER)
                .add(BlockTypes.LEVER)
                .add(BlockTypes.SNOW)
                .add(BlockTypes.SUGAR_CANE)
                .add(BlockTypes.REPEATER)
                .add(BlockTypes.COMPARATOR)
                .build();
        return fallsOff.contains(type);
    }

    protected void setBlock(StampBrushBlockWrapper blockWrapper) {
        BlockVector3 targetBlock = getTargetBlock();
        clampY(
                targetBlock.getX() + blockWrapper.getX(),
                targetBlock.getY() + blockWrapper.getY(),
                targetBlock.getZ() + blockWrapper.getZ()
        );
        setBlockData(targetBlock.getX(), targetBlock.getY(), targetBlock.getZ(), blockWrapper.getBlockData());
    }

    protected void setBlockFill(StampBrushBlockWrapper blockWrapper) {
        BlockVector3 targetBlock = getTargetBlock();
        BlockState block = clampY(
                targetBlock.getX() + blockWrapper.getX(),
                targetBlock.getY() + blockWrapper.getY(),
                targetBlock.getZ() + blockWrapper.getZ()
        );
        if (block.isAir()) {
            setBlockData(targetBlock.getX(), targetBlock.getY(), targetBlock.getZ(), blockWrapper.getBlockData());
        }
    }

    protected void stamp() {
        if (this.sorted) {
            for (StampBrushBlockWrapper block : this.solid) {
                setBlock(block);
            }
            for (StampBrushBlockWrapper block : this.drop) {
                setBlock(block);
            }
            for (StampBrushBlockWrapper block : this.fall) {
                setBlock(block);
            }
        } else {
            this.fall.clear();
            this.drop.clear();
            this.solid.clear();
            for (StampBrushBlockWrapper block : this.clone) {
                BlockState blockData = block.getBlockData();
                BlockType type = blockData.getBlockType();
                if (this.fallsOff(type)) {
                    this.fall.add(block);
                } else if (this.falling(type)) {
                    this.drop.add(block);
                } else {
                    this.solid.add(block);
                    this.setBlock(block);
                }
            }
            for (StampBrushBlockWrapper block : this.drop) {
                this.setBlock(block);
            }
            for (StampBrushBlockWrapper block : this.fall) {
                this.setBlock(block);
            }
            this.sorted = true;
        }
    }

    protected void stampFill() {
        if (this.sorted) {
            for (StampBrushBlockWrapper block : this.solid) {
                this.setBlockFill(block);
            }
            for (StampBrushBlockWrapper block : this.drop) {
                this.setBlockFill(block);
            }
            for (StampBrushBlockWrapper block : this.fall) {
                this.setBlockFill(block);
            }
        } else {
            this.fall.clear();
            this.drop.clear();
            this.solid.clear();
            for (StampBrushBlockWrapper block : this.clone) {
                BlockState blockData = block.getBlockData();
                BlockType type = blockData.getBlockType();
                if (fallsOff(type)) {
                    this.fall.add(block);
                } else if (falling(type)) {
                    this.drop.add(block);
                } else if (!Materials.isEmpty(type)) {
                    this.solid.add(block);
                    this.setBlockFill(block);
                }
            }
            for (StampBrushBlockWrapper block : this.drop) {
                this.setBlockFill(block);
            }
            for (StampBrushBlockWrapper block : this.fall) {
                this.setBlockFill(block);
            }
            this.sorted = true;
        }
    }

    protected void stampNoAir() {
        if (this.sorted) {
            for (StampBrushBlockWrapper block : this.solid) {
                this.setBlock(block);
            }
            for (StampBrushBlockWrapper block : this.drop) {
                this.setBlock(block);
            }
            for (StampBrushBlockWrapper block : this.fall) {
                this.setBlock(block);
            }
        } else {
            this.fall.clear();
            this.drop.clear();
            this.solid.clear();
            for (StampBrushBlockWrapper block : this.clone) {
                BlockState blockData = block.getBlockData();
                BlockType type = blockData.getBlockType();
                if (this.fallsOff(type)) {
                    this.fall.add(block);
                } else if (this.falling(type)) {
                    this.drop.add(block);
                } else if (!Materials.isEmpty(type)) {
                    this.solid.add(block);
                    this.setBlock(block);
                }
            }
            for (StampBrushBlockWrapper block : this.drop) {
                this.setBlock(block);
            }
            for (StampBrushBlockWrapper block : this.fall) {
                this.setBlock(block);
            }
            this.sorted = true;
        }
    }

    public StampType getStamp() {
        return this.stamp;
    }

    public void setStamp(StampType stamp) {
        this.stamp = stamp;
    }

}

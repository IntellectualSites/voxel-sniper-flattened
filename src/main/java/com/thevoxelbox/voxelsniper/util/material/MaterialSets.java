package com.thevoxelbox.voxelsniper.util.material;

import com.sk89q.worldedit.world.block.BlockCategories;
import com.sk89q.worldedit.world.block.BlockTypes;

public final class MaterialSets {

    public static final MaterialSet AIRS = MaterialSet.builder()
            .add(BlockTypes.AIR)
            .add(BlockTypes.CAVE_AIR)
            .add(BlockTypes.VOID_AIR)
            .build();

    public static final MaterialSet CHESTS = MaterialSet.builder()
            .add(BlockTypes.CHEST)
            .add(BlockTypes.TRAPPED_CHEST)
            .add(BlockTypes.ENDER_CHEST)
            .build();

    public static final MaterialSet TORCHES = MaterialSet.builder()
            .add(BlockTypes.TORCH)
            .add(BlockTypes.WALL_TORCH)
            .add("SOUL_TORCH")
            .add("SOUL_WALL_TORCH")
            .build();

    public static final MaterialSet REDSTONE_TORCHES = MaterialSet.builder()
            .add(BlockTypes.REDSTONE_TORCH)
            .add(BlockTypes.REDSTONE_WALL_TORCH)
            .build();

    public static final MaterialSet MUSHROOMS = MaterialSet.builder()
            .add(BlockTypes.BROWN_MUSHROOM)
            .add(BlockTypes.RED_MUSHROOM)
            .build();

    public static final MaterialSet STEMS = MaterialSet.builder()
            .add(BlockTypes.ATTACHED_MELON_STEM)
            .add(BlockTypes.ATTACHED_PUMPKIN_STEM)
            .add(BlockTypes.MELON_STEM)
            .add(BlockTypes.PUMPKIN_STEM)
            .build();

    public static final MaterialSet FLORA = MaterialSet.builder()
            .with(BlockCategories.FLOWERS)
            .with(MUSHROOMS)
            .with(STEMS)
            .add(BlockTypes.GRASS)
            .add(BlockTypes.TALL_GRASS)
            .add(BlockTypes.DEAD_BUSH)
            .add(BlockTypes.WHEAT)
            .add(BlockTypes.SUGAR_CANE)
            .add(BlockTypes.VINE)
            .add(BlockTypes.LILY_PAD)
            .add(BlockTypes.CACTUS)
            .add(BlockTypes.NETHER_WART)
            .build();

    public static final MaterialSet STONES = MaterialSet.builder()
            .add(BlockTypes.STONE)
            .add(BlockTypes.GRANITE)
            .add(BlockTypes.DIORITE)
            .add(BlockTypes.ANDESITE)
            .add("CALCITE")
            .add("TUFF")
            .build();

    public static final MaterialSet GRASSES = MaterialSet.builder()
            .add(BlockTypes.GRASS_BLOCK)
            .add(BlockTypes.PODZOL)
            .build();

    public static final MaterialSet LIQUIDS = MaterialSet.builder()
            .add(BlockTypes.WATER)
            .add(BlockTypes.LAVA)
            .build();

    public static final MaterialSet FALLING = MaterialSet.builder()
            .with(LIQUIDS)
            .with(BlockCategories.SAND)
            .add(BlockTypes.GRAVEL)
            .build();

    public static final MaterialSet SANDSTONES = MaterialSet.builder()
            .add(BlockTypes.SANDSTONE)
            .add(BlockTypes.CHISELED_SANDSTONE)
            .add(BlockTypes.CUT_SANDSTONE)
            .add(BlockTypes.SMOOTH_SANDSTONE)
            .build();

    public static final MaterialSet RED_SANDSTONES = MaterialSet.builder()
            .add(BlockTypes.RED_SANDSTONE)
            .add(BlockTypes.CHISELED_RED_SANDSTONE)
            .add(BlockTypes.CUT_RED_SANDSTONE)
            .add(BlockTypes.SMOOTH_RED_SANDSTONE)
            .build();

    public static final MaterialSet OVERRIDEABLE = MaterialSet.builder()
            .with(STONES)
            .with(GRASSES)
            .with(BlockCategories.DIRT)
            .with(SANDSTONES)
            .with(RED_SANDSTONES)
            .with(BlockCategories.SAND)
            .add(BlockTypes.GRAVEL)
            .add(BlockTypes.MOSSY_COBBLESTONE)
            .add(BlockTypes.OBSIDIAN)
            .add(BlockTypes.SNOW)
            .add(BlockTypes.CLAY)
            .build();

    public static final MaterialSet ORES = MaterialSet.builder()
            .with(BlockCategories.COAL_ORES)
            .with("COPPER_ORES")
            .with(BlockCategories.DIAMOND_ORES)
            .with(BlockCategories.EMERALD_ORES)
            .with(BlockCategories.GOLD_ORES)
            .with(BlockCategories.IRON_ORES)
            .with(BlockCategories.LAPIS_ORES)
            .with(BlockCategories.REDSTONE_ORES)
            .add(BlockTypes.NETHER_QUARTZ_ORE)
            .add("NETHER_GOLD_ORE")
            .build();

    public static final MaterialSet OVERRIDEABLE_WITH_ORES = MaterialSet.builder()
            .with(OVERRIDEABLE)
            .with(ORES)
            .build();

    public static final MaterialSet PISTONS = MaterialSet.builder()
            .add(BlockTypes.MOVING_PISTON)
            .add(BlockTypes.PISTON)
            .add(BlockTypes.PISTON_HEAD)
            .add(BlockTypes.STICKY_PISTON)
            .build();

    private MaterialSets() {
        throw new UnsupportedOperationException("Cannot create an instance of this class");
    }

}

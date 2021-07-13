package com.thevoxelbox.voxelsniper.brush.type;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.world.block.BlockCategories;
import com.sk89q.worldedit.world.block.BlockState;
import com.sk89q.worldedit.world.block.BlockType;
import com.sk89q.worldedit.world.block.BlockTypes;
import com.thevoxelbox.voxelsniper.sniper.snipe.Snipe;
import com.thevoxelbox.voxelsniper.sniper.snipe.message.SnipeMessenger;
import com.thevoxelbox.voxelsniper.sniper.toolkit.ToolkitProperties;
import com.thevoxelbox.voxelsniper.util.material.MaterialSet;
import com.thevoxelbox.voxelsniper.util.material.MaterialSets;
import com.thevoxelbox.voxelsniper.util.text.NumericParser;
import org.bukkit.ChatColor;

public class OceanBrush extends AbstractBrush {

    private static final int WATER_LEVEL_DEFAULT = 62; // y=63 -- we are using array indices here
    private static final int WATER_LEVEL_MIN = 12;
    private static final int LOW_CUT_LEVEL = 12;
    private static final MaterialSet EXCLUDED_MATERIALS = MaterialSet.builder()
            .with(BlockCategories.SAPLINGS)
            .with(BlockCategories.LOGS)
            .with(BlockCategories.LEAVES)
            .with(BlockCategories.ICE)
            .with(MaterialSets.AIRS)
            .with(MaterialSets.LIQUIDS)
            .with(BlockCategories.SNOW)
            .with(MaterialSets.STEMS)
            .with(MaterialSets.MUSHROOMS)
            .with(BlockCategories.FLOWERS)
            .add(BlockTypes.MELON)
            .add(BlockTypes.PUMPKIN)
            .add(BlockTypes.COCOA)
            .add(BlockTypes.SUGAR_CANE)
            .add(BlockTypes.TALL_GRASS)
            .build();

    private int waterLevel = WATER_LEVEL_DEFAULT;
    private boolean coverFloor;

    @Override
    public void handleCommand(String[] parameters, Snipe snipe) {
        SnipeMessenger messenger = snipe.createMessenger();
        for (int i = 0; i < parameters.length; i++) {
            String parameter = parameters[i];
            if (parameter.equalsIgnoreCase("info")) {
                messenger.sendMessage(ChatColor.BLUE + "Parameters:");
                messenger.sendMessage(ChatColor.GREEN + "-wlevel #  " + ChatColor.BLUE + "--  Sets the water level (e.g. -wlevel 64)");
                messenger.sendMessage(ChatColor.GREEN + "-cfloor [y|n]  " + ChatColor.BLUE + "--  Enables or disables sea floor cover (e.g. -cfloor y) (Cover material will be your voxel material)");
            } else if (parameter.equalsIgnoreCase("-wlevel")) {
                if ((i + 1) >= parameters.length) {
                    messenger.sendMessage(ChatColor.RED + "Missing parameter. Correct syntax: -wlevel [#] (e.g. -wlevel 64)");
                    continue;
                }
                Integer temp = NumericParser.parseInteger(parameters[++i]);
                if (temp == null) {
                    messenger.sendMessage(ChatColor.RED + String.format("Error while parsing parameter: %s", parameter));
                    return;
                }
                if (temp <= WATER_LEVEL_MIN) {
                    messenger.sendMessage(ChatColor.RED + "Error: Your specified water level was below 12.");
                    continue;
                }
                this.waterLevel = temp - 1;
                messenger.sendMessage(ChatColor.BLUE + "Water level set to " + ChatColor.GREEN + (this.waterLevel + 1)); // +1 since we are working with 0-based array indices
            } else if (parameter.equalsIgnoreCase("-cfloor") || parameter.equalsIgnoreCase("-coverfloor")) {
                if ((i + 1) >= parameters.length) {
                    messenger.sendMessage(ChatColor.RED + "Missing parameter. Correct syntax: -cfloor [y|n] (e.g. -cfloor y)");
                    continue;
                }
                this.coverFloor = parameters[++i].equalsIgnoreCase("y");
                messenger.sendMessage(ChatColor.BLUE + String.format(
                        "Floor cover %s.",
                        ChatColor.GREEN + (this.coverFloor ? "enabled" : "disabled")
                ));
            }
        }
    }

    @Override
    public void handleArrowAction(Snipe snipe) {
        ToolkitProperties toolkitProperties = snipe.getToolkitProperties();
        oceanator(toolkitProperties);
    }

    @Override
    public void handleGunpowderAction(Snipe snipe) {
        handleArrowAction(snipe);
    }

    private void oceanator(ToolkitProperties toolkitProperties) {
        EditSession editSession = getEditSession();
        BlockVector3 targetBlock = getTargetBlock();
        int targetBlockX = targetBlock.getX();
        int targetBlockZ = targetBlock.getZ();
        int brushSize = toolkitProperties.getBrushSize();
        int minX = (int) Math.floor(targetBlockX - brushSize);
        int minZ = (int) Math.floor(targetBlockZ - brushSize);
        int maxX = (int) Math.floor(targetBlockX + brushSize);
        int maxZ = (int) Math.floor(targetBlockZ + brushSize);
        for (int x = minX; x <= maxX; x++) {
            for (int z = minZ; z <= maxZ; z++) {
                int currentHeight = getHeight(x, z);
                int wLevelDiff = currentHeight - (this.waterLevel - 1);
                int newSeaFloorLevel = Math.max((this.waterLevel - wLevelDiff), LOW_CUT_LEVEL);
                int highestY = getHighestTerrainBlock(x, z, editSession.getMinY(), editSession.getMaxY() + 1);
                // go down from highest Y block down to new sea floor
                for (int y = highestY; y > newSeaFloorLevel; y--) {
                    BlockState block = getBlock(x, y, z);
                    if (!block.isAir()) {
                        setBlockType(x, y, z, BlockTypes.AIR);
                    }
                }
                // go down from water level to new sea level
                for (int y = this.waterLevel; y > newSeaFloorLevel; y--) {
                    BlockState block = getBlock(x, y, z);
                    BlockType blockType = block.getBlockType();
                    if (blockType != BlockTypes.WATER) {
                        setBlockType(x, y, z, BlockTypes.WATER);
                    }
                }
                // cover the sea floor of required
                if (this.coverFloor && (newSeaFloorLevel < this.waterLevel)) {
                    BlockState block = getBlock(x, newSeaFloorLevel, z);
                    BlockType blockType = block.getBlockType();
                    if (blockType != toolkitProperties.getBlockType()) {
                        setBlockType(x, newSeaFloorLevel, z, toolkitProperties.getBlockType());
                    }
                }
            }
        }
    }

    private int getHeight(int bx, int bz) {
        EditSession editSession = getEditSession();
        for (int y = getHighestTerrainBlock(bx, bz, editSession.getMinY(), editSession.getMaxY() + 1); y > 0; y--) {
            BlockState clamp = this.clampY(bx, y, bz);
            if (!EXCLUDED_MATERIALS.contains(clamp)) {
                return y;
            }
        }
        return 0;
    }

    @Override
    public void sendInfo(Snipe snipe) {
        SnipeMessenger messenger = snipe.createMessenger();
        messenger.sendBrushNameMessage();
        messenger.sendMessage(ChatColor.BLUE + "Water level set to " + ChatColor.GREEN + (this.waterLevel + 1)); // +1 since we are working with 0-based array indices
        messenger.sendMessage(ChatColor.BLUE + String.format(
                "Floor cover %s.",
                ChatColor.GREEN + (this.coverFloor ? "enabled" : "disabled")
        ));
    }

}

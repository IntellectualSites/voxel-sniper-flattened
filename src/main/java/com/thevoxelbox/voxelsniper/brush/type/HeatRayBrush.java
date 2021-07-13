package com.thevoxelbox.voxelsniper.brush.type;

import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.world.block.BlockCategories;
import com.sk89q.worldedit.world.block.BlockState;
import com.sk89q.worldedit.world.block.BlockType;
import com.sk89q.worldedit.world.block.BlockTypes;
import com.thevoxelbox.voxelsniper.sniper.snipe.Snipe;
import com.thevoxelbox.voxelsniper.sniper.snipe.message.SnipeMessenger;
import com.thevoxelbox.voxelsniper.sniper.toolkit.ToolkitProperties;
import com.thevoxelbox.voxelsniper.util.Vectors;
import com.thevoxelbox.voxelsniper.util.material.MaterialSet;
import com.thevoxelbox.voxelsniper.util.material.MaterialSets;
import com.thevoxelbox.voxelsniper.util.material.Materials;
import org.bukkit.ChatColor;
import org.bukkit.util.Vector;
import org.bukkit.util.noise.PerlinNoiseGenerator;

import java.util.Random;

public class HeatRayBrush extends AbstractBrush {

    private static final double REQUIRED_OBSIDIAN_DENSITY = 0.6;
    private static final double REQUIRED_COBBLE_DENSITY = 0.5;
    private static final double REQUIRED_FIRE_DENSITY = -0.25;
    private static final double REQUIRED_AIR_DENSITY = 0;
    private static final MaterialSet FLAMEABLE_BLOCKS = MaterialSet.builder()
            .with(BlockCategories.LOGS)
            .with(BlockCategories.SAPLINGS)
            .with(BlockCategories.PLANKS)
            .with(BlockCategories.LEAVES)
            .with(BlockCategories.WOOL)
            .with(BlockCategories.WOODEN_SLABS)
            .with(BlockCategories.WOODEN_STAIRS)
            .with(BlockCategories.WOODEN_DOORS)
            .with(BlockCategories.WOODEN_TRAPDOORS)
            .with(BlockCategories.WOODEN_PRESSURE_PLATES)
            .with(BlockCategories.ICE)
            .with(BlockCategories.SIGNS)
            .with(BlockCategories.WOODEN_FENCES)
            .with(BlockCategories.FENCE_GATES)
            .with(BlockCategories.SNOW)
            .with(MaterialSets.TORCHES)
            .with(MaterialSets.FLORA)
            .add(BlockTypes.SPONGE)
            .add(BlockTypes.COBWEB)
            .add(BlockTypes.FIRE)
            .add(BlockTypes.LADDER)
            .build();

    private int octaves = 5;
    private double frequency = 1;
    private double amplitude = 0.3;

    @Override
    public void handleCommand(String[] parameters, Snipe snipe) {
        SnipeMessenger messenger = snipe.createMessenger();
        for (String s : parameters) {
            String parameter = s.toLowerCase();
            if (parameter.equalsIgnoreCase("info")) {
                messenger.sendMessage(ChatColor.GOLD + "Heat Ray brush Parameters:");
                messenger.sendMessage(ChatColor.AQUA + "/b hr oct[int] -- Octaves parameter for the noise generator.");
                messenger.sendMessage(ChatColor.AQUA + "/b hr amp[float] -- Amplitude parameter for the noise generator.");
                messenger.sendMessage(ChatColor.AQUA + "/b hr freq[float] -- Frequency parameter for the noise generator.");
            }
            if (parameter.startsWith("oct")) {
                this.octaves = Integer.parseInt(parameter.replace("oct", ""));
                messenger.sendMessage(ChatColor.GREEN + "Octaves: " + this.octaves);
            } else if (parameter.startsWith("amp")) {
                this.amplitude = Double.parseDouble(parameter.replace("amp", ""));
                messenger.sendMessage(ChatColor.GREEN + "Amplitude: " + this.amplitude);
            } else if (parameter.startsWith("freq")) {
                this.frequency = Double.parseDouble(parameter.replace("freq", ""));
                messenger.sendMessage(ChatColor.GREEN + "Frequency: " + this.frequency);
            }
        }
    }

    @Override
    public void handleArrowAction(Snipe snipe) {
        heatRay(snipe);
    }

    @Override
    public void handleGunpowderAction(Snipe snipe) {
        heatRay(snipe);
    }

    /**
     * Heat Ray executer.
     */
    public void heatRay(Snipe snipe) {
        ToolkitProperties toolkitProperties = snipe.getToolkitProperties();
        PerlinNoiseGenerator generator = new PerlinNoiseGenerator(new Random());
        BlockVector3 targetBlock = getTargetBlock();
        Vector targetBlockVector = Vectors.toBukkit(targetBlock);
        Vector currentLocation = new Vector();
        int brushSize = toolkitProperties.getBrushSize();
        for (int z = brushSize; z >= -brushSize; z--) {
            for (int x = brushSize; x >= -brushSize; x--) {
                for (int y = brushSize; y >= -brushSize; y--) {
                    currentLocation.setX(targetBlock.getX() + x);
                    currentLocation.setY(targetBlock.getY() + y);
                    currentLocation.setZ(targetBlock.getZ() + z);
                    Vector currentLocationVector = currentLocation.clone();
                    if (currentLocationVector.isInSphere(targetBlockVector, brushSize)) {
                        BlockState currentBlock = getBlock(
                                currentLocation.getBlockX(),
                                currentLocation.getBlockY(),
                                currentLocation.getBlockZ()
                        );
                        BlockType currentBlockType = currentBlock.getBlockType();
                        if (currentBlockType == BlockTypes.CHEST) {
                            continue;
                        }
                        if (Materials.isLiquid(currentBlockType)) {
                            setBlockType(
                                    currentLocation.getBlockX(),
                                    currentLocation.getBlockY(),
                                    currentLocation.getBlockZ(),
                                    BlockTypes.AIR
                            );
                            continue;
                        }
                        if (FLAMEABLE_BLOCKS.contains(currentBlockType)) {
                            setBlockType(
                                    currentLocation.getBlockX(),
                                    currentLocation.getBlockY(),
                                    currentLocation.getBlockZ(),
                                    BlockTypes.FIRE
                            );
                            continue;
                        }
                        if (!Materials.isEmpty(currentBlockType)) {
                            double airDensity = generator.noise(
                                    currentLocation.getX(),
                                    currentLocation.getY(),
                                    currentLocation.getZ(),
                                    this.octaves,
                                    this.frequency,
                                    this.amplitude
                            );
                            double fireDensity = generator.noise(
                                    currentLocation.getX(),
                                    currentLocation.getY(),
                                    currentLocation.getZ(),
                                    this.octaves,
                                    this.frequency,
                                    this.amplitude
                            );
                            double cobbleDensity = generator.noise(
                                    currentLocation.getX(),
                                    currentLocation.getY(),
                                    currentLocation.getZ(),
                                    this.octaves,
                                    this.frequency,
                                    this.amplitude
                            );
                            double obsidianDensity = generator.noise(
                                    currentLocation.getX(),
                                    currentLocation.getY(),
                                    currentLocation.getZ(),
                                    this.octaves,
                                    this.frequency,
                                    this.amplitude
                            );
                            if (obsidianDensity >= REQUIRED_OBSIDIAN_DENSITY) {
                                if (currentBlockType != BlockTypes.OBSIDIAN) {
                                    setBlockType(
                                            currentLocation.getBlockX(),
                                            currentLocation.getBlockY(),
                                            currentLocation.getBlockZ(),
                                            BlockTypes.OBSIDIAN
                                    );
                                }
                            } else if (cobbleDensity >= REQUIRED_COBBLE_DENSITY) {
                                if (currentBlockType != BlockTypes.COBBLESTONE) {
                                    setBlockType(
                                            currentLocation.getBlockX(),
                                            currentLocation.getBlockY(),
                                            currentLocation.getBlockZ(),
                                            BlockTypes.COBBLESTONE
                                    );
                                }
                            } else if (fireDensity >= REQUIRED_FIRE_DENSITY) {
                                if (currentBlockType != BlockTypes.FIRE) {
                                    setBlockType(
                                            currentLocation.getBlockX(),
                                            currentLocation.getBlockY(),
                                            currentLocation.getBlockZ(),
                                            BlockTypes.FIRE
                                    );
                                }
                            } else if (airDensity >= REQUIRED_AIR_DENSITY) {
                                setBlockType(
                                        currentLocation.getBlockX(),
                                        currentLocation.getBlockY(),
                                        currentLocation.getBlockZ(),
                                        BlockTypes.AIR
                                );
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    public void sendInfo(Snipe snipe) {
        SnipeMessenger messenger = snipe.createMessenger();
        messenger.sendBrushNameMessage();
        messenger.sendMessage(ChatColor.GREEN + "Octaves: " + this.octaves);
        messenger.sendMessage(ChatColor.GREEN + "Amplitude: " + this.amplitude);
        messenger.sendMessage(ChatColor.GREEN + "Frequency: " + this.frequency);
        messenger.sendBrushSizeMessage();
    }

}

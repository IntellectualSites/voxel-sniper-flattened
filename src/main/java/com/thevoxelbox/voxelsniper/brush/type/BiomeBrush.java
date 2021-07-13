package com.thevoxelbox.voxelsniper.brush.type;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.math.BlockVector3;
import com.thevoxelbox.voxelsniper.sniper.Sniper;
import com.thevoxelbox.voxelsniper.sniper.snipe.Snipe;
import com.thevoxelbox.voxelsniper.sniper.snipe.message.SnipeMessenger;
import com.thevoxelbox.voxelsniper.sniper.toolkit.ToolkitProperties;
import org.bukkit.ChatColor;
import org.bukkit.block.Biome;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class BiomeBrush extends AbstractBrush {

    private Biome selectedBiome = Biome.PLAINS;

    @Override
    public void handleCommand(String[] parameters, Snipe snipe) {
        Sniper sniper = snipe.getSniper();
        Player player = sniper.getPlayer();
        String firstParameter = parameters[1];
        if (firstParameter.equalsIgnoreCase("info")) {
            player.sendMessage(ChatColor.GOLD + "Biome Brush Parameters:");
            StringBuilder availableBiomes = new StringBuilder();
            for (Biome biome : Biome.values()) {
                if (availableBiomes.length() == 0) {
                    availableBiomes = new StringBuilder(ChatColor.DARK_GREEN + biome.name());
                    continue;
                }
                availableBiomes.append(ChatColor.RED + ", " + ChatColor.DARK_GREEN)
                        .append(biome.name());
            }
            player.sendMessage(ChatColor.DARK_BLUE + "Available biomes: " + availableBiomes);
        } else {
            // allows biome names with spaces in their name
            String biomeName = IntStream.range(2, parameters.length)
                    .mapToObj(index -> " " + parameters[index])
                    .collect(Collectors.joining("", firstParameter, ""));
            this.selectedBiome = Arrays.stream(Biome.values())
                    .filter(biome -> biomeName.equalsIgnoreCase(biome.name()))
                    .findFirst()
                    .orElse(this.selectedBiome);
            player.sendMessage(ChatColor.GOLD + "Currently selected biome type: " + ChatColor.DARK_GREEN + this.selectedBiome.name());
        }
    }

    @Override
    public void handleArrowAction(Snipe snipe) {
        biome(snipe);
    }

    @Override
    public void handleGunpowderAction(Snipe snipe) {
        biome(snipe);
    }

    private void biome(Snipe snipe) {
        ToolkitProperties toolkitProperties = snipe.getToolkitProperties();
        int brushSize = toolkitProperties.getBrushSize();
        double brushSizeSquared = Math.pow(brushSize, 2);
        EditSession editSession = getEditSession();
        BlockVector3 targetBlock = getTargetBlock();
        int targetBlockX = targetBlock.getX();
        int targetBlockZ = targetBlock.getZ();
        for (int x = -brushSize; x <= brushSize; x++) {
            double xSquared = Math.pow(x, 2);
            for (int z = -brushSize; z <= brushSize; z++) {
                if (xSquared + Math.pow(z, 2) <= brushSizeSquared) {
                    for (int y = 0; y < editSession.getMaxY() + 1; ++y) {
                        setBiome(targetBlockX + x, y, targetBlockZ + z, this.selectedBiome);
                    }
                }
            }
        }
        int block1X = targetBlockX - brushSize;
        int block2X = targetBlockX + brushSize;
        int block1Z = targetBlockZ - brushSize;
        int block2Z = targetBlockZ + brushSize;
        int chunk1X = block1X >> 4;
        int chunk2X = block2X >> 4;
        int chunk1Z = block1Z >> 4;
        int chunk2Z = block2Z >> 4;
        int lowChunkX = block1X <= block2X ? chunk1X : chunk2X;
        int lowChunkZ = block1Z <= block2Z ? chunk1Z : chunk2Z;
        int highChunkX = block1X >= block2X ? chunk1X : chunk2X;
        int highChunkZ = block1Z >= block2Z ? chunk1Z : chunk2Z;
        for (int x = lowChunkX; x <= highChunkX; x++) {
            for (int z = lowChunkZ; z <= highChunkZ; z++) {
                refreshChunk(x, z);
            }
        }
    }

    @Override
    public void sendInfo(Snipe snipe) {
        SnipeMessenger messenger = snipe.createMessenger();
        messenger.sendBrushNameMessage();
        messenger.sendMessage(ChatColor.GOLD + "Currently selected biome type: " + ChatColor.DARK_GREEN + this.selectedBiome.name());
    }

}

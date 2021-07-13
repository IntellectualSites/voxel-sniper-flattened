package com.thevoxelbox.voxelsniper.brush.type.performer;

import com.sk89q.worldedit.math.BlockVector3;
import com.thevoxelbox.voxelsniper.sniper.snipe.Snipe;
import com.thevoxelbox.voxelsniper.sniper.snipe.message.SnipeMessenger;
import com.thevoxelbox.voxelsniper.sniper.toolkit.ToolkitProperties;
import com.thevoxelbox.voxelsniper.util.material.MaterialSets;
import org.bukkit.ChatColor;

public class UnderlayBrush extends AbstractPerformerBrush {

    private static final int DEFAULT_DEPTH = 3;

    private int depth = DEFAULT_DEPTH;
    private boolean allBlocks;

    @Override
    public final void handleCommand(String[] parameters, Snipe snipe) {
        SnipeMessenger messenger = snipe.createMessenger();
        for (String parameter : parameters) {
            if (parameter.equalsIgnoreCase("info")) {
                snipe.createMessageSender()
                        .message(ChatColor.GOLD + "Reverse Overlay brush parameters:")
                        .message(ChatColor.AQUA + "d[number] (ex: d3) The number of blocks thick to change.")
                        .message(ChatColor.BLUE + "all (ex: /b reover all) Sets the brush to affect ALL materials")
                        .send();
                if (this.depth < 1) {
                    this.depth = 1;
                }
                return;
            }
            if (!parameter.isEmpty() && parameter.charAt(0) == 'd') {
                this.depth = Integer.parseInt(parameter.replace("d", ""));
                messenger.sendMessage(ChatColor.AQUA + "Depth set to " + this.depth);
            } else if (parameter.startsWith("all")) {
                this.allBlocks = true;
                messenger.sendMessage(ChatColor.BLUE + "Will underlay over any block." + this.depth);
            } else if (parameter.startsWith("some")) {
                this.allBlocks = false;
                messenger.sendMessage(ChatColor.BLUE + "Will underlay only natural block types." + this.depth);
            } else {
                messenger.sendMessage(ChatColor.RED + "Invalid brush parameters! use the info parameter to display parameter info.");
            }
        }
    }

    @Override
    public void handleArrowAction(Snipe snipe) {
        underlay(snipe);
    }

    @Override
    public void handleGunpowderAction(Snipe snipe) {
        underlay2(snipe);
    }

    private void underlay(Snipe snipe) {
        ToolkitProperties toolkitProperties = snipe.getToolkitProperties();
        int brushSize = toolkitProperties.getBrushSize();
        int[][] memory = new int[brushSize * 2 + 1][brushSize * 2 + 1];
        double brushSizeSquared = Math.pow(brushSize + 0.5, 2);
        for (int z = brushSize; z >= -brushSize; z--) {
            for (int x = brushSize; x >= -brushSize; x--) {
                BlockVector3 targetBlock = getTargetBlock();
                int blockX = targetBlock.getX();
                int blockY = targetBlock.getY();
                int blockZ = targetBlock.getZ();
                for (int y = blockY; y < blockY + this.depth; y++) { // start scanning from the height you clicked at
                    if (memory[x + brushSize][z + brushSize] != 1) { // if haven't already found the surface in this column
                        if (Math.pow(x, 2) + Math.pow(z, 2) <= brushSizeSquared) { // if inside of the column...
                            if (this.allBlocks) {
                                for (int i = 0; i < this.depth; i++) {
                                    if (!clampY(blockX + x, y + i, blockZ + z).isAir()) {
                                        this.performer.perform(
                                                getEditSession(),
                                                blockX + x,
                                                clampY(y + i),
                                                blockZ + z,
                                                clampY(blockX + x, y + i, blockZ + z)
                                        ); // fills down as many layers as you specify in
                                        // parameters
                                        memory[x + brushSize][z + brushSize] = 1; // stop it from checking any other blocks in this vertical 1x1 column.
                                    }
                                }
                            } else { // if the override parameter has not been activated, go to the switch that filters out manmade stuff.
                                if (MaterialSets.OVERRIDEABLE.contains(getBlockType(blockX + x, y, blockZ + z))) {
                                    for (int i = 0; (i < this.depth); i++) {
                                        if (!clampY(blockX + x, y + i, blockZ + z).isAir()) {
                                            this.performer.perform(
                                                    getEditSession(),
                                                    blockX + x,
                                                    clampY(y + i),
                                                    blockZ + z,
                                                    clampY(blockX + x, y + i, blockZ + z)
                                            ); // fills down as many layers as you specify in
                                            // parameters
                                            memory[x + brushSize][z + brushSize] = 1; // stop it from checking any other blocks in this vertical 1x1 column.
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private void underlay2(Snipe snipe) {
        ToolkitProperties toolkitProperties = snipe.getToolkitProperties();
        int brushSize = toolkitProperties.getBrushSize();
        int[][] memory = new int[brushSize * 2 + 1][brushSize * 2 + 1];
        double brushSizeSquared = Math.pow(brushSize + 0.5, 2);
        for (int z = brushSize; z >= -brushSize; z--) {
            for (int x = brushSize; x >= -brushSize; x--) {
                BlockVector3 targetBlock = getTargetBlock();
                int blockX = targetBlock.getX();
                int blockY = targetBlock.getY();
                int blockZ = targetBlock.getZ();
                for (int y = blockY; y < blockY + this.depth; y++) { // start scanning from the height you clicked at
                    if (memory[x + brushSize][z + brushSize] != 1) { // if haven't already found the surface in this column
                        if ((Math.pow(x, 2) + Math.pow(z, 2)) <= brushSizeSquared) { // if inside of the column...
                            if (this.allBlocks) {
                                for (int i = -1; i < this.depth - 1; i++) {
                                    this.performer.perform(
                                            getEditSession(),
                                            blockX + x,
                                            clampY(y - i),
                                            blockZ + z,
                                            clampY(blockX + x, y - i, blockZ + z)
                                    ); // fills down as many layers as you specify in
                                    // parameters
                                    memory[x + brushSize][z + brushSize] = 1; // stop it from checking any other blocks in this vertical 1x1 column.
                                }
                            } else {
                                // if the override parameter has not been activated, go to the switch that filters out manmade stuff.
                                if (MaterialSets.OVERRIDEABLE_WITH_ORES.contains(getBlockType(blockX + x, y, blockZ + z))) {
                                    for (int i = -1; i < this.depth - 1; i++) {
                                        this.performer.perform(
                                                getEditSession(),
                                                blockX + x,
                                                clampY(y - i),
                                                blockZ + z,
                                                clampY(blockX + x, y - i, blockZ + z)
                                        ); // fills down as many layers as you specify in
                                        // parameters
                                        memory[x + brushSize][z + brushSize] = 1; // stop it from checking any other blocks in this vertical 1x1 column.
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    public void sendInfo(Snipe snipe) {
        snipe.createMessageSender()
                .brushNameMessage()
                .brushSizeMessage()
                .send();
    }

}

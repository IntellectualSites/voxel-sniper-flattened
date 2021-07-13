package com.thevoxelbox.voxelsniper.brush.type;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.world.block.BlockState;
import com.sk89q.worldedit.world.block.BlockType;
import com.sk89q.worldedit.world.block.BlockTypes;
import com.thevoxelbox.voxelsniper.sniper.snipe.Snipe;
import com.thevoxelbox.voxelsniper.sniper.snipe.message.SnipeMessenger;
import com.thevoxelbox.voxelsniper.sniper.toolkit.ToolkitProperties;
import com.thevoxelbox.voxelsniper.util.Vectors;
import com.thevoxelbox.voxelsniper.util.material.Materials;
import com.thevoxelbox.voxelsniper.util.text.NumericParser;
import org.bukkit.ChatColor;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.Nullable;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class ErodeBrush extends AbstractBrush {

    private static final List<BlockVector3> FACES_TO_CHECK = Arrays.asList(
            BlockVector3.at(0, 0, 1),
            BlockVector3.at(0, 0, -1),
            BlockVector3.at(0, 1, 0),
            BlockVector3.at(0, -1, 0),
            BlockVector3.at(1, 0, 0),
            BlockVector3.at(-1, 0, 0)
    );

    private ErosionPreset currentPreset = new ErosionPreset(0, 1, 0, 1);

    @Override
    public void handleCommand(String[] parameters, Snipe snipe) {
        SnipeMessenger messenger = snipe.createMessenger();
        for (String parameter : parameters) {
            Preset preset = Preset.getPreset(parameter);
            if (preset != null) {
                try {
                    this.currentPreset = preset.getPreset();
                    messenger.sendMessage(ChatColor.LIGHT_PURPLE + "Brush preset set to " + preset.getName());
                    return;
                } catch (IllegalArgumentException exception) {
                    messenger.sendMessage(ChatColor.LIGHT_PURPLE + "No such preset.");
                    return;
                }
            }
            ErosionPreset currentPresetBackup = this.currentPreset;
            if (!parameter.isEmpty() && parameter.charAt(0) == 'f') {
                String fillFacesString = parameter.replace("f", "");
                Integer fillFaces = NumericParser.parseInteger(fillFacesString);
                if (fillFaces != null) {
                    this.currentPreset = new ErosionPreset(
                            this.currentPreset.getErosionFaces(),
                            this.currentPreset.getErosionRecursion(),
                            fillFaces,
                            this.currentPreset.getFillRecursion()
                    );
                }
            }
            if (!parameter.isEmpty() && parameter.charAt(0) == 'e') {
                String erosionFacesString = parameter.replace("e", "");
                Integer erosionFaces = NumericParser.parseInteger(erosionFacesString);
                if (erosionFaces != null) {
                    this.currentPreset = new ErosionPreset(
                            erosionFaces,
                            this.currentPreset.getErosionRecursion(),
                            this.currentPreset.getFillFaces(),
                            this.currentPreset.getFillRecursion()
                    );
                }
            }
            if (!parameter.isEmpty() && parameter.charAt(0) == 'F') {
                String fillRecursionString = parameter.replace("F", "");
                Integer fillRecursion = NumericParser.parseInteger(fillRecursionString);
                if (fillRecursion != null) {
                    this.currentPreset = new ErosionPreset(
                            this.currentPreset.getErosionFaces(),
                            this.currentPreset.getErosionRecursion(),
                            this.currentPreset.getFillFaces(),
                            fillRecursion
                    );
                }
            }
            if (!parameter.isEmpty() && parameter.charAt(0) == 'E') {
                String erosionRecursionString = parameter.replace("E", "");
                Integer erosionRecursion = NumericParser.parseInteger(erosionRecursionString);
                if (erosionRecursion != null) {
                    this.currentPreset = new ErosionPreset(
                            this.currentPreset.getErosionFaces(),
                            erosionRecursion,
                            this.currentPreset.getFillFaces(),
                            this.currentPreset.getFillRecursion()
                    );
                }
            }
            if (!this.currentPreset.equals(currentPresetBackup)) {
                if (this.currentPreset.getErosionFaces() != currentPresetBackup.getErosionFaces()) {
                    messenger.sendMessage(ChatColor.AQUA + "Erosion faces set to: " + ChatColor.WHITE + this.currentPreset.getErosionFaces());
                }
                if (this.currentPreset.getFillFaces() != currentPresetBackup.getFillFaces()) {
                    messenger.sendMessage(ChatColor.AQUA + "Fill faces set to: " + ChatColor.WHITE + this.currentPreset.getFillFaces());
                }
                if (this.currentPreset.getErosionRecursion() != currentPresetBackup.getErosionRecursion()) {
                    messenger.sendMessage(ChatColor.AQUA + "Erosion recursions set to: " + ChatColor.WHITE + this.currentPreset.getErosionRecursion());
                }
                if (this.currentPreset.getFillRecursion() != currentPresetBackup.getFillRecursion()) {
                    messenger.sendMessage(ChatColor.AQUA + "Fill recursions set to: " + ChatColor.WHITE + this.currentPreset.getFillRecursion());
                }
            }
        }
    }

    @Override
    public void handleArrowAction(Snipe snipe) {
        erosion(snipe, this.currentPreset);
    }

    @Override
    public void handleGunpowderAction(Snipe snipe) {
        erosion(snipe, this.currentPreset.getInverted());
    }

    private void erosion(Snipe snipe, ErosionPreset erosionPreset) {
        ToolkitProperties toolkitProperties = snipe.getToolkitProperties();
        BlockVector3 targetBlock = getTargetBlock();
        BlockChangeTracker blockChangeTracker = new BlockChangeTracker(getEditSession());
        Vector targetBlockVector = Vectors.toBukkit(targetBlock);
        for (int i = 0; i < erosionPreset.getErosionRecursion(); ++i) {
            erosionIteration(toolkitProperties, erosionPreset, blockChangeTracker, targetBlockVector);
        }
        for (int i = 0; i < erosionPreset.getFillRecursion(); ++i) {
            fillIteration(toolkitProperties, erosionPreset, blockChangeTracker, targetBlockVector);
        }
        for (BlockWrapper blockWrapper : blockChangeTracker.getAll()) {
            BlockState block = blockWrapper.getBlock();
            if (block != null) {
                BlockState blockData = blockWrapper.getBlockData();
                setBlockData(blockWrapper.getX(), blockWrapper.getY(), blockWrapper.getZ(), blockData);
            }
        }
    }

    private void fillIteration(
            ToolkitProperties toolkitProperties,
            ErosionPreset erosionPreset,
            BlockChangeTracker blockChangeTracker,
            Vector targetBlockVector
    ) {
        int currentIteration = blockChangeTracker.nextIteration();
        BlockVector3 targetBlock = getTargetBlock();
        int brushSize = toolkitProperties.getBrushSize();
        for (int x = targetBlock.getX() - brushSize; x <= targetBlock.getX() + brushSize; ++x) {
            for (int z = targetBlock.getZ() - brushSize; z <= targetBlock.getZ() + brushSize; ++z) {
                for (int y = targetBlock.getY() - brushSize; y <= targetBlock.getY() + brushSize; ++y) {
                    Vector currentPosition = new Vector(x, y, z);
                    if (currentPosition.isInSphere(targetBlockVector, brushSize)) {
                        BlockWrapper currentBlock = blockChangeTracker.get(currentPosition, currentIteration);
                        if (!(currentBlock.isEmpty() || currentBlock.isLiquid())) {
                            continue;
                        }
                        int count = 0;
                        Map<BlockWrapper, Integer> blockCount = new HashMap<>();
                        for (BlockVector3 vector : FACES_TO_CHECK) {
                            Vector relativePosition = Vectors.toBukkit(Vectors.of(currentPosition).add(vector));
                            BlockWrapper relativeBlock = blockChangeTracker.get(relativePosition, currentIteration);
                            if (!(relativeBlock.isEmpty() || relativeBlock.isLiquid())) {
                                count++;
                                BlockWrapper typeBlock = new BlockWrapper(x, y, z, null, relativeBlock.getBlockData());
                                if (blockCount.containsKey(typeBlock)) {
                                    blockCount.put(typeBlock, blockCount.get(typeBlock) + 1);
                                } else {
                                    blockCount.put(typeBlock, 1);
                                }
                            }
                        }
                        BlockWrapper currentBlockWrapper = new BlockWrapper(x, y, z, null, BlockTypes.AIR.getDefaultState());
                        int amount = 0;
                        for (BlockWrapper wrapper : blockCount.keySet()) {
                            Integer currentCount = blockCount.get(wrapper);
                            if (amount <= currentCount) {
                                currentBlockWrapper = wrapper;
                                amount = currentCount;
                            }
                        }
                        if (count >= erosionPreset.getFillFaces()) {
                            blockChangeTracker.put(
                                    currentPosition,
                                    new BlockWrapper(x, y, z, currentBlock.getBlock(), currentBlockWrapper.getBlockData()),
                                    currentIteration
                            );
                        }
                    }
                }
            }
        }
    }

    private void erosionIteration(
            ToolkitProperties toolkitProperties,
            ErosionPreset erosionPreset,
            BlockChangeTracker blockChangeTracker,
            Vector targetBlockVector
    ) {
        int currentIteration = blockChangeTracker.nextIteration();
        BlockVector3 targetBlock = this.getTargetBlock();
        int brushSize = toolkitProperties.getBrushSize();
        for (int x = targetBlock.getX() - brushSize; x <= targetBlock.getX() + brushSize; ++x) {
            for (int z = targetBlock.getZ() - brushSize; z <= targetBlock.getZ() + brushSize; ++z) {
                for (int y = targetBlock.getY() - brushSize; y <= targetBlock.getY() + brushSize; ++y) {
                    Vector currentPosition = new Vector(x, y, z);
                    if (currentPosition.isInSphere(targetBlockVector, brushSize)) {
                        BlockWrapper currentBlock = blockChangeTracker.get(currentPosition, currentIteration);
                        if (currentBlock.isEmpty() || currentBlock.isLiquid()) {
                            continue;
                        }
                        int count = (int) FACES_TO_CHECK.stream()
                                .map(vector -> Vectors.of(currentPosition).add(vector))
                                .map(Vectors::toBukkit)
                                .map(relativePosition -> blockChangeTracker.get(relativePosition, currentIteration))
                                .filter(relativeBlock -> relativeBlock.isEmpty() || relativeBlock.isLiquid())
                                .count();
                        if (count >= erosionPreset.getErosionFaces()) {
                            blockChangeTracker.put(
                                    currentPosition,
                                    new BlockWrapper(x, y, z, currentBlock.getBlock(), BlockTypes.AIR.getDefaultState()),
                                    currentIteration
                            );
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
        messenger.sendBrushSizeMessage();
        messenger.sendMessage(ChatColor.AQUA + "Erosion minimum exposed faces set to " + this.currentPreset.getErosionFaces());
        messenger.sendMessage(ChatColor.BLUE + "Fill minumum touching faces set to " + this.currentPreset.getFillFaces());
        messenger.sendMessage(ChatColor.DARK_BLUE + "Erosion recursion amount set to " + this.currentPreset.getErosionRecursion());
        messenger.sendMessage(ChatColor.DARK_GREEN + "Fill recursion amount set to " + this.currentPreset.getFillRecursion());
    }

    private enum Preset {

        MELT("melt", new ErosionPreset(2, 1, 5, 1)),
        FILL("fill", new ErosionPreset(5, 1, 2, 1)),
        SMOOTH("smooth", new ErosionPreset(3, 1, 3, 1)),
        LIFT("lift", new ErosionPreset(6, 0, 1, 1)),
        FLOAT_CLEAN("floatclean", new ErosionPreset(6, 1, 6, 1));

        private final String name;
        private final ErosionPreset preset;

        Preset(String name, ErosionPreset preset) {
            this.name = name;
            this.preset = preset;
        }

        @Nullable
        public static Preset getPreset(String name) {
            return Arrays.stream(values())
                    .filter(preset -> preset.name.equalsIgnoreCase(name))
                    .findFirst()
                    .orElse(null);
        }

        public String getName() {
            return this.name;
        }

        public ErosionPreset getPreset() {
            return this.preset;
        }
    }

    private static final class BlockChangeTracker {

        private final Map<Integer, Map<Vector, BlockWrapper>> blockChanges;
        private final Map<Vector, BlockWrapper> flatChanges;
        private final EditSession editSession;
        private int nextIterationId;

        private BlockChangeTracker(EditSession editSession) {
            this.blockChanges = new HashMap<>();
            this.flatChanges = new HashMap<>();
            this.editSession = editSession;
        }

        public BlockWrapper get(Vector position, int iteration) {
            for (int i = iteration - 1; i >= 0; --i) {
                if (this.blockChanges.containsKey(i) && this.blockChanges.get(i)
                        .containsKey(position)) {
                    return this.blockChanges.get(i)
                            .get(position);
                }
            }
            return new BlockWrapper(position.getBlockX(), position.getBlockY(), position.getBlockZ(),
                    editSession.getBlock(position.getBlockX(), position.getBlockY(), position.getBlockZ())
            );
        }

        public Collection<BlockWrapper> getAll() {
            return this.flatChanges.values();
        }

        public int nextIteration() {
            int nextIterationId = this.nextIterationId;
            this.nextIterationId++;
            return nextIterationId;
        }

        public void put(Vector position, BlockWrapper changedBlock, int iteration) {
            if (!this.blockChanges.containsKey(iteration)) {
                this.blockChanges.put(iteration, new HashMap<>());
            }
            this.blockChanges.get(iteration)
                    .put(position, changedBlock);
            this.flatChanges.put(position, changedBlock);
        }

    }

    private static final class BlockWrapper {

        private final int x;
        private final int y;
        private final int z;
        @Nullable
        private final BlockState block;
        private final BlockState blockData;

        private BlockWrapper(int x, int y, int z, BlockState block) {
            this(x, y, z, block, block);
        }

        private BlockWrapper(int x, int y, int z, @Nullable BlockState block, BlockState blockData) {
            this.x = x;
            this.y = y;
            this.z = z;
            this.block = block;
            this.blockData = blockData;
        }

        public int getX() {
            return x;
        }

        public int getY() {
            return y;
        }

        public int getZ() {
            return z;
        }

        @Nullable
        public BlockState getBlock() {
            return this.block;
        }

        public BlockState getBlockData() {
            return this.blockData;
        }

        public boolean isEmpty() {
            BlockType type = blockData.getBlockType();
            return Materials.isEmpty(type);
        }

        public boolean isLiquid() {
            BlockType type = blockData.getBlockType();
            return Materials.isLiquid(type);
        }

    }

    private static final class ErosionPreset implements Serializable {

        private static final long serialVersionUID = 8997952776355430411L;

        private final int erosionFaces;
        private final int erosionRecursion;
        private final int fillFaces;
        private final int fillRecursion;

        private ErosionPreset(int erosionFaces, int erosionRecursion, int fillFaces, int fillRecursion) {
            this.erosionFaces = erosionFaces;
            this.erosionRecursion = erosionRecursion;
            this.fillFaces = fillFaces;
            this.fillRecursion = fillRecursion;
        }

        @Override
        public int hashCode() {
            return Objects.hash(this.erosionFaces, this.erosionRecursion, this.fillFaces, this.fillRecursion);
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof ErosionPreset) {
                ErosionPreset other = (ErosionPreset) obj;
                return this.erosionFaces == other.erosionFaces && this.erosionRecursion == other.erosionRecursion && this.fillFaces == other.fillFaces && this.fillRecursion == other.fillRecursion;
            }
            return false;
        }

        /**
         * @return the erosionFaces
         */
        public int getErosionFaces() {
            return this.erosionFaces;
        }

        /**
         * @return the erosionRecursion
         */
        public int getErosionRecursion() {
            return this.erosionRecursion;
        }

        /**
         * @return the fillFaces
         */
        public int getFillFaces() {
            return this.fillFaces;
        }

        /**
         * @return the fillRecursion
         */
        public int getFillRecursion() {
            return this.fillRecursion;
        }

        public ErosionPreset getInverted() {
            return new ErosionPreset(this.fillFaces, this.fillRecursion, this.erosionFaces, this.erosionRecursion);
        }

    }

}

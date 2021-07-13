package com.thevoxelbox.voxelsniper.brush.type;

import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.world.block.BlockState;
import com.sk89q.worldedit.world.block.BlockType;
import com.sk89q.worldedit.world.block.BlockTypes;
import com.thevoxelbox.voxelsniper.sniper.snipe.Snipe;
import com.thevoxelbox.voxelsniper.sniper.snipe.message.SnipeMessenger;
import com.thevoxelbox.voxelsniper.sniper.toolkit.ToolkitProperties;
import com.thevoxelbox.voxelsniper.util.material.Materials;
import com.thevoxelbox.voxelsniper.util.text.NumericParser;
import org.bukkit.ChatColor;

import java.util.HashSet;
import java.util.Set;

public class PullBrush extends AbstractBrush {

    private final Set<PullBrushBlockWrapper> surface = new HashSet<>();
    private int voxelHeight;
    private double c1 = 1;
    private double c2;

    @Override
    public void handleCommand(String[] parameters, Snipe snipe) {
        Double pinch = NumericParser.parseDouble(parameters[0]);
        Double bubble = NumericParser.parseDouble(parameters[1]);
        if (pinch == null || bubble == null) {
            SnipeMessenger messenger = snipe.createMessenger();
            messenger.sendMessage(ChatColor.RED + "Invalid brush parameters!");
            return;
        }
        this.c1 = 1 - pinch;
        this.c2 = bubble;
    }

    @Override
    public void handleArrowAction(Snipe snipe) {
        ToolkitProperties toolkitProperties = snipe.getToolkitProperties();
        this.voxelHeight = toolkitProperties.getVoxelHeight();
        getSurface(toolkitProperties);
        if (this.voxelHeight > 0) {
            for (PullBrushBlockWrapper block : this.surface) {
                setBlock(block);
            }
        } else if (this.voxelHeight < 0) {
            for (PullBrushBlockWrapper block : this.surface) {
                setBlockDown(block);
            }
        }
    }

    @Override
    public void handleGunpowderAction(Snipe snipe) {
        ToolkitProperties toolkitProperties = snipe.getToolkitProperties();
        this.voxelHeight = toolkitProperties.getVoxelHeight();
        this.surface.clear();
        int lastY;
        int brushSize = toolkitProperties.getBrushSize();
        double brushSizeSquared = Math.pow(brushSize + 0.5, 2);
        // Are we pulling up ?
        BlockVector3 targetBlock = getTargetBlock();
        if (this.voxelHeight > 0) {
            // Z - Axis
            for (int z = -brushSize; z <= brushSize; z++) {
                int zSquared = z * z;
                int actualZ = targetBlock.getZ() + z;
                // X - Axis
                for (int x = -brushSize; x <= brushSize; x++) {
                    int xSquared = x * x;
                    int actualX = targetBlock.getX() + x;
                    // Down the Y - Axis
                    for (int y = brushSize; y >= -brushSize; y--) {
                        double volume = zSquared + xSquared + (y * y);
                        // Is this in the range of the brush?
                        if (volume <= brushSizeSquared && !getBlock(actualX, targetBlock.getY() + y, actualZ).isAir()) {
                            int actualY = targetBlock.getY() + y;
                            // Starting strength and new Position
                            double str = this.getStr(volume / brushSizeSquared);
                            int lastStr = (int) (this.voxelHeight * str);
                            lastY = actualY + lastStr;
                            setBlockType(actualX, clampY(lastY), actualZ,
                                    getBlockType(actualX, actualY, actualZ)
                            );
                            if (Double.compare(str, 1.0) == 0) {
                                str = 0.8;
                            }
                            while (lastStr > 0) {
                                if (actualY < targetBlock.getY()) {
                                    str *= str;
                                }
                                lastStr = (int) (this.voxelHeight * str);
                                int newY = actualY + lastStr;
                                BlockType blockType = getBlockType(actualX, actualY, actualZ);
                                for (int i = newY; i < lastY; i++) {
                                    setBlockType(actualX, clampY(i), actualZ, blockType);
                                }
                                lastY = newY;
                                actualY--;
                            }
                            break;
                        }
                    }
                }
            }
        } else {
            for (int z = -brushSize; z <= brushSize; z++) {
                double zSquared = Math.pow(z, 2);
                int actualZ = targetBlock.getZ() + z;
                for (int x = -brushSize; x <= brushSize; x++) {
                    double xSquared = Math.pow(x, 2);
                    int actualX = targetBlock.getX() + x;
                    for (int y = -brushSize; y <= brushSize; y++) {
                        double volume = (xSquared + Math.pow(y, 2) + zSquared);
                        if (volume <= brushSizeSquared && !getBlock(actualX, targetBlock.getY() + y, actualZ).isAir()) {
                            int actualY = targetBlock.getY() + y;
                            lastY = actualY + (int) (this.voxelHeight * this.getStr(volume / brushSizeSquared));
                            setBlockType(actualX, clampY(lastY), actualZ,
                                    getBlockType(actualX, actualY, actualZ)
                            );
                            y++;
                            double volume2 = (xSquared + Math.pow(y, 2) + zSquared);
                            while (volume2 <= brushSizeSquared) {
                                int blockY = targetBlock.getY() + y + (int) (this.voxelHeight * this.getStr(volume2 / brushSizeSquared));
                                BlockType blockType = getBlockType(actualX, targetBlock.getY() + y, actualZ);
                                for (int i = blockY; i < lastY; i++) {
                                    setBlockType(actualX, clampY(i), actualZ, blockType);
                                }
                                lastY = blockY;
                                y++;
                                volume2 = (xSquared + Math.pow(y, 2) + zSquared);
                            }
                            break;
                        }
                    }
                }
            }
        }
    }

    private double getStr(double t) {
        double lt = 1 - t;
        return (lt * lt * lt) + 3 * (lt * lt) * t * this.c1 + 3 * lt * (t * t) * this.c2; // My + (t * ((By + (t * ((c2 + (t * (0 - c2))) - By))) - My));
    }

    private void getSurface(ToolkitProperties toolkitProperties) {
        this.surface.clear();
        int brushSize = toolkitProperties.getBrushSize();
        double bSquared = Math.pow(brushSize + 0.5, 2);
        for (int z = -brushSize; z <= brushSize; z++) {
            double zSquared = Math.pow(z, 2);
            BlockVector3 targetBlock = getTargetBlock();
            int actualZ = targetBlock.getZ() + z;
            for (int x = -brushSize; x <= brushSize; x++) {
                double xSquared = Math.pow(x, 2);
                int actualX = targetBlock.getX() + x;
                for (int y = -brushSize; y <= brushSize; y++) {
                    double volume = (xSquared + Math.pow(y, 2) + zSquared);
                    if (volume <= bSquared) {
                        if (this.isSurface(actualX, targetBlock.getY() + y, actualZ)) {
                            this.surface.add(new PullBrushBlockWrapper(
                                    actualX,
                                    clampY(targetBlock.getY() + y),
                                    actualZ,
                                    this.clampY(actualX, targetBlock.getY() + y, actualZ),
                                    this.getStr(((volume / bSquared)))
                            ));
                        }
                    }
                }
            }
        }
    }

    private boolean isSurface(int x, int y, int z) {
        return !isEmpty(x, y, z) && (isEmpty(x, y - 1, z) || isEmpty(x, y + 1, z) || isEmpty(x + 1, y, z) || isEmpty(
                x - 1,
                y,
                z
        ) || isEmpty(x, y, z + 1) || isEmpty(x, y, z - 1));
    }

    private boolean isEmpty(int x, int y, int i) {
        return getBlock(x, y, i).isAir();
    }

    private void setBlock(PullBrushBlockWrapper block) {
        int blockY = clampY(block.getY() + (int) (this.voxelHeight * block.getStr()));
        if (Materials.isEmpty(getBlockType(block.getX(), block.getY() - 1, block.getZ()))) {
            setBlockData(block.getX(), blockY, block.getZ(), block.getBlockData());
            for (int y = block.getY(); y < blockY; y++) {
                setBlockType(block.getX(), y, block.getZ(), BlockTypes.AIR);
            }
        } else {
            setBlockData(block.getX(), blockY, block.getZ(), block.getBlockData());
            for (int y = block.getY() - 1; y < blockY; y++) {
                setBlockData(block.getX(), clampY(y), block.getZ(), block.getBlockData());
            }
        }
    }

    private void setBlockDown(PullBrushBlockWrapper block) {
        int blockY = clampY(block.getY() + (int) (this.voxelHeight * block.getStr()));
        setBlockData(block.getX(), blockY, block.getZ(), block.getBlockData());
        for (int y = block.getY(); y > blockY; y--) {
            this.setBlockType(block.getX(), y, block.getZ(), BlockTypes.AIR);
        }
    }

    @Override
    public void sendInfo(Snipe snipe) {
        SnipeMessenger messenger = snipe.createMessenger();
        messenger.sendBrushNameMessage();
        messenger.sendBrushSizeMessage();
        messenger.sendVoxelHeightMessage();
        messenger.sendMessage(ChatColor.AQUA + "Pinch " + (-this.c1 + 1));
        messenger.sendMessage(ChatColor.AQUA + "Bubble " + this.c2);
    }

    private static final class PullBrushBlockWrapper {

        private final int x;
        private final int y;
        private final int z;
        private final BlockState blockData;
        private final double str;

        private PullBrushBlockWrapper(int x, int y, int z, BlockState block, double str) {
            this.x = x;
            this.y = y;
            this.z = z;
            this.blockData = block;
            this.str = str;
        }

        public int getX() {
            return this.x;
        }

        public int getY() {
            return this.y;
        }

        public int getZ() {
            return this.z;
        }

        public BlockState getBlockData() {
            return this.blockData;
        }

        public double getStr() {
            return this.str;
        }

    }

}

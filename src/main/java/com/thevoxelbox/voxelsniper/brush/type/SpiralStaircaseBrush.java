package com.thevoxelbox.voxelsniper.brush.type;

import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.registry.state.Property;
import com.sk89q.worldedit.registry.state.PropertyKey;
import com.sk89q.worldedit.util.Direction;
import com.sk89q.worldedit.world.block.BlockState;
import com.sk89q.worldedit.world.block.BlockType;
import com.sk89q.worldedit.world.block.BlockTypes;
import com.thevoxelbox.voxelsniper.sniper.snipe.Snipe;
import com.thevoxelbox.voxelsniper.sniper.snipe.message.SnipeMessenger;
import com.thevoxelbox.voxelsniper.sniper.toolkit.ToolkitProperties;
import org.bukkit.ChatColor;

import java.util.stream.Stream;

public class SpiralStaircaseBrush extends AbstractBrush {

    private String stairType = "block"; // "block" 1x1 blocks (default), "step" alternating step double step, "stair" staircase with blocks on corners
    private String sdirect = "c"; // "c" clockwise (default), "cc" counter-clockwise
    private String sopen = "n"; // "n" north (default), "e" east, "world" south, "world" west

    @Override
    public void handleCommand(String[] parameters, Snipe snipe) {
        SnipeMessenger messenger = snipe.createMessenger();
        if (parameters[0].equalsIgnoreCase("info")) {
            messenger.sendMessage(ChatColor.GOLD + "Spiral Staircase Parameters:");
            messenger.sendMessage(ChatColor.AQUA + "/b sstair 'block' (default) | 'step' | 'woodstair' | 'cobblestair' -- set the type of staircase");
            messenger.sendMessage(ChatColor.AQUA + "/b sstair 'c' (default) | 'cc' -- set the turning direction of staircase");
            messenger.sendMessage(ChatColor.AQUA + "/b sstair 'n' (default) | 'e' | 's' | 'world' -- set the opening direction of staircase");
            return;
        }
        for (String parameter : parameters) {
            if (Stream.of("block", "step", "woodstair", "cobblestair")
                    .anyMatch(parameter::equalsIgnoreCase)) {
                this.stairType = parameter;
                messenger.sendMessage(ChatColor.BLUE + "Staircase type: " + this.stairType);
            } else if (parameter.equalsIgnoreCase("c") || parameter.equalsIgnoreCase("cc")) {
                this.sdirect = parameter;
                messenger.sendMessage(ChatColor.BLUE + "Staircase turns: " + this.sdirect);
            } else if (Stream.of("n", "e", "s", "world")
                    .anyMatch(parameter::equalsIgnoreCase)) {
                this.sopen = parameter;
                messenger.sendMessage(ChatColor.BLUE + "Staircase opens: " + this.sopen);
            } else {
                messenger.sendMessage(ChatColor.RED + "Invalid brush parameters! use the info parameter to display parameter info.");
            }
        }
    }

    @Override
    public void handleArrowAction(Snipe snipe) {
        BlockVector3 targetBlock = getTargetBlock();
        digStairWell(snipe, targetBlock); // make stairwell below target
    }

    @Override
    public void handleGunpowderAction(Snipe snipe) {
        BlockVector3 lastBlock = getLastBlock();
        buildStairWell(snipe, lastBlock); // make stairwell above target
    }

    private void buildStairWell(Snipe snipe, BlockVector3 targetBlock) {
        ToolkitProperties toolkitProperties = snipe.getToolkitProperties();
        SnipeMessenger messenger = snipe.createMessenger();
        int voxelHeight = toolkitProperties.getVoxelHeight();
        if (voxelHeight < 1) {
            toolkitProperties.setVoxelHeight(1);
            messenger.sendMessage(ChatColor.RED + "VoxelHeight must be a natural number! Set to 1.");
        }
        int brushSize = toolkitProperties.getBrushSize();
        // locate first block in staircase
        // Note to self, fix these
        int startX;
        int startZ;
        if (this.sdirect.equalsIgnoreCase("cc")) {
            if (this.sopen.equalsIgnoreCase("n")) {
                startX = 0;
                startZ = 2 * brushSize;
            } else if (this.sopen.equalsIgnoreCase("e")) {
                startX = 0;
                startZ = 0;
            } else if (this.sopen.equalsIgnoreCase("s")) {
                startX = 2 * brushSize;
                startZ = 0;
            } else {
                startX = 2 * brushSize;
                startZ = 2 * brushSize;
            }
        } else {
            if (this.sopen.equalsIgnoreCase("n")) {
                startX = 0;
                startZ = 0;
            } else if (this.sopen.equalsIgnoreCase("e")) {
                startX = 2 * brushSize;
                startZ = 0;
            } else if (this.sopen.equalsIgnoreCase("s")) {
                startX = 2 * brushSize;
                startZ = 2 * brushSize;
            } else {
                startX = 0;
                startZ = 2 * brushSize;
            }
        }
        int toggle = 0;
        int zOffset = 0;
        int xOffset = 0;
        int y = 0;
        int[][][] spiral = new int[2 * brushSize + 1][voxelHeight][2 * brushSize + 1];
        while (y < voxelHeight) {
            if (this.stairType.equalsIgnoreCase("block")) {
                // 1x1x1 voxel material steps
                spiral[startX + xOffset][y][startZ + zOffset] = 1;
                y++;
            } else if (this.stairType.equalsIgnoreCase("step")) {
                // alternating step-doublestep, uses data value to determine type
                switch (toggle) {
                    case 0:
                    case 1:
                        toggle = 2;
                        spiral[startX + xOffset][y][startZ + zOffset] = 1;
                        break;
                    case 2:
                        toggle = 1;
                        spiral[startX + xOffset][y][startZ + zOffset] = 2;
                        y++;
                        break;
                    default:
                        break;
                }
            }
            // Adjust horizontal position and do stair-option array stuff
            if (startX + xOffset == 0) { // All North
                if (startZ + zOffset == 0) { // NORTHEAST
                    if (this.stairType.equalsIgnoreCase("woodstair") || this.stairType.equalsIgnoreCase("cobblestair")) {
                        spiral[startX + xOffset][y][startZ + zOffset] = 1;
                    }
                    if (this.sdirect.equalsIgnoreCase("c")) {
                        xOffset++;
                    } else {
                        zOffset++;
                    }
                } else if (startZ + zOffset == 2 * brushSize) { // NORTHWEST
                    if (this.stairType.equalsIgnoreCase("woodstair") || this.stairType.equalsIgnoreCase("cobblestair")) {
                        spiral[startX + xOffset][y][startZ + zOffset] = 1;
                    }
                    if (this.sdirect.equalsIgnoreCase("c")) {
                        zOffset--;
                    } else {
                        xOffset++;
                    }
                } else { // JUST PLAIN NORTH
                    if (this.sdirect.equalsIgnoreCase("c")) {
                        if (this.stairType.equalsIgnoreCase("woodstair") || this.stairType.equalsIgnoreCase("cobblestair")) {
                            spiral[startX + xOffset][y][startZ + zOffset] = 5;
                            y++;
                        }
                        zOffset--;
                    } else {
                        if (this.stairType.equalsIgnoreCase("woodstair") || this.stairType.equalsIgnoreCase("cobblestair")) {
                            spiral[startX + xOffset][y][startZ + zOffset] = 4;
                            y++;
                        }
                        zOffset++;
                    }
                }
            } else if (startX + xOffset == 2 * brushSize) { // ALL SOUTH
                if (startZ + zOffset == 0) { // SOUTHEAST
                    if (this.stairType.equalsIgnoreCase("woodstair") || this.stairType.equalsIgnoreCase("cobblestair")) {
                        spiral[startX + xOffset][y][startZ + zOffset] = 1;
                    }
                    if (this.sdirect.equalsIgnoreCase("c")) {
                        zOffset++;
                    } else {
                        xOffset--;
                    }
                } else if (startZ + zOffset == 2 * brushSize) { // SOUTHWEST
                    if (this.stairType.equalsIgnoreCase("woodstair") || this.stairType.equalsIgnoreCase("cobblestair")) {
                        spiral[startX + xOffset][y][startZ + zOffset] = 1;
                    }
                    if (this.sdirect.equalsIgnoreCase("c")) {
                        xOffset--;
                    } else {
                        zOffset--;
                    }
                } else { // JUST PLAIN SOUTH
                    if (this.sdirect.equalsIgnoreCase("c")) {
                        if (this.stairType.equalsIgnoreCase("woodstair") || this.stairType.equalsIgnoreCase("cobblestair")) {
                            spiral[startX + xOffset][y][startZ + zOffset] = 4;
                            y++;
                        }
                        zOffset++;
                    } else {
                        if (this.stairType.equalsIgnoreCase("woodstair") || this.stairType.equalsIgnoreCase("cobblestair")) {
                            spiral[startX + xOffset][y][startZ + zOffset] = 5;
                            y++;
                        }
                        zOffset--;
                    }
                }
            } else if (startZ + zOffset == 0) { // JUST PLAIN EAST
                if (this.sdirect.equalsIgnoreCase("c")) {
                    if (this.stairType.equalsIgnoreCase("woodstair") || this.stairType.equalsIgnoreCase("cobblestair")) {
                        spiral[startX + xOffset][y][startZ + zOffset] = 2;
                        y++;
                    }
                    xOffset++;
                } else {
                    if (this.stairType.equalsIgnoreCase("woodstair") || this.stairType.equalsIgnoreCase("cobblestair")) {
                        spiral[startX + xOffset][y][startZ + zOffset] = 3;
                        y++;
                    }
                    xOffset--;
                }
            } else { // JUST PLAIN WEST
                if (this.sdirect.equalsIgnoreCase("c")) {
                    if (this.stairType.equalsIgnoreCase("woodstair") || this.stairType.equalsIgnoreCase("cobblestair")) {
                        spiral[startX + xOffset][y][startZ + zOffset] = 3;
                        y++;
                    }
                    xOffset--;
                } else {
                    if (this.stairType.equalsIgnoreCase("woodstair") || this.stairType.equalsIgnoreCase("cobblestair")) {
                        spiral[startX + xOffset][y][startZ + zOffset] = 2;
                        y++;
                    }
                    xOffset++;
                }
            }
        }
        // Make the changes
        for (int x = 2 * brushSize; x >= 0; x--) {
            for (int i = voxelHeight - 1; i >= 0; i--) {
                for (int z = 2 * brushSize; z >= 0; z--) {
                    int blockPositionX = targetBlock.getX();
                    int blockPositionY = targetBlock.getY();
                    int blockPositionZ = targetBlock.getZ();
                    BlockVector3 position = BlockVector3.at(
                            blockPositionX - brushSize + x,
                            blockPositionY + i,
                            blockPositionZ - brushSize + z
                    );
                    BlockType blockType = getBlockType(position);
                    if (spiral[x][i][z] == 0) {
                        if (i == voxelHeight - 1) {
                            setBlockType(position, BlockTypes.AIR);
                        } else {
                            if (!((this.stairType.equalsIgnoreCase("woodstair") || this.stairType.equalsIgnoreCase("cobblestair")) && spiral[x][i + 1][z] == 1)) {
                                setBlockType(position, BlockTypes.AIR);
                            }
                        }
                    } else if (spiral[x][i][z] == 1) {
                        if (this.stairType.equalsIgnoreCase("block")) {
                            setBlockType(position, toolkitProperties.getBlockType());
                        } else if (this.stairType.equalsIgnoreCase("step")) {
                            setBlockType(position, BlockTypes.OAK_SLAB);
                            setBlockData(
                                    position.getX(),
                                    clampY(position.getY()),
                                    position.getZ(),
                                    toolkitProperties.getBlockData()
                            );
                        } else if (this.stairType.equalsIgnoreCase("woodstair") || this.stairType.equalsIgnoreCase("cobblestair")) {
                            setBlockType(
                                    blockPositionX - brushSize + x,
                                    blockPositionY + i - 1,
                                    blockPositionZ - brushSize + z,
                                    toolkitProperties.getBlockType()
                            );
                        }
                    } else if (spiral[x][i][z] == 2) {
                        if (this.stairType.equalsIgnoreCase("step")) {
                            setBlockData(position, BlockTypes.OAK_SLAB.getDefaultState().with(PropertyKey.TYPE, "double"));
                            setBlockData(
                                    position.getX(),
                                    clampY(position.getY()),
                                    position.getZ(),
                                    toolkitProperties.getBlockData()
                            );
                        } else if (this.stairType.equalsIgnoreCase("woodstair")) {
                            setBlockType(position, BlockTypes.OAK_STAIRS);
                            setBlockData(
                                    position.getX(),
                                    clampY(position.getY()),
                                    position.getZ(),
                                    BlockTypes.OAK_STAIRS.getDefaultState().with(PropertyKey.FACING, "east")
                            );
                        } else if (this.stairType.equalsIgnoreCase("cobblestair")) {
                            setBlockType(position, BlockTypes.COBBLESTONE_STAIRS);
                            setBlockData(
                                    position.getX(),
                                    clampY(position.getY()),
                                    position.getZ(),
                                    BlockTypes.COBBLESTONE_STAIRS.getDefaultState().with(PropertyKey.FACING, "east")
                            );
                        }
                    } else {
                        if (this.stairType.equalsIgnoreCase("woodstair")) {
                            setBlockType(position, BlockTypes.OAK_STAIRS);
                            setStairsDirection(position, spiral[x][i][z] - 2);
                        } else if (this.stairType.equalsIgnoreCase("cobblestair")) {
                            setBlockType(position, BlockTypes.COBBLESTONE_STAIRS);
                            setStairsDirection(position, spiral[x][i][z] - 2);
                        }
                    }
                }
            }
        }
    }

    private void digStairWell(Snipe snipe, BlockVector3 targetBlock) {
        ToolkitProperties toolkitProperties = snipe.getToolkitProperties();
        SnipeMessenger messenger = snipe.createMessenger();
        int voxelHeight = toolkitProperties.getVoxelHeight();
        if (voxelHeight < 1) {
            toolkitProperties.setVoxelHeight(1);
            messenger.sendMessage(ChatColor.RED + "VoxelHeight must be a natural number! Set to 1.");
        }
        // initialize array
        int brushSize = toolkitProperties.getBrushSize();
        // locate first block in staircase
        // Note to self, fix these
        int startX;
        int startZ;
        if (this.sdirect.equalsIgnoreCase("cc")) {
            if (this.sopen.equalsIgnoreCase("n")) {
                startX = 0;
                startZ = 2 * brushSize;
            } else if (this.sopen.equalsIgnoreCase("e")) {
                startX = 0;
                startZ = 0;
            } else if (this.sopen.equalsIgnoreCase("s")) {
                startX = 2 * brushSize;
                startZ = 0;
            } else {
                startX = 2 * brushSize;
                startZ = 2 * brushSize;
            }
        } else {
            if (this.sopen.equalsIgnoreCase("n")) {
                startX = 0;
                startZ = 0;
            } else if (this.sopen.equalsIgnoreCase("e")) {
                startX = 2 * brushSize;
                startZ = 0;
            } else if (this.sopen.equalsIgnoreCase("s")) {
                startX = 2 * brushSize;
                startZ = 2 * brushSize;
            } else {
                startX = 0;
                startZ = 2 * brushSize;
            }
        }
        int toggle = 0;
        int zOffset = 0;
        int xOffset = 0;
        int y = 0;
        int[][][] spiral = new int[2 * brushSize + 1][voxelHeight][2 * brushSize + 1];
        while (y < voxelHeight) {
            if (this.stairType.equalsIgnoreCase("block")) {
                // 1x1x1 voxel material steps
                spiral[startX + xOffset][y][startZ + zOffset] = 1;
                y++;
            } else if (this.stairType.equalsIgnoreCase("step")) {
                // alternating step-doublestep, uses data value to determine type
                switch (toggle) {
                    case 0:
                    case 1:
                        toggle = 2;
                        spiral[startX + xOffset][y][startZ + zOffset] = 2;
                        break;
                    case 2:
                        toggle = 1;
                        spiral[startX + xOffset][y][startZ + zOffset] = 1;
                        y++;
                        break;
                    default:
                        break;
                }
            }
            // Adjust horizontal position and do stair-option array stuff
            if (startX + xOffset == 0) { // All North
                if (startZ + zOffset == 0) { // NORTHEAST
                    if (this.stairType.equalsIgnoreCase("woodstair") || this.stairType.equalsIgnoreCase("cobblestair")) {
                        spiral[startX + xOffset][y][startZ + zOffset] = 1;
                    }
                    if (this.sdirect.equalsIgnoreCase("c")) {
                        xOffset++;
                    } else {
                        zOffset++;
                    }
                } else if (startZ + zOffset == 2 * brushSize) { // NORTHWEST
                    if (this.stairType.equalsIgnoreCase("woodstair") || this.stairType.equalsIgnoreCase("cobblestair")) {
                        spiral[startX + xOffset][y][startZ + zOffset] = 1;
                    }
                    if (this.sdirect.equalsIgnoreCase("c")) {
                        zOffset--;
                    } else {
                        xOffset++;
                    }
                } else { // JUST PLAIN NORTH
                    if (this.sdirect.equalsIgnoreCase("c")) {
                        if (this.stairType.equalsIgnoreCase("woodstair") || this.stairType.equalsIgnoreCase("cobblestair")) {
                            spiral[startX + xOffset][y][startZ + zOffset] = 4;
                            y++;
                        }
                        zOffset--;
                    } else {
                        if (this.stairType.equalsIgnoreCase("woodstair") || this.stairType.equalsIgnoreCase("cobblestair")) {
                            spiral[startX + xOffset][y][startZ + zOffset] = 5;
                            y++;
                        }
                        zOffset++;
                    }
                }
            } else if (startX + xOffset == 2 * brushSize) { // ALL SOUTH
                if (startZ + zOffset == 0) { // SOUTHEAST
                    if (this.stairType.equalsIgnoreCase("woodstair") || this.stairType.equalsIgnoreCase("cobblestair")) {
                        spiral[startX + xOffset][y][startZ + zOffset] = 1;
                    }
                    if (this.sdirect.equalsIgnoreCase("c")) {
                        zOffset++;
                    } else {
                        xOffset--;
                    }
                } else if (startZ + zOffset == 2 * brushSize) { // SOUTHWEST
                    if (this.stairType.equalsIgnoreCase("woodstair") || this.stairType.equalsIgnoreCase("cobblestair")) {
                        spiral[startX + xOffset][y][startZ + zOffset] = 1;
                    }
                    if (this.sdirect.equalsIgnoreCase("c")) {
                        xOffset--;
                    } else {
                        zOffset--;
                    }
                } else { // JUST PLAIN SOUTH
                    if (this.sdirect.equalsIgnoreCase("c")) {
                        if (this.stairType.equalsIgnoreCase("woodstair") || this.stairType.equalsIgnoreCase("cobblestair")) {
                            spiral[startX + xOffset][y][startZ + zOffset] = 5;
                            y++;
                        }
                        zOffset++;
                    } else {
                        if (this.stairType.equalsIgnoreCase("woodstair") || this.stairType.equalsIgnoreCase("cobblestair")) {
                            spiral[startX + xOffset][y][startZ + zOffset] = 4;
                            y++;
                        }
                        zOffset--;
                    }
                }
            } else if (startZ + zOffset == 0) { // JUST PLAIN EAST
                if (this.sdirect.equalsIgnoreCase("c")) {
                    if (this.stairType.equalsIgnoreCase("woodstair") || this.stairType.equalsIgnoreCase("cobblestair")) {
                        spiral[startX + xOffset][y][startZ + zOffset] = 3;
                        y++;
                    }
                    xOffset++;
                } else {
                    if (this.stairType.equalsIgnoreCase("woodstair") || this.stairType.equalsIgnoreCase("cobblestair")) {
                        spiral[startX + xOffset][y][startZ + zOffset] = 2;
                        y++;
                    }
                    xOffset--;
                }
            } else { // JUST PLAIN WEST
                if (this.sdirect.equalsIgnoreCase("c")) {
                    if (this.stairType.equalsIgnoreCase("woodstair") || this.stairType.equalsIgnoreCase("cobblestair")) {
                        spiral[startX + xOffset][y][startZ + zOffset] = 2;
                        y++;
                    }
                    xOffset--;
                } else {
                    if (this.stairType.equalsIgnoreCase("woodstair") || this.stairType.equalsIgnoreCase("cobblestair")) {
                        spiral[startX + xOffset][y][startZ + zOffset] = 3;
                        y++;
                    }
                    xOffset++;
                }
            }
        }
        // Make the changes
        for (int x = 2 * brushSize; x >= 0; x--) {
            for (int i = voxelHeight - 1; i >= 0; i--) {
                for (int z = 2 * brushSize; z >= 0; z--) {
                    int blockPositionX = targetBlock.getX();
                    int blockPositionY = targetBlock.getY();
                    int blockPositionZ = targetBlock.getZ();
                    BlockVector3 position = BlockVector3.at(
                            blockPositionX - brushSize + x,
                            blockPositionY - i,
                            blockPositionZ - brushSize + z
                    );
                    BlockType blockType = getBlockType(position);
                    if (spiral[x][i][z] == 0) {
                        setBlockType(position, BlockTypes.AIR);
                    } else if (spiral[x][i][z] == 1) {
                        if (this.stairType.equalsIgnoreCase("block")) {
                            setBlockType(position, toolkitProperties.getBlockType());
                        } else if (this.stairType.equalsIgnoreCase("step")) {
                            setBlockType(position, BlockTypes.OAK_SLAB);
                            setBlockData(
                                    position.getX(),
                                    clampY(position.getY()),
                                    position.getZ(),
                                    toolkitProperties.getBlockData()
                            );
                        } else if (this.stairType.equalsIgnoreCase("woodstair") || this.stairType.equalsIgnoreCase("cobblestair")) {
                            setBlockType(position, toolkitProperties.getBlockType());
                        }
                    } else if (spiral[x][i][z] == 2) {
                        if (this.stairType.equalsIgnoreCase("step")) {
                            setBlockData(position, BlockTypes.OAK_SLAB.getDefaultState().with(PropertyKey.TYPE, "double"));
                            setBlockData(
                                    position.getX(),
                                    clampY(position.getY()),
                                    position.getZ(),
                                    toolkitProperties.getBlockData()
                            );
                        } else if (this.stairType.equalsIgnoreCase("woodstair")) {
                            setBlockType(position, BlockTypes.OAK_STAIRS);
                            setBlockData(
                                    position.getX(),
                                    clampY(position.getY()),
                                    position.getZ(),
                                    BlockTypes.OAK_STAIRS.getDefaultState().with(PropertyKey.FACING, "east")
                            );
                        } else if (this.stairType.equalsIgnoreCase("cobblestair")) {
                            setBlockType(position, BlockTypes.COBBLESTONE_STAIRS);
                            setBlockData(
                                    position.getX(),
                                    clampY(position.getY()),
                                    position.getZ(),
                                    BlockTypes.COBBLESTONE_STAIRS.getDefaultState().with(PropertyKey.FACING, "east")
                            );
                        }
                    } else {
                        if (this.stairType.equalsIgnoreCase("woodstair")) {
                            setBlockType(position, BlockTypes.OAK_STAIRS);
                            setStairsDirection(position, spiral[x][i][z] - 2);
                        } else if (this.stairType.equalsIgnoreCase("cobblestair")) {
                            setBlockType(position, BlockTypes.COBBLESTONE_STAIRS);
                            setStairsDirection(position, spiral[x][i][z] - 2);
                        }
                    }
                }
            }
        }
    }

    private void setStairsDirection(BlockVector3 position, int data) {
        BlockState blockData = getBlock(position);
        BlockType type = blockData.getBlockType();
        Property<Direction> directionProperty = type.getProperty("facing");
        if (directionProperty == null) {
            return;
        }
        blockData = blockData.with(directionProperty, dataToDirection(data));
        setBlockData(position, blockData);
    }

    private Direction dataToDirection(int data) {
        switch (data) {
            case 3:
                return Direction.NORTH;
            case 2:
                return Direction.SOUTH;
            case 1:
                return Direction.WEST;
            case 0:
            default:
                return Direction.EAST;
        }
    }

    @Override
    public void sendInfo(Snipe snipe) {
        SnipeMessenger messenger = snipe.createMessenger();
        messenger.sendBrushNameMessage();
        messenger.sendBrushSizeMessage();
        messenger.sendBlockTypeMessage();
        messenger.sendVoxelHeightMessage();
        messenger.sendBlockDataMessage();
        messenger.sendMessage(ChatColor.BLUE + "Staircase type: " + this.stairType);
        messenger.sendMessage(ChatColor.BLUE + "Staircase turns: " + this.sdirect);
        messenger.sendMessage(ChatColor.BLUE + "Staircase opens: " + this.sopen);
    }

}

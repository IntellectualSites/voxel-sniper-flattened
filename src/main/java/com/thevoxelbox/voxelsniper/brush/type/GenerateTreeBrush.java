package com.thevoxelbox.voxelsniper.brush.type;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.world.block.BlockCategories;
import com.sk89q.worldedit.world.block.BlockState;
import com.sk89q.worldedit.world.block.BlockType;
import com.sk89q.worldedit.world.block.BlockTypes;
import com.thevoxelbox.voxelsniper.sniper.snipe.Snipe;
import com.thevoxelbox.voxelsniper.sniper.snipe.message.SnipeMessenger;
import com.thevoxelbox.voxelsniper.util.material.MaterialSet;
import com.thevoxelbox.voxelsniper.util.material.MaterialSets;
import org.bukkit.ChatColor;
import org.bukkit.Material;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

// Proposal: Use /v and /vr for leave and wood material // or two more parameters -- Monofraps
public class GenerateTreeBrush extends AbstractBrush {

    // Tree Variables.
    private final Random randGenerator = new Random();
    private final List<BlockVector3> branchBlocksLocation = new ArrayList<>();
    // If these default values are edited. Remember to change default values in the default preset.
    private BlockType leafType = BlockTypes.OAK_LEAVES;
    private BlockType woodType = BlockTypes.OAK_LOG;
    private boolean rootFloat;
    private int startHeight;
    private int rootLength = 9;
    private int maxRoots = 2;
    private int minRoots = 1;
    private int thickness = 1;
    private int slopeChance = 40;
    private int heightMinimum = 14;
    private int heightMaximum = 18;
    private int branchLength = 8;
    private int nodeMax = 4;
    private int nodeMin = 3;
    private int blockPositionX;
    private int blockPositionY;
    private int blockPositionZ;

    @Override
    public void handleCommand(String[] parameters, Snipe snipe) {
        SnipeMessenger messenger = snipe.createMessenger();
        for (String parameter : parameters) {
            try {
                if (parameter.equalsIgnoreCase("info")) {
                    snipe.createMessageSender()
                            .message(ChatColor.GOLD + "This brush takes the following parameters:")
                            .message(ChatColor.AQUA + "lt* - leaf type (e.g. oak)")
                            .message(ChatColor.AQUA + "wt* - wood type (e.g. oak)")
                            .message(ChatColor.AQUA + "tt# - tree thickness (whole number)")
                            .message(ChatColor.AQUA + "rf* - root float (true or false)")
                            .message(ChatColor.AQUA + "sh# - starting height (whole number)")
                            .message(ChatColor.AQUA + "rl# - root length (whole number)")
                            .message(ChatColor.AQUA + "ts# - trunk slope chance (0-100)")
                            .message(ChatColor.AQUA + "bl# - branch length (whole number)")
                            .message(ChatColor.AQUA + "info2 - more parameters")
                            .send();
                    return;
                }
                if (parameter.equalsIgnoreCase("info2")) {
                    snipe.createMessageSender()
                            .message(ChatColor.GOLD + "This brush takes the following parameters:")
                            .message(ChatColor.AQUA + "minr# - minimum roots (whole number)")
                            .message(ChatColor.AQUA + "maxr# - maximum roots (whole number)")
                            .message(ChatColor.AQUA + "minh# - minimum height (whole number)")
                            .message(ChatColor.AQUA + "maxh# - maximum height (whole number)")
                            .message(ChatColor.AQUA + "minl# - minimum leaf node size (whole number)")
                            .message(ChatColor.AQUA + "maxl# - maximum leaf node size (whole number)")
                            .message(ChatColor.AQUA + "default - restore default params")
                            .send();
                    return;
                }
                if (parameter.startsWith("lt")) { // Leaf Type
                    Material leafType = Material.matchMaterial(parameter.replace("lt", "") + "_leaves");
                    if (leafType == null) {
                        messenger.sendMessage(ChatColor.RED + "Invalid leaf type");
                        return;
                    }
                    this.leafType = BukkitAdapter.asBlockType(leafType);
                    messenger.sendMessage(ChatColor.BLUE + "Leaf Type set to " + this.leafType);
                } else if (parameter.startsWith("wt")) { // Wood Type
                    Material woodType = Material.matchMaterial(parameter.replace("wt", "") + "_log");
                    if (woodType == null) {
                        messenger.sendMessage(ChatColor.RED + "Invalid wood type");
                        return;
                    }
                    this.woodType = BukkitAdapter.asBlockType(woodType);
                    messenger.sendMessage(ChatColor.BLUE + "Wood Type set to " + this.woodType);
                } else if (parameter.startsWith("tt")) { // Tree Thickness
                    this.thickness = Integer.parseInt(parameter.replace("tt", ""));
                    messenger.sendMessage(ChatColor.BLUE + "Thickness set to " + this.thickness);
                } else if (parameter.startsWith("rf")) { // Root Float
                    this.rootFloat = Boolean.parseBoolean(parameter.replace("rf", ""));
                    messenger.sendMessage(ChatColor.BLUE + "Floating Roots set to " + this.rootFloat);
                } else if (parameter.startsWith("sh")) { // Starting Height
                    this.startHeight = Integer.parseInt(parameter.replace("sh", ""));
                    messenger.sendMessage(ChatColor.BLUE + "Starting Height set to " + this.startHeight);
                } else if (parameter.startsWith("rl")) { // Root Length
                    this.rootLength = Integer.parseInt(parameter.replace("rl", ""));
                    messenger.sendMessage(ChatColor.BLUE + "Root Length set to " + this.rootLength);
                } else if (parameter.startsWith("minr")) { // Minimum Roots
                    this.minRoots = Integer.parseInt(parameter.replace("minr", ""));
                    if (this.minRoots > this.maxRoots) {
                        this.minRoots = this.maxRoots;
                        messenger.sendMessage(ChatColor.RED + "Minimum Roots can't exceed Maximum Roots, has  been set to " + this.minRoots + " Instead!");
                    } else {
                        messenger.sendMessage(ChatColor.BLUE + "Minimum Roots set to " + this.minRoots);
                    }
                } else if (parameter.startsWith("maxr")) { // Maximum Roots
                    this.maxRoots = Integer.parseInt(parameter.replace("maxr", ""));
                    if (this.minRoots > this.maxRoots) {
                        this.maxRoots = this.minRoots;
                        messenger.sendMessage(ChatColor.RED + "Maximum Roots can't be lower than Minimum Roots, has been set to " + this.minRoots + " Instead!");
                    } else {
                        messenger.sendMessage(ChatColor.BLUE + "Maximum Roots set to " + this.maxRoots);
                    }
                } else if (parameter.startsWith("ts")) { // Trunk Slope Chance
                    this.slopeChance = Integer.parseInt(parameter.replace("ts", ""));
                    messenger.sendMessage(ChatColor.BLUE + "Trunk Slope set to " + this.slopeChance);
                } else if (parameter.startsWith("minh")) { // Height Minimum
                    this.heightMinimum = Integer.parseInt(parameter.replace("minh", ""));
                    if (this.heightMinimum > this.heightMaximum) {
                        this.heightMinimum = this.heightMaximum;
                        messenger.sendMessage(ChatColor.RED + "Minimum Height exceed than Maximum Height, has been set to " + this.heightMinimum + " Instead!");
                    } else {
                        messenger.sendMessage(ChatColor.BLUE + "Minimum Height set to " + this.heightMinimum);
                    }
                } else if (parameter.startsWith("maxh")) { // Height Maximum
                    this.heightMaximum = Integer.parseInt(parameter.replace("maxh", ""));
                    if (this.heightMinimum > this.heightMaximum) {
                        this.heightMaximum = this.heightMinimum;
                        messenger.sendMessage(ChatColor.RED + "Maximum Height can't be lower than Minimum Height, has been set to " + this.heightMaximum + " Instead!");
                    } else {
                        messenger.sendMessage(ChatColor.BLUE + "Maximum Roots set to " + this.heightMaximum);
                    }
                } else if (parameter.startsWith("bl")) { // Branch Length
                    this.branchLength = Integer.parseInt(parameter.replace("bl", ""));
                    messenger.sendMessage(ChatColor.BLUE + "Branch Length set to " + this.branchLength);
                } else if (parameter.startsWith("maxl")) { // Leaf Node Max Size
                    this.nodeMax = Integer.parseInt(parameter.replace("maxl", ""));
                    messenger.sendMessage(ChatColor.BLUE + "Leaf Max Thickness set to " + this.nodeMax + " (Default 4)");
                } else if (parameter.startsWith("minl")) { // Leaf Node Min Size
                    this.nodeMin = Integer.parseInt(parameter.replace("minl", ""));
                    messenger.sendMessage(ChatColor.BLUE + "Leaf Min Thickness set to " + this.nodeMin + " (Default 3)");
                    // -------
                    // Presets
                    // -------
                } else if (parameter.startsWith("default")) { // Default settings.
                    this.leafType = BlockTypes.OAK_LEAVES;
                    this.woodType = BlockTypes.OAK_LOG;
                    this.rootFloat = false;
                    this.startHeight = 0;
                    this.rootLength = 9;
                    this.maxRoots = 2;
                    this.minRoots = 1;
                    this.thickness = 1;
                    this.slopeChance = 40;
                    this.heightMinimum = 14;
                    this.heightMaximum = 18;
                    this.branchLength = 8;
                    this.nodeMax = 4;
                    this.nodeMin = 3;
                    messenger.sendMessage(ChatColor.GOLD + "Brush reset to default parameters.");
                } else {
                    messenger.sendMessage(ChatColor.RED + "Invalid brush parameters! use the info parameter to display parameter info.");
                }
            } catch (NumberFormatException exception) {
                messenger.sendMessage(ChatColor.RED + "Invalid brush parameters! \"" + parameter + "\" is not a valid statement. Please use the 'info' parameter to display parameter info.");
            }
        }
    }

    @Override
    public void handleArrowAction(Snipe snipe) {
        this.branchBlocksLocation.clear();
        // Sets the location variables.
        BlockVector3 targetBlock = this.getTargetBlock();
        this.blockPositionX = targetBlock.getX();
        this.blockPositionY = targetBlock.getY() + this.startHeight;
        this.blockPositionZ = targetBlock.getZ();
        // Generates the roots.
        rootGen();
        // Generates the trunk, which also generates branches.
        generateTrunk();
        // Each branch block was saved in an array. This is now fed through an array.
        // This array takes each branch block and constructs a leaf node around it.
        for (BlockVector3 blockLocation : this.branchBlocksLocation) {
            this.blockPositionX = blockLocation.getX();
            this.blockPositionY = blockLocation.getY();
            this.blockPositionZ = blockLocation.getZ();
            this.createLeafNode();
        }
    }
    // The Powder currently does nothing extra.

    @Override
    public void handleGunpowderAction(Snipe snipe) {
        handleArrowAction(snipe);
    }
    // Branch Creation based on direction chosen from the parameters passed.

    private void createBranch(int xDirection, int zDirection) {
        // Sets branch origin.
        int originX = this.blockPositionX;
        int originY = this.blockPositionY;
        int originZ = this.blockPositionZ;
        // Sets direction preference.
        int xPreference = this.randGenerator.nextInt(60) + 20;
        int zPreference = this.randGenerator.nextInt(60) + 20;
        // Iterates according to branch length.
        for (int r = 0; r < this.branchLength; r++) {
            // Alters direction according to preferences.
            if (this.randGenerator.nextInt(100) < xPreference) {
                this.blockPositionX += xDirection;
            }
            if (this.randGenerator.nextInt(100) < zPreference) {
                this.blockPositionZ += zDirection;
            }
            // 50% chance to increase elevation every second block.
            if (Math.abs(r % 2) == 1) {
                this.blockPositionY += this.randGenerator.nextInt(2);
            }
            // Creates a branch block.
            setBlockType(this.blockPositionX, clampY(this.blockPositionY), this.blockPositionZ, woodType);
            this.branchBlocksLocation.add(BlockVector3.at(this.blockPositionX, clampY(this.blockPositionY), this.blockPositionZ));
        }
        // Resets the origin
        this.blockPositionX = originX;
        this.blockPositionY = originY;
        this.blockPositionZ = originZ;
    }

    private void createLeafNode() {
        // Generates the node size.
        int nodeRadius = this.randGenerator.nextInt(this.nodeMax - this.nodeMin + 1) + this.nodeMin;
        // Lowers the current block in order to start at the bottom of the node.
        this.blockPositionY -= 2;
        double bSquared = Math.pow(nodeRadius + 0.5, 2);
        for (int z = nodeRadius; z >= 0; z--) {
            double zSquared = Math.pow(z, 2);
            for (int x = nodeRadius; x >= 0; x--) {
                double xSquared = Math.pow(x, 2);
                for (int y = nodeRadius; y >= 0; y--) {
                    if ((xSquared + Math.pow(y, 2) + zSquared) <= bSquared) {
                        // Chance to skip creation of a block.
                        generateLeafNodeBlock(this.blockPositionX + x, this.blockPositionY + y, this.blockPositionZ + z);
                        generateLeafNodeBlock(this.blockPositionX + x, this.blockPositionY + y, this.blockPositionZ - z);
                        generateLeafNodeBlock(this.blockPositionX - x, this.blockPositionY + y, this.blockPositionZ + z);
                        generateLeafNodeBlock(this.blockPositionX - x, this.blockPositionY + y, this.blockPositionZ - z);
                        generateLeafNodeBlock(this.blockPositionX + x, this.blockPositionY - y, this.blockPositionZ + z);
                        generateLeafNodeBlock(this.blockPositionX + x, this.blockPositionY - y, this.blockPositionZ - z);
                        generateLeafNodeBlock(this.blockPositionX - x, this.blockPositionY - y, this.blockPositionZ + z);
                        generateLeafNodeBlock(this.blockPositionX - x, this.blockPositionY - y, this.blockPositionZ - z);
                    }
                }
            }
        }
    }

    private void generateLeafNodeBlock(int x, int y, int z) {
        if (this.randGenerator.nextInt(100) >= 30) {
            // If block is Air, create a leaf block.
            BlockState block = getBlock(x, y, z);
            if (block.isAir()) {
                // Creates block.
                setBlockType(x, clampY(y), z, this.leafType);
            }
        }
    }

    /**
     * Code Concerning Root Generation.
     */

    private void createRoot(int xDirection, int zDirection) {
        // Sets Origin.
        int originX = this.blockPositionX;
        int originY = this.blockPositionY;
        int originZ = this.blockPositionZ;
        // Generates the number of roots to create.
        int roots = this.randGenerator.nextInt(this.maxRoots - this.minRoots + 1) + this.minRoots;
        // A roots preference to move along the X and Y axis.
        // Loops for each root to be created.
        for (int i = 0; i < roots; i++) {
            // Pushes the root'world starting point out from the center of the tree.
            for (int t = 0; t < this.thickness - 1; t++) {
                this.blockPositionX += xDirection;
                this.blockPositionZ += zDirection;
            }
            // Generate directional preference between 30% and 70%
            int xPreference = this.randGenerator.nextInt(30) + 40;
            int zPreference = this.randGenerator.nextInt(30) + 40;
            for (int j = 0; j < this.rootLength; j++) {
                // For the purposes of this algorithm, logs aren't considered solid.
                // If not solid then...
                if (BlockCategories.LOGS.contains(getBlockType(this.blockPositionX, this.blockPositionY, this.blockPositionZ))) {
                    // If solid then...
                    // End loop
                    break;
                } else {
                    // Place log block.
                    setBlockType(this.blockPositionX, clampY(this.blockPositionY), this.blockPositionZ, this.woodType);
                }
                MaterialSet solids = MaterialSet.builder()
                        .with(BlockCategories.LOGS)
                        .with(MaterialSets.AIRS)
                        .add(BlockTypes.WATER)
                        .add(BlockTypes.SNOW)
                        .build();
                // Checks is block below is solid
                if (solids.contains(clampY(this.blockPositionX, this.blockPositionY - 1, this.blockPositionZ))) {
                    // Mos down if solid.
                    this.blockPositionY -= 1;
                    if (this.rootFloat) {
                        if (this.randGenerator.nextInt(100) < xPreference) {
                            this.blockPositionX += xDirection;
                        }
                        if (this.randGenerator.nextInt(100) < zPreference) {
                            this.blockPositionZ += zDirection;
                        }
                    }
                } else {
                    // If solid then move.
                    if (this.randGenerator.nextInt(100) < xPreference) {
                        this.blockPositionX += xDirection;
                    }
                    if (this.randGenerator.nextInt(100) < zPreference) {
                        this.blockPositionZ += zDirection;
                    }
                    // Checks if new location is solid, if not then move down.
                    if (solids.contains(clampY(this.blockPositionX, this.blockPositionY - 1, this.blockPositionZ))) {
                        this.blockPositionY -= 1;
                    }
                }
            }
            // Reset origin.
            this.blockPositionX = originX;
            this.blockPositionY = originY;
            this.blockPositionZ = originZ;
        }
    }

    private void rootGen() {
        // Quadrant 1
        this.createRoot(1, 1);
        // Quadrant 2
        this.createRoot(-1, 1);
        // Quadrant 3
        this.createRoot(1, -1);
        // Quadrant 4
        this.createRoot(-1, -1);
    }

    private void createTrunk() {
        // Creates true circle discs of the set size using the wood type selected.
        double bSquared = Math.pow(this.thickness + 0.5, 2);
        for (int x = this.thickness; x >= 0; x--) {
            double xSquared = Math.pow(x, 2);
            for (int z = this.thickness; z >= 0; z--) {
                if ((xSquared + Math.pow(z, 2)) <= bSquared) {
                    // If block is air, then create a block.
                    generateTrunkBlock(this.blockPositionX + x, this.blockPositionZ + z);
                    generateTrunkBlock(this.blockPositionX + x, this.blockPositionZ - z);
                    generateTrunkBlock(this.blockPositionX - x, this.blockPositionZ + z);
                    generateTrunkBlock(this.blockPositionX - x, this.blockPositionZ - z);
                }
            }
        }
    }

    private void generateTrunkBlock(int x, int y) {
        BlockState block = getBlock(x, this.blockPositionY, y);
        if (block.isAir()) {
            // Creates block.
            setBlockType(x, clampY(this.blockPositionY), y, this.woodType);
        }
    }

    private void generateTrunk() {
        // Sets Origin
        int originX = this.blockPositionX;
        int originY = this.blockPositionY;
        int originZ = this.blockPositionZ;
        // ----------
        // Main Trunk
        // ----------
        // Sets directional preferences.
        int xPreference = this.randGenerator.nextInt(this.slopeChance);
        int zPreference = this.randGenerator.nextInt(this.slopeChance);
        // Sets direction.
        int xDirection = 1;
        if (this.randGenerator.nextInt(100) < 50) {
            xDirection = -1;
        }
        int zDirection = 1;
        if (this.randGenerator.nextInt(100) < 50) {
            zDirection = -1;
        }
        // Generates a height for trunk.
        int height = this.randGenerator.nextInt(this.heightMaximum - this.heightMinimum + 1) + this.heightMinimum;
        // This is a hidden value not available through Parameters. Otherwise messy.
        int twistChance = 5;
        for (int p = 0; p < height; p++) {
            if (p > 3) {
                if (this.randGenerator.nextInt(100) <= twistChance) {
                    xDirection *= -1;
                }
                if (this.randGenerator.nextInt(100) <= twistChance) {
                    zDirection *= -1;
                }
                if (this.randGenerator.nextInt(100) < xPreference) {
                    this.blockPositionX += xDirection;
                }
                if (this.randGenerator.nextInt(100) < zPreference) {
                    this.blockPositionZ += zDirection;
                }
            }
            // Creates trunk section
            this.createTrunk();
            // Mos up for next section
            this.blockPositionY += 1;
        }
        // Generates branches at top of trunk for each quadrant.
        this.createBranch(1, 1);
        this.createBranch(-1, 1);
        this.createBranch(1, -1);
        this.createBranch(-1, -1);
        // Reset Origin for next trunk.
        this.blockPositionX = originX;
        this.blockPositionY = originY + 4;
        this.blockPositionZ = originZ;
        // ---------------
        // Secondary Trunk
        // ---------------
        // Sets directional preferences.
        int nextXPreference = this.randGenerator.nextInt(this.slopeChance);
        int nextZPreference = this.randGenerator.nextInt(this.slopeChance);
        // Sets direction.
        int nextXDirection = 1;
        if (this.randGenerator.nextInt(100) < 50) {
            nextXDirection = -1;
        }
        int nextZDirection = 1;
        if (this.randGenerator.nextInt(100) < 50) {
            nextZDirection = -1;
        }
        // Generates a height for trunk.
        int nextHeight = this.randGenerator.nextInt(this.heightMaximum - this.heightMinimum + 1) + this.heightMinimum;
        if (nextHeight > 4) {
            for (int p = 0; p < nextHeight; p++) {
                if (this.randGenerator.nextInt(100) <= twistChance) {
                    nextXDirection *= -1;
                }
                if (this.randGenerator.nextInt(100) <= twistChance) {
                    nextZDirection *= -1;
                }
                if (this.randGenerator.nextInt(100) < nextXPreference) {
                    this.blockPositionX += nextXDirection;
                }
                if (this.randGenerator.nextInt(100) < nextZPreference) {
                    this.blockPositionZ += nextZDirection;
                }
                // Creates a trunk section
                this.createTrunk();
                // Mos up for next section
                this.blockPositionY += 1;
            }
            // Generates branches at top of trunk for each quadrant.
            createBranch(1, 1);
            createBranch(-1, 1);
            createBranch(1, -1);
            createBranch(-1, -1);
        }
    }

    @Override
    public void sendInfo(Snipe snipe) {
        SnipeMessenger messenger = snipe.createMessenger();
        messenger.sendBrushNameMessage();
    }

}

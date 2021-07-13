package com.thevoxelbox.voxelsniper.brush.type.stencil;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.world.block.BlockState;
import com.thevoxelbox.voxelsniper.VoxelSniperPlugin;
import com.thevoxelbox.voxelsniper.brush.type.AbstractBrush;
import com.thevoxelbox.voxelsniper.sniper.snipe.Snipe;
import com.thevoxelbox.voxelsniper.sniper.snipe.message.SnipeMessenger;
import com.thevoxelbox.voxelsniper.util.material.Materials;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class StencilListBrush extends AbstractBrush {

    private final Map<Integer, String> stencilList = new HashMap<>();
    private byte pasteOption = 1; // 0 = full, 1 = fill, 2 = replace
    private String filename = "NoFileLoaded";
    private short x;
    private short z;
    private short y;
    private short xRef;
    private short zRef;
    private short yRef;
    private byte pasteParam;

    @Override
    public void handleCommand(String[] parameters, Snipe snipe) {
        SnipeMessenger messenger = snipe.createMessenger();
        String secondParameter = parameters[0];
        if (secondParameter.equalsIgnoreCase("info")) {
            messenger.sendMessage(ChatColor.GOLD + "Stencil List brush Parameters:");
            messenger.sendMessage(ChatColor.AQUA + "/b schem [optional: 'full' 'fill' or 'replace', with fill as default] [name] -- Loads the specified stencil list.  Full/fill/replace must come first.  Full = paste all blocks, fill = paste only into air blocks, replace = paste full blocks in only, but replace anything in their way.");
            return;
        } else if (secondParameter.equalsIgnoreCase("full")) {
            this.pasteOption = 0;
            this.pasteParam = 1;
        } else if (secondParameter.equalsIgnoreCase("fill")) {
            this.pasteOption = 1;
            this.pasteParam = 1;
        } else if (secondParameter.equalsIgnoreCase("replace")) {
            this.pasteOption = 2;
            this.pasteParam = 1;
        }
        try {
            this.filename = parameters[1 + this.pasteParam];
            File file = new File(VoxelSniperPlugin.getPlugin().getDataFolder() + "/stencilLists/" + this.filename + ".txt");
            if (file.exists()) {
                messenger.sendMessage(ChatColor.RED + "Stencil List '" + this.filename + "' exists and was loaded.");
                readStencilList();
            } else {
                messenger.sendMessage(ChatColor.AQUA + "Stencil List '" + this.filename + "' does not exist.  This brush will not function without a valid stencil list.");
                this.filename = "NoFileLoaded";
            }
        } catch (RuntimeException exception) {
            messenger.sendMessage(ChatColor.RED + "You need to type a stencil name.");
        }
    }

    @Override
    public void handleArrowAction(Snipe snipe) {
        stencilPaste(snipe);
    }

    @Override
    public void handleGunpowderAction(Snipe snipe) {
        stencilPasteRotation(snipe);
    }

    private String readRandomStencil() {
        double rand = Math.random() * (this.stencilList.size());
        int choice = (int) rand;
        return this.stencilList.get(choice);
    }

    private void readStencilList() {
        stencilList.clear();
        File file = new File(VoxelSniperPlugin.getPlugin().getDataFolder() + "/stencilLists/" + this.filename + ".txt");
        if (file.exists()) {
            try {
                Scanner scanner = new Scanner(file);
                int counter = 0;
                while (scanner.hasNext()) {
                    this.stencilList.put(counter, scanner.nextLine());
                    counter++;
                }
                scanner.close();
            } catch (FileNotFoundException exception) {
                exception.printStackTrace();
            }
        }
    }

    private void stencilPaste(Snipe snipe) {
        SnipeMessenger messenger = snipe.createMessenger();
        if (this.filename.matches("NoFileLoaded")) {
            messenger.sendMessage(ChatColor.RED + "You did not specify a filename for the list.  This is required.");
            return;
        }
        String stencilName = this.readRandomStencil();
        messenger.sendMessage(stencilName);
        File file = new File(VoxelSniperPlugin.getPlugin().getDataFolder() + "/stencils/" + stencilName + ".vstencil");
        if (file.exists()) {
            try {
                DataInputStream in = new DataInputStream(new BufferedInputStream(new FileInputStream(file)));
                this.x = in.readShort();
                this.z = in.readShort();
                this.y = in.readShort();
                this.xRef = in.readShort();
                this.zRef = in.readShort();
                this.yRef = in.readShort();
                int numRuns = in.readInt();
                // Something here that checks ranks using sanker'world thingie he added to Sniper and boots you out with error message if too big.
                int volume = this.x * this.y * this.z;
                messenger.sendMessage(ChatColor.AQUA + this.filename + " pasted.  Volume is " + volume + " blocks.");
                int currX = -this.xRef; // so if your ref point is +5 x, you want to start pasting -5 blocks from the clicked point (the reference) to get the
                // corner, for example.
                int currZ = -this.zRef;
                int currY = -this.yRef;
                BlockState blockData;
                BlockVector3 targetBlock = getTargetBlock();
                if (this.pasteOption == 0) {
                    for (int i = 1; i < numRuns + 1; i++) {
                        if (in.readBoolean()) {
                            int numLoops = in.readByte() + 128;
                            blockData = readBlockData(in);
                            for (int j = 0; j < numLoops; j++) {
                                setBlockData(
                                        targetBlock.getX() + currX,
                                        clampY(targetBlock.getY() + currY),
                                        targetBlock.getZ() + currZ,
                                        blockData
                                );
                                currX++;
                                if (currX == this.x - this.xRef) {
                                    currX = -this.xRef;
                                    currZ++;
                                    if (currZ == this.z - this.zRef) {
                                        currZ = -this.zRef;
                                        currY++;
                                    }
                                }
                            }
                        } else {
                            setBlockData(
                                    targetBlock.getX() + currX,
                                    clampY(targetBlock.getY() + currY),
                                    targetBlock.getZ() + currZ,
                                    readBlockData(in)
                            );
                            currX++;
                            if (currX == this.x - this.xRef) {
                                currX = -this.xRef;
                                currZ++;
                                if (currZ == this.z - this.zRef) {
                                    currZ = -this.zRef;
                                    currY++;
                                }
                            }
                        }
                    }
                } else if (this.pasteOption == 1) {
                    for (int i = 1; i < numRuns + 1; i++) {
                        if (in.readBoolean()) {
                            int numLoops = in.readByte() + 128;
                            blockData = readBlockData(in);
                            for (int j = 0; j < numLoops; j++) {
                                if (!Materials.isEmpty(blockData.getBlockType()) && clampY(
                                        targetBlock.getX() + currX,
                                        targetBlock.getY() + currY,
                                        targetBlock.getZ() + currZ
                                ).isAir()) {
                                    setBlockData(
                                            targetBlock.getX() + currX,
                                            clampY(targetBlock.getY() + currY),
                                            targetBlock.getZ() + currZ,
                                            blockData
                                    );
                                }
                                currX++;
                                if (currX == this.x - this.xRef) {
                                    currX = -this.xRef;
                                    currZ++;
                                    if (currZ == this.z - this.zRef) {
                                        currZ = -this.zRef;
                                        currY++;
                                    }
                                }
                            }
                        } else {
                            blockData = readBlockData(in);
                            if (!Materials.isEmpty(blockData.getBlockType()) && this.clampY(
                                    targetBlock.getX() + currX,
                                    targetBlock.getY() + currY,
                                    targetBlock.getZ() + currZ
                            ).isAir()) {
                                setBlockData(
                                        targetBlock.getX() + currX,
                                        clampY(targetBlock.getY() + currY),
                                        targetBlock.getZ() + currZ,
                                        blockData
                                );
                            }
                            currX++;
                            if (currX == this.x - this.xRef) {
                                currX = -this.xRef;
                                currZ++;
                                if (currZ == this.z - this.zRef) {
                                    currZ = -this.zRef;
                                    currY++;
                                }
                            }
                        }
                    }
                } else { // replace
                    for (int i = 1; i < numRuns + 1; i++) {
                        if (in.readBoolean()) {
                            int numLoops = in.readByte() + 128;
                            blockData = readBlockData(in);
                            for (int j = 0; j < (numLoops); j++) {
                                if (!Materials.isEmpty(blockData.getBlockType())) {
                                    setBlockData(
                                            targetBlock.getX() + currX,
                                            clampY(targetBlock.getY() + currY),
                                            targetBlock.getZ() + currZ,
                                            blockData
                                    );
                                }
                                currX++;
                                if (currX == this.x - this.xRef) {
                                    currX = -this.xRef;
                                    currZ++;
                                    if (currZ == this.z - this.zRef) {
                                        currZ = -this.zRef;
                                        currY++;
                                    }
                                }
                            }
                        } else {
                            blockData = readBlockData(in);
                            if (!Materials.isEmpty(blockData.getBlockType())) {
                                setBlockData(
                                        targetBlock.getX() + currX,
                                        clampY(targetBlock.getY() + currY),
                                        targetBlock.getZ() + currZ,
                                        blockData
                                );
                            }
                            currX++;
                            if (currX == this.x) {
                                currX = 0;
                                currZ++;
                                if (currZ == this.z) {
                                    currZ = 0;
                                    currY++;
                                }
                            }
                        }
                    }
                }
                in.close();
            } catch (IOException exception) {
                messenger.sendMessage(ChatColor.RED + "Something went wrong.");
                exception.printStackTrace();
            }
        } else {
            messenger.sendMessage(ChatColor.RED + "You need to type a stencil name / your specified stencil does not exist.");
        }
    }

    private void stencilPaste180(Snipe snipe) {
        SnipeMessenger messenger = snipe.createMessenger();
        if (this.filename.matches("NoFileLoaded")) {
            messenger.sendMessage(ChatColor.RED + "You did not specify a filename for the list.  This is required.");
            return;
        }
        String stencilName = this.readRandomStencil();
        File file = new File(VoxelSniperPlugin.getPlugin().getDataFolder() + "/stencils/" + stencilName + ".vstencil");
        if (file.exists()) {
            try {
                DataInputStream in = new DataInputStream(new BufferedInputStream(new FileInputStream(file)));
                this.x = in.readShort();
                this.z = in.readShort();
                this.y = in.readShort();
                this.xRef = in.readShort();
                this.zRef = in.readShort();
                this.yRef = in.readShort();
                int numRuns = in.readInt();
                // Something here that checks ranks using sanker'world thingie he added to Sniper and boots you out with error message if too big.
                int volume = this.x * this.y * this.z;
                messenger.sendMessage(ChatColor.AQUA + this.filename + " pasted.  Volume is " + volume + " blocks.");
                int currX = this.xRef; // so if your ref point is +5 x, you want to start pasting -5 blocks from the clicked point (the reference) to get the
                // corner, for example.
                int currZ = this.zRef;
                int currY = -this.yRef;
                BlockState blockData;
                BlockVector3 targetBlock = this.getTargetBlock();
                if (this.pasteOption == 0) {
                    for (int i = 1; i < numRuns + 1; i++) {
                        if (in.readBoolean()) {
                            int numLoops = in.readByte() + 128;
                            blockData = readBlockData(in);
                            for (int j = 0; j < numLoops; j++) {
                                setBlockData(
                                        targetBlock.getX() + currX,
                                        clampY(targetBlock.getY() + currY),
                                        targetBlock.getZ() + currZ,
                                        blockData
                                );
                                currX--;
                                if (currX == -this.x + this.xRef) {
                                    currX = this.xRef;
                                    currZ--;
                                    if (currZ == -this.z + this.zRef) {
                                        currZ = this.zRef;
                                        currY++;
                                    }
                                }
                            }
                        } else {
                            setBlockData(
                                    targetBlock.getX() + currX,
                                    clampY(targetBlock.getY() + currY),
                                    targetBlock.getZ() + currZ,
                                    readBlockData(in)
                            );
                            currX--;
                            if (currX == -this.x + this.xRef) {
                                currX = this.xRef;
                                currZ--;
                                if (currZ == -this.z + this.zRef) {
                                    currZ = this.zRef;
                                    currY++;
                                }
                            }
                        }
                    }
                } else if (this.pasteOption == 1) {
                    for (int i = 1; i < numRuns + 1; i++) {
                        if (in.readBoolean()) {
                            int numLoops = in.readByte() + 128;
                            blockData = readBlockData(in);
                            for (int j = 0; j < numLoops; j++) {
                                if (!Materials.isEmpty(blockData.getBlockType()) && this.clampY(
                                        targetBlock.getX() + currX,
                                        targetBlock.getY() + currY,
                                        targetBlock.getZ() + currZ
                                ).isAir()) {
                                    setBlockData(
                                            targetBlock.getX() + currX,
                                            clampY(targetBlock.getY() + currY),
                                            targetBlock.getZ() + currZ,
                                            blockData
                                    );
                                }
                                currX--;
                                if (currX == -this.x + this.xRef) {
                                    currX = this.xRef;
                                    currZ--;
                                    if (currZ == -this.z + this.zRef) {
                                        currZ = this.zRef;
                                        currY++;
                                    }
                                }
                            }
                        } else {
                            blockData = readBlockData(in);
                            if (!Materials.isEmpty(blockData.getBlockType()) && this.clampY(
                                    targetBlock.getX() + currX,
                                    targetBlock.getY() + currY,
                                    targetBlock.getZ() + currZ
                            ).isAir()) {
                                setBlockData(
                                        targetBlock.getX() + currX,
                                        clampY(targetBlock.getY() + currY),
                                        targetBlock.getZ() + currZ,
                                        blockData
                                );
                            }
                            currX--;
                            if (currX == -this.x + this.xRef) {
                                currX = this.xRef;
                                currZ--;
                                if (currZ == -this.z + this.zRef) {
                                    currZ = this.zRef;
                                    currY++;
                                }
                            }
                        }
                    }
                } else { // replace
                    for (int i = 1; i < numRuns + 1; i++) {
                        if (in.readBoolean()) {
                            int numLoops = in.readByte() + 128;
                            blockData = readBlockData(in);
                            for (int j = 0; j < (numLoops); j++) {
                                if (!Materials.isEmpty(blockData.getBlockType())) {
                                    setBlockData(
                                            targetBlock.getX() + currX,
                                            clampY(targetBlock.getY() + currY),
                                            targetBlock.getZ() + currZ,
                                            blockData
                                    );
                                }
                                currX--;
                                if (currX == -this.x + this.xRef) {
                                    currX = this.xRef;
                                    currZ--;
                                    if (currZ == -this.z + this.zRef) {
                                        currZ = this.zRef;
                                        currY++;
                                    }
                                }
                            }
                        } else {
                            blockData = readBlockData(in);
                            if (!Materials.isEmpty(blockData.getBlockType())) {
                                setBlockData(
                                        targetBlock.getX() + currX,
                                        clampY(targetBlock.getY() + currY),
                                        targetBlock.getZ() + currZ,
                                        blockData
                                );
                            }
                            currX--;
                            if (currX == -this.x + this.xRef) {
                                currX = this.xRef;
                                currZ--;
                                if (currZ == -this.z + this.zRef) {
                                    currZ = this.zRef;
                                    currY++;
                                }
                            }
                        }
                    }
                }
                in.close();
            } catch (IOException exception) {
                messenger.sendMessage(ChatColor.RED + "Something went wrong.");
                exception.printStackTrace();
            }
        } else {
            messenger.sendMessage(ChatColor.RED + "You need to type a stencil name / your specified stencil does not exist.");
        }
    }

    private void stencilPaste270(Snipe snipe) {
        SnipeMessenger messenger = snipe.createMessenger();
        if (this.filename.matches("NoFileLoaded")) {
            messenger.sendMessage(ChatColor.RED + "You did not specify a filename for the list.  This is required.");
            return;
        }
        String stencilName = this.readRandomStencil();
        File file = new File(VoxelSniperPlugin.getPlugin().getDataFolder() + "/stencils/" + stencilName + ".vstencil");
        if (file.exists()) {
            try {
                DataInputStream in = new DataInputStream(new BufferedInputStream(new FileInputStream(file)));
                this.x = in.readShort();
                this.z = in.readShort();
                this.y = in.readShort();
                this.xRef = in.readShort();
                this.zRef = in.readShort();
                this.yRef = in.readShort();
                int numRuns = in.readInt();
                // Something here that checks ranks using sanker'world thingie he added to Sniper and boots you out with error message if too big.
                int volume = this.x * this.y * this.z;
                messenger.sendMessage(ChatColor.AQUA + this.filename + " pasted.  Volume is " + volume + " blocks.");
                int currX = this.zRef; // so if your ref point is +5 x, you want to start pasting -5 blocks from the clicked point (the reference) to get the
                // corner, for example.
                int currZ = -this.xRef;
                int currY = -this.yRef;
                BlockState blockData;
                BlockVector3 targetBlock = this.getTargetBlock();
                if (this.pasteOption == 0) {
                    for (int i = 1; i < numRuns + 1; i++) {
                        if (in.readBoolean()) {
                            int numLoops = in.readByte() + 128;
                            blockData = readBlockData(in);
                            for (int j = 0; j < numLoops; j++) {
                                setBlockData(
                                        targetBlock.getX() + currX,
                                        clampY(targetBlock.getY() + currY),
                                        targetBlock.getZ() + currZ,
                                        blockData
                                );
                                currZ++;
                                if (currZ == this.x - this.xRef) {
                                    currZ = -this.xRef;
                                    currX--;
                                    if (currX == -this.z + this.zRef) {
                                        currX = this.zRef;
                                        currY++;
                                    }
                                }
                            }
                        } else {
                            setBlockData(
                                    targetBlock.getX() + currX,
                                    clampY(targetBlock.getY() + currY),
                                    targetBlock.getZ() + currZ,
                                    readBlockData(in)
                            );
                            currZ++;
                            currZ++;
                            if (currZ == this.x - this.xRef) {
                                currZ = -this.xRef;
                                currX--;
                                if (currX == -this.z + this.zRef) {
                                    currX = this.zRef;
                                    currY++;
                                }
                            }
                        }
                    }
                } else if (this.pasteOption == 1) {
                    for (int i = 1; i < numRuns + 1; i++) {
                        if (in.readBoolean()) {
                            int numLoops = in.readByte() + 128;
                            blockData = readBlockData(in);
                            for (int j = 0; j < numLoops; j++) {
                                if (!Materials.isEmpty(blockData.getBlockType()) && this.clampY(
                                        targetBlock.getX() + currX,
                                        targetBlock.getY() + currY,
                                        targetBlock.getZ() + currZ
                                ).isAir()) { // no reason to paste air over
                                    // air, and it prevents us
                                    // most of the time from
                                    // having to even check the
                                    // block.
                                    setBlockData(
                                            targetBlock.getX() + currX,
                                            clampY(targetBlock.getY() + currY),
                                            targetBlock.getZ() + currZ,
                                            blockData
                                    );
                                }
                                currZ++;
                                if (currZ == this.x - this.xRef) {
                                    currZ = -this.xRef;
                                    currX--;
                                    if (currX == -this.z + this.zRef) {
                                        currX = this.zRef;
                                        currY++;
                                    }
                                }
                            }
                        } else {
                            blockData = readBlockData(in);
                            if (!Materials.isEmpty(blockData.getBlockType()) && this.clampY(
                                    targetBlock.getX() + currX,
                                    targetBlock.getY() + currY,
                                    targetBlock.getZ() + currZ
                            ).isAir()) { // no reason to paste air over
                                // air, and it prevents us most of
                                // the time from having to even
                                // check the block.
                                setBlockData(
                                        targetBlock.getX() + currX,
                                        clampY(targetBlock.getY() + currY),
                                        targetBlock.getZ() + currZ,
                                        blockData
                                );
                            }
                            currZ++;
                            if (currZ == this.x - this.xRef) {
                                currZ = -this.xRef;
                                currX--;
                                if (currX == -this.z + this.zRef) {
                                    currX = this.zRef;
                                    currY++;
                                }
                            }
                        }
                    }
                } else { // replace
                    for (int i = 1; i < numRuns + 1; i++) {
                        if (in.readBoolean()) {
                            int numLoops = in.readByte() + 128;
                            blockData = readBlockData(in);
                            for (int j = 0; j < (numLoops); j++) {
                                if (!Materials.isEmpty(blockData.getBlockType())) {
                                    setBlockData(
                                            targetBlock.getX() + currX,
                                            clampY(targetBlock.getY() + currY),
                                            targetBlock.getZ() + currZ,
                                            blockData
                                    );
                                }
                                currZ++;
                                if (currZ == this.x - this.xRef) {
                                    currZ = -this.xRef;
                                    currX--;
                                    if (currX == -this.z + this.zRef) {
                                        currX = this.zRef;
                                        currY++;
                                    }
                                }
                            }
                        } else {
                            blockData = readBlockData(in);
                            if (!Materials.isEmpty(blockData.getBlockType())) {
                                setBlockData(
                                        targetBlock.getX() + currX,
                                        clampY(targetBlock.getY() + currY),
                                        targetBlock.getZ() + currZ,
                                        blockData
                                );
                            }
                            currZ++;
                            if (currZ == this.x - this.xRef) {
                                currZ = -this.xRef;
                                currX--;
                                if (currX == -this.z + this.zRef) {
                                    currX = this.zRef;
                                    currY++;
                                }
                            }
                        }
                    }
                }
                in.close();
            } catch (IOException exception) {
                messenger.sendMessage(ChatColor.RED + "Something went wrong.");
                exception.printStackTrace();
            }
        } else {
            messenger.sendMessage(ChatColor.RED + "You need to type a stencil name / your specified stencil does not exist.");
        }
    }

    private void stencilPaste90(Snipe snipe) {
        SnipeMessenger messenger = snipe.createMessenger();
        if (this.filename.matches("NoFileLoaded")) {
            messenger.sendMessage(ChatColor.RED + "You did not specify a filename for the list.  This is required.");
            return;
        }
        String stencilName = this.readRandomStencil();
        File file = new File(VoxelSniperPlugin.getPlugin().getDataFolder() + "/stencils/" + stencilName + ".vstencil");
        if (file.exists()) {
            try {
                DataInputStream in = new DataInputStream(new BufferedInputStream(new FileInputStream(file)));
                this.x = in.readShort();
                this.z = in.readShort();
                this.y = in.readShort();
                this.xRef = in.readShort();
                this.zRef = in.readShort();
                this.yRef = in.readShort();
                int numRuns = in.readInt();
                // Something here that checks ranks using sanker'world thingie he added to Sniper and boots you out with error message if too big.
                int volume = this.x * this.y * this.z;
                messenger.sendMessage(ChatColor.AQUA + this.filename + " pasted.  Volume is " + volume + " blocks.");
                int currX = -this.zRef; // so if your ref point is +5 x, you want to start pasting -5 blocks from the clicked point (the reference) to get the
                // corner, for example.
                int currZ = this.xRef;
                int currY = -this.yRef;
                BlockState blockData;
                BlockVector3 targetBlock = this.getTargetBlock();
                if (this.pasteOption == 0) {
                    for (int i = 1; i < numRuns + 1; i++) {
                        if (in.readBoolean()) {
                            int numLoops = in.readByte() + 128;
                            blockData = readBlockData(in);
                            for (int j = 0; j < numLoops; j++) {
                                setBlockData(
                                        targetBlock.getX() + currX,
                                        clampY(targetBlock.getY() + currY),
                                        targetBlock.getZ() + currZ,
                                        blockData
                                );
                                currZ--;
                                if (currZ == -this.x + this.xRef) {
                                    currZ = this.xRef;
                                    currX++;
                                    if (currX == this.z - this.zRef) {
                                        currX = -this.zRef;
                                        currY++;
                                    }
                                }
                            }
                        } else {
                            setBlockData(
                                    targetBlock.getX() + currX,
                                    clampY(targetBlock.getY() + currY),
                                    targetBlock.getZ() + currZ,
                                    readBlockData(in)
                            );
                            currZ--;
                            if (currZ == -this.x + this.xRef) {
                                currZ = this.xRef;
                                currX++;
                                if (currX == this.z - this.zRef) {
                                    currX = -this.zRef;
                                    currY++;
                                }
                            }
                        }
                    }
                } else if (this.pasteOption == 1) {
                    for (int i = 1; i < numRuns + 1; i++) {
                        if (in.readBoolean()) {
                            int numLoops = in.readByte() + 128;
                            blockData = readBlockData(in);
                            for (int j = 0; j < numLoops; j++) {
                                if (!Materials.isEmpty(blockData.getBlockType()) && this.clampY(
                                        targetBlock.getX() + currX,
                                        targetBlock.getY() + currY,
                                        targetBlock.getZ() + currZ
                                ).isAir()) {
                                    setBlockData(
                                            targetBlock.getX() + currX,
                                            clampY(targetBlock.getY() + currY),
                                            targetBlock.getZ() + currZ,
                                            blockData
                                    );
                                }
                                currZ--;
                                if (currZ == -this.x + this.xRef) {
                                    currZ = this.xRef;
                                    currX++;
                                    if (currX == this.z - this.zRef) {
                                        currX = -this.zRef;
                                        currY++;
                                    }
                                }
                            }
                        } else {
                            blockData = readBlockData(in);
                            if (!Materials.isEmpty(blockData.getBlockType()) && this.clampY(
                                    targetBlock.getX() + currX,
                                    targetBlock.getY() + currY,
                                    targetBlock.getZ() + currZ
                            ).isAir()) {
                                setBlockData(
                                        targetBlock.getX() + currX,
                                        clampY(targetBlock.getY() + currY),
                                        targetBlock.getZ() + currZ,
                                        blockData
                                );
                            }
                            currZ--;
                            if (currZ == -this.x + this.xRef) {
                                currZ = this.xRef;
                                currX++;
                                if (currX == this.z - this.zRef) {
                                    currX = -this.zRef;
                                    currY++;
                                }
                            }
                        }
                    }
                } else { // replace
                    for (int i = 1; i < numRuns + 1; i++) {
                        if (in.readBoolean()) {
                            int numLoops = in.readByte() + 128;
                            blockData = readBlockData(in);
                            for (int j = 0; j < (numLoops); j++) {
                                if (!Materials.isEmpty(blockData.getBlockType())) {
                                    setBlockData(
                                            targetBlock.getX() + currX,
                                            clampY(targetBlock.getY() + currY),
                                            targetBlock.getZ() + currZ,
                                            blockData
                                    );
                                }
                                currZ--;
                                if (currZ == -this.x + this.xRef) {
                                    currZ = this.xRef;
                                    currX++;
                                    if (currX == this.z - this.zRef) {
                                        currX = -this.zRef;
                                        currY++;
                                    }
                                }
                            }
                        } else {
                            blockData = readBlockData(in);
                            if (!Materials.isEmpty(blockData.getBlockType())) {
                                setBlockData(
                                        targetBlock.getX() + currX,
                                        clampY(targetBlock.getY() + currY),
                                        targetBlock.getZ() + currZ,
                                        blockData
                                );
                            }
                            currZ--;
                            if (currZ == -this.x + this.xRef) {
                                currZ = this.xRef;
                                currX++;
                                if (currX == this.z - this.zRef) {
                                    currX = -this.zRef;
                                    currY++;
                                }
                            }
                        }
                    }
                }
                in.close();
            } catch (IOException exception) {
                messenger.sendMessage(ChatColor.RED + "Something went wrong.");
                exception.printStackTrace();
            }
        } else {
            messenger.sendMessage(ChatColor.RED + "You need to type a stencil name / your specified stencil does not exist.");
        }
    }

    private BlockState readBlockData(DataInputStream in) throws IOException {
        String blockDataString = in.readUTF();
        return BukkitAdapter.adapt(Bukkit.createBlockData(blockDataString));
    }

    private void stencilPasteRotation(Snipe snipe) {
        // just randomly chooses a rotation and then calls stencilPaste.
        this.readStencilList();
        double random = Math.random();
        if (random < 0.26) {
            this.stencilPaste(snipe);
        } else if (random < 0.51) {
            this.stencilPaste90(snipe);
        } else if (random < 0.76) {
            this.stencilPaste180(snipe);
        } else {
            this.stencilPaste270(snipe);
        }
    }

    @Override
    public void sendInfo(Snipe snipe) {
        SnipeMessenger messenger = snipe.createMessenger();
        messenger.sendBrushNameMessage();
        messenger.sendMessage("File loaded: " + this.filename);
    }

}

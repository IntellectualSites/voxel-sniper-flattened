package com.thevoxelbox.voxelsniper.brush.type;

import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.world.block.BlockState;
import com.sk89q.worldedit.world.block.BlockType;
import com.thevoxelbox.voxelsniper.sniper.snipe.Snipe;
import com.thevoxelbox.voxelsniper.sniper.snipe.message.SnipeMessenger;
import com.thevoxelbox.voxelsniper.util.material.Materials;
import org.bukkit.ChatColor;

import java.util.stream.Stream;

public class CopyPastaBrush extends AbstractBrush {

    private static final int BLOCK_LIMIT = 10000;
    private final int[] pastePoint = new int[3];
    private final int[] minPoint = new int[3];
    private final int[] offsetPoint = new int[3];
    private final int[] arraySize = new int[3];
    private boolean pasteAir = true; // False = no air, true = air
    private int points; //
    private int numBlocks;
    private int[] firstPoint = new int[3];
    private int[] secondPoint = new int[3];
    private BlockType[] blockArray;
    private BlockState[] dataArray;
    private int pivot; // ccw degrees

    @Override
    public void handleCommand(String[] parameters, Snipe snipe) {
        SnipeMessenger messenger = snipe.createMessenger();
        String parameter = parameters[0];
        if (parameter.equalsIgnoreCase("info")) {
            snipe.createMessageSender()
                    .message(ChatColor.GOLD + "CopyPasta Parameters:")
                    .message(ChatColor.AQUA + "/b cp air -- toggle include (default) or exclude  air during paste")
                    .message(ChatColor.AQUA + "/b cp 0|90|180|270 -- toggle rotation (0 default)")
                    .send();
            return;
        }
        if (parameter.equalsIgnoreCase("air")) {
            this.pasteAir = !this.pasteAir;
            messenger.sendMessage(ChatColor.GOLD + "Paste air: " + this.pasteAir);
            return;
        }
        if (Stream.of("90", "180", "270", "0")
                .anyMatch(parameter::equalsIgnoreCase)) {
            this.pivot = Integer.parseInt(parameter);
            messenger.sendMessage(ChatColor.GOLD + "Pivot angle: " + this.pivot);
        }
    }

    @Override
    public void handleArrowAction(Snipe snipe) {
        SnipeMessenger messenger = snipe.createMessenger();
        BlockVector3 targetBlock = getTargetBlock();
        if (this.points == 0) {
            this.firstPoint[0] = targetBlock.getX();
            this.firstPoint[1] = targetBlock.getY();
            this.firstPoint[2] = targetBlock.getZ();
            messenger.sendMessage(ChatColor.GRAY + "First point");
            this.points = 1;
        } else if (this.points == 1) {
            this.secondPoint[0] = targetBlock.getX();
            this.secondPoint[1] = targetBlock.getY();
            this.secondPoint[2] = targetBlock.getZ();
            messenger.sendMessage(ChatColor.GRAY + "Second point");
            this.points = 2;
        } else {
            this.firstPoint = new int[3];
            this.secondPoint = new int[3];
            this.numBlocks = 0;
            this.blockArray = new BlockType[1];
            this.dataArray = new BlockState[1];
            this.points = 0;
            messenger.sendMessage(ChatColor.GRAY + "Points cleared.");
        }
    }

    @Override
    public void handleGunpowderAction(Snipe snipe) {
        SnipeMessenger messenger = snipe.createMessenger();
        if (this.points == 2) {
            if (this.numBlocks == 0) {
                doCopy(snipe);
            } else if (this.numBlocks > 0 && this.numBlocks < BLOCK_LIMIT) {
                BlockVector3 targetBlock = this.getTargetBlock();
                this.pastePoint[0] = targetBlock.getX();
                this.pastePoint[1] = targetBlock.getY();
                this.pastePoint[2] = targetBlock.getZ();
                doPasta(snipe);
            } else {
                messenger.sendMessage(ChatColor.RED + "Error");
            }
        } else {
            messenger.sendMessage(ChatColor.RED + "You must select exactly two points.");
        }
    }

    private void doCopy(Snipe snipe) {
        for (int i = 0; i < 3; i++) {
            this.arraySize[i] = Math.abs(this.firstPoint[i] - this.secondPoint[i]) + 1;
            this.minPoint[i] = Math.min(this.firstPoint[i], this.secondPoint[i]);
            this.offsetPoint[i] = this.minPoint[i] - this.firstPoint[i]; // will always be negative or zero
        }
        this.numBlocks = (this.arraySize[0]) * (this.arraySize[1]) * (this.arraySize[2]);
        SnipeMessenger messenger = snipe.createMessenger();
        if (this.numBlocks > 0 && this.numBlocks < BLOCK_LIMIT) {
            this.blockArray = new BlockType[this.numBlocks];
            this.dataArray = new BlockState[this.numBlocks];
            for (int i = 0; i < this.arraySize[0]; i++) {
                for (int j = 0; j < this.arraySize[1]; j++) {
                    for (int k = 0; k < this.arraySize[2]; k++) {
                        int currentPosition = i + this.arraySize[0] * j + this.arraySize[0] * this.arraySize[1] * k;
                        BlockState block = getBlock(this.minPoint[0] + i, this.minPoint[1] + j, this.minPoint[2] + k);
                        this.blockArray[currentPosition] = block.getBlockType();
                        BlockState clamp = this.clampY(this.minPoint[0] + i, this.minPoint[1] + j, this.minPoint[2] + k);
                        this.dataArray[currentPosition] = clamp;
                    }
                }
            }
            messenger.sendMessage(ChatColor.AQUA + String.valueOf(this.numBlocks) + " blocks copied.");
        } else {
            messenger.sendMessage(ChatColor.RED + "Copy area too big: " + this.numBlocks + "(Limit: " + BLOCK_LIMIT + ")");
        }
    }

    private void doPasta(Snipe snipe) {
        for (int i = 0; i < this.arraySize[0]; i++) {
            for (int j = 0; j < this.arraySize[1]; j++) {
                for (int k = 0; k < this.arraySize[2]; k++) {
                    int currentPosition = i + this.arraySize[0] * j + this.arraySize[0] * this.arraySize[1] * k;
                    int x;
                    int y;
                    int z;
                    switch (this.pivot) {
                        case 180:
                            x = this.pastePoint[0] - this.offsetPoint[0] - i;
                            y = this.pastePoint[1] + this.offsetPoint[1] + j;
                            z = this.pastePoint[2] - this.offsetPoint[2] - k;
                            break;
                        case 270:
                            x = this.pastePoint[0] + this.offsetPoint[2] + k;
                            y = this.pastePoint[1] + this.offsetPoint[1] + j;
                            z = this.pastePoint[2] - this.offsetPoint[0] - i;
                            break;
                        case 90:
                            x = this.pastePoint[0] - this.offsetPoint[2] - k;
                            y = this.pastePoint[1] + this.offsetPoint[1] + j;
                            z = this.pastePoint[2] + this.offsetPoint[0] + i;
                            break;
                        default: // assume no rotation
                            x = this.pastePoint[0] + this.offsetPoint[0] + i;
                            y = this.pastePoint[1] + this.offsetPoint[1] + j;
                            z = this.pastePoint[2] + this.offsetPoint[2] + k;
                            break;
                    }
                    BlockState block = clampY(x, y, z);
                    if (!(Materials.isEmpty(this.blockArray[currentPosition]) && !this.pasteAir)) {
                        setBlockData(x, y, z, this.dataArray[currentPosition]);
                    }
                }
            }
        }
        SnipeMessenger messenger = snipe.createMessenger();
        messenger.sendMessage(ChatColor.AQUA + String.valueOf(this.numBlocks) + " blocks pasted.");
    }

    @Override
    public void sendInfo(Snipe snipe) {
        snipe.createMessageSender()
                .brushNameMessage()
                .message(ChatColor.GOLD + "Paste air: " + this.pasteAir)
                .message(ChatColor.GOLD + "Pivot angle: " + this.pivot)
                .send();
    }

}

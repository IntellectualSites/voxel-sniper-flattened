package com.thevoxelbox.voxelsniper.util.message;

import com.sk89q.worldedit.world.block.BlockState;
import com.sk89q.worldedit.world.block.BlockStateHolder;
import com.sk89q.worldedit.world.block.BlockType;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.List;
import java.util.stream.Collectors;

public class Messenger {

    private static final int BRUSH_SIZE_WARNING_THRESHOLD = 20;

    private final CommandSender sender;

    public Messenger(CommandSender sender) {
        this.sender = sender;
    }

    public void sendBrushNameMessage(String brushName) {
        sendMessage(ChatColor.AQUA + "Brush Type: " + ChatColor.LIGHT_PURPLE + brushName);
    }

    public void sendPerformerNameMessage(String performerName) {
        sendMessage(ChatColor.DARK_PURPLE + "Performer: " + ChatColor.DARK_GREEN + performerName);
    }

    public void sendBlockTypeMessage(BlockType blockType) {
        sendMessage(ChatColor.GOLD + "Voxel: " + ChatColor.RED + blockType.getId());
    }

    public void sendBlockDataMessage(BlockState blockData) {
        sendMessage(ChatColor.BLUE + "Data Variable: " + ChatColor.DARK_RED + blockData.getAsString());
    }

    public void sendReplaceBlockTypeMessage(BlockType replaceBlockType) {
        sendMessage(ChatColor.AQUA + "Replace Material: " + ChatColor.RED + replaceBlockType.getId());
    }

    public void sendReplaceBlockDataMessage(BlockState replaceBlockData) {
        sendMessage(ChatColor.DARK_GRAY + "Replace Data Variable: " + ChatColor.DARK_RED + replaceBlockData.getAsString());
    }

    public void sendBrushSizeMessage(int brushSize) {
        sendMessage(ChatColor.GREEN + "Brush Size: " + ChatColor.DARK_RED + brushSize);
        if (brushSize >= BRUSH_SIZE_WARNING_THRESHOLD) {
            sendMessage(ChatColor.RED + "WARNING: Large brush size selected!");
        }
    }

    public void sendCylinderCenterMessage(int cylinderCenter) {
        sendMessage(ChatColor.DARK_BLUE + "Brush Center: " + ChatColor.DARK_RED + cylinderCenter);
    }

    public void sendVoxelHeightMessage(int voxelHeight) {
        sendMessage(ChatColor.DARK_AQUA + "Brush Height: " + ChatColor.DARK_RED + voxelHeight);
    }

    public void sendVoxelListMessage(List<? extends BlockState> voxelList) {
        if (voxelList.isEmpty()) {
            sendMessage(ChatColor.DARK_GREEN + "No blocks selected!");
        }
        String message = voxelList.stream()
                .map(BlockStateHolder::getAsString)
                .map(dataAsString -> dataAsString + " ")
                .collect(Collectors.joining("", ChatColor.DARK_GREEN + "Block Types Selected: " + ChatColor.AQUA, ""));
        sendMessage(message);
    }

    public void sendMessage(String message) {
        this.sender.sendMessage(message);
    }

}

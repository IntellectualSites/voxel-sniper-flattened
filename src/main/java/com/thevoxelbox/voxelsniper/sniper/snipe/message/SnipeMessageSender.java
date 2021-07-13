package com.thevoxelbox.voxelsniper.sniper.snipe.message;

import com.sk89q.worldedit.world.block.BlockState;
import com.sk89q.worldedit.world.block.BlockType;
import com.thevoxelbox.voxelsniper.brush.property.BrushProperties;
import com.thevoxelbox.voxelsniper.sniper.toolkit.ToolkitProperties;
import com.thevoxelbox.voxelsniper.util.message.MessageSender;
import org.bukkit.entity.Player;

import java.util.List;

public class SnipeMessageSender {

    private final ToolkitProperties toolkitProperties;
    private final BrushProperties brushProperties;
    private final MessageSender messageSender;

    public SnipeMessageSender(ToolkitProperties toolkitProperties, BrushProperties brushProperties, Player player) {
        this.toolkitProperties = toolkitProperties;
        this.brushProperties = brushProperties;
        this.messageSender = new MessageSender(player);
    }

    public SnipeMessageSender brushNameMessage() {
        String brushName = this.brushProperties.getName();
        this.messageSender.brushNameMessage(brushName);
        return this;
    }

    public SnipeMessageSender blockTypeMessage() {
        BlockType blockType = this.toolkitProperties.getBlockType();
        this.messageSender.blockTypeMessage(blockType);
        return this;
    }

    public SnipeMessageSender blockDataMessage() {
        BlockState blockData = this.toolkitProperties.getBlockData();
        this.messageSender.blockDataMessage(blockData);
        return this;
    }

    public SnipeMessageSender replaceBlockTypeMessage() {
        BlockType replaceBlockType = this.toolkitProperties.getReplaceBlockType();
        this.messageSender.replaceBlockTypeMessage(replaceBlockType);
        return this;
    }

    public SnipeMessageSender replaceBlockDataMessage() {
        BlockState replaceBlockData = this.toolkitProperties.getReplaceBlockData();
        this.messageSender.replaceBlockDataMessage(replaceBlockData);
        return this;
    }

    public SnipeMessageSender brushSizeMessage() {
        int brushSize = this.toolkitProperties.getBrushSize();
        this.messageSender.brushSizeMessage(brushSize);
        return this;
    }

    public SnipeMessageSender cylinderCenterMessage() {
        int cylinderCenter = this.toolkitProperties.getCylinderCenter();
        this.messageSender.cylinderCenterMessage(cylinderCenter);
        return this;
    }

    public SnipeMessageSender voxelHeightMessage() {
        int voxelHeight = this.toolkitProperties.getVoxelHeight();
        this.messageSender.voxelHeightMessage(voxelHeight);
        return this;
    }

    public SnipeMessageSender voxelListMessage() {
        List<BlockState> voxelList = this.toolkitProperties.getVoxelList();
        this.messageSender.voxelListMessage(voxelList);
        return this;
    }

    public SnipeMessageSender message(String message) {
        this.messageSender.message(message);
        return this;
    }

    public void send() {
        this.messageSender.send();
    }

    public MessageSender getMessageSender() {
        return this.messageSender;
    }

}

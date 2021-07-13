package com.thevoxelbox.voxelsniper.sniper.snipe.performer.message;

import com.thevoxelbox.voxelsniper.brush.property.BrushProperties;
import com.thevoxelbox.voxelsniper.performer.property.PerformerProperties;
import com.thevoxelbox.voxelsniper.sniper.snipe.message.SnipeMessageSender;
import com.thevoxelbox.voxelsniper.sniper.toolkit.ToolkitProperties;
import com.thevoxelbox.voxelsniper.util.message.MessageSender;
import org.bukkit.entity.Player;

public class PerformerSnipeMessageSender extends SnipeMessageSender {

    private final PerformerProperties performerProperties;

    public PerformerSnipeMessageSender(
            ToolkitProperties toolkitProperties,
            BrushProperties brushProperties,
            PerformerProperties performerProperties,
            Player player
    ) {
        super(toolkitProperties, brushProperties, player);
        this.performerProperties = performerProperties;
    }

    public PerformerSnipeMessageSender performerNameMessage() {
        MessageSender messageSender = getMessageSender();
        String performerName = this.performerProperties.getName();
        messageSender.performerNameMessage(performerName);
        return this;
    }

}

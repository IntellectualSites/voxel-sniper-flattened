package com.thevoxelbox.voxelsniper.sniper.snipe.performer.message;

import com.thevoxelbox.voxelsniper.brush.property.BrushProperties;
import com.thevoxelbox.voxelsniper.performer.property.PerformerProperties;
import com.thevoxelbox.voxelsniper.sniper.snipe.message.SnipeMessenger;
import com.thevoxelbox.voxelsniper.sniper.toolkit.ToolkitProperties;
import com.thevoxelbox.voxelsniper.util.message.Messenger;
import org.bukkit.entity.Player;

public class PerformerSnipeMessenger extends SnipeMessenger {

    private final PerformerProperties performerProperties;

    public PerformerSnipeMessenger(
            ToolkitProperties toolkitProperties,
            BrushProperties brushProperties,
            PerformerProperties performerProperties,
            Player player
    ) {
        super(toolkitProperties, brushProperties, player);
        this.performerProperties = performerProperties;
    }

    public void sendPerformerNameMessage() {
        Messenger messenger = getMessenger();
        String performerName = this.performerProperties.getName();
        messenger.sendPerformerNameMessage(performerName);
    }

}

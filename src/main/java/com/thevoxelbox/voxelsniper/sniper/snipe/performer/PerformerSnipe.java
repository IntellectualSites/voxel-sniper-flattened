package com.thevoxelbox.voxelsniper.sniper.snipe.performer;

import com.thevoxelbox.voxelsniper.brush.Brush;
import com.thevoxelbox.voxelsniper.brush.property.BrushProperties;
import com.thevoxelbox.voxelsniper.performer.Performer;
import com.thevoxelbox.voxelsniper.performer.property.PerformerProperties;
import com.thevoxelbox.voxelsniper.sniper.Sniper;
import com.thevoxelbox.voxelsniper.sniper.snipe.Snipe;
import com.thevoxelbox.voxelsniper.sniper.snipe.performer.message.PerformerSnipeMessageSender;
import com.thevoxelbox.voxelsniper.sniper.snipe.performer.message.PerformerSnipeMessenger;
import com.thevoxelbox.voxelsniper.sniper.toolkit.Toolkit;
import com.thevoxelbox.voxelsniper.sniper.toolkit.ToolkitProperties;
import org.bukkit.entity.Player;

public class PerformerSnipe extends Snipe {

    private final PerformerProperties performerProperties;
    private final Performer performer;

    public PerformerSnipe(Snipe snipe, PerformerProperties performerProperties, Performer performer) {
        this(
                snipe.getSniper(),
                snipe.getToolkit(),
                snipe.getToolkitProperties(),
                snipe.getBrushProperties(),
                snipe.getBrush(),
                performerProperties,
                performer
        );
    }

    public PerformerSnipe(
            Sniper sniper,
            Toolkit toolkit,
            ToolkitProperties toolkitProperties,
            BrushProperties brushProperties,
            Brush brush,
            PerformerProperties performerProperties,
            Performer performer
    ) {
        super(sniper, toolkit, toolkitProperties, brushProperties, brush);
        this.performerProperties = performerProperties;
        this.performer = performer;
    }

    @Override
    public PerformerSnipeMessenger createMessenger() {
        ToolkitProperties toolkitProperties = getToolkitProperties();
        BrushProperties brushProperties = getBrushProperties();
        Sniper sniper = getSniper();
        Player player = sniper.getPlayer();
        return new PerformerSnipeMessenger(toolkitProperties, brushProperties, this.performerProperties, player);
    }

    @Override
    public PerformerSnipeMessageSender createMessageSender() {
        ToolkitProperties toolkitProperties = getToolkitProperties();
        BrushProperties brushProperties = getBrushProperties();
        Sniper sniper = getSniper();
        Player player = sniper.getPlayer();
        return new PerformerSnipeMessageSender(toolkitProperties, brushProperties, this.performerProperties, player);
    }

    public PerformerProperties getPerformerProperties() {
        return this.performerProperties;
    }

    public Performer getPerformer() {
        return this.performer;
    }

}

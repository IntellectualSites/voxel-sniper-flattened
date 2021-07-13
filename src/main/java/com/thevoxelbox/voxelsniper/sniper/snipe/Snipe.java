package com.thevoxelbox.voxelsniper.sniper.snipe;

import com.thevoxelbox.voxelsniper.brush.Brush;
import com.thevoxelbox.voxelsniper.brush.property.BrushProperties;
import com.thevoxelbox.voxelsniper.sniper.Sniper;
import com.thevoxelbox.voxelsniper.sniper.snipe.message.SnipeMessageSender;
import com.thevoxelbox.voxelsniper.sniper.snipe.message.SnipeMessenger;
import com.thevoxelbox.voxelsniper.sniper.toolkit.Toolkit;
import com.thevoxelbox.voxelsniper.sniper.toolkit.ToolkitProperties;
import org.bukkit.entity.Player;

public class Snipe {

    private final Sniper sniper;
    private final Toolkit toolkit;
    private final ToolkitProperties toolkitProperties;
    private final BrushProperties brushProperties;
    private final Brush brush;


    public Snipe(
            Sniper sniper,
            Toolkit toolkit,
            ToolkitProperties toolkitProperties,
            BrushProperties brushProperties,
            Brush brush
    ) {
        this.sniper = sniper;
        this.toolkit = toolkit;
        this.toolkitProperties = toolkitProperties;
        this.brushProperties = brushProperties;
        this.brush = brush;
    }

    public SnipeMessenger createMessenger() {
        Player player = this.sniper.getPlayer();
        return new SnipeMessenger(this.toolkitProperties, this.brushProperties, player);
    }

    public SnipeMessageSender createMessageSender() {
        Player player = this.sniper.getPlayer();
        return new SnipeMessageSender(this.toolkitProperties, this.brushProperties, player);
    }

    public Sniper getSniper() {
        return this.sniper;
    }

    public Toolkit getToolkit() {
        return this.toolkit;
    }

    public ToolkitProperties getToolkitProperties() {
        return this.toolkitProperties;
    }

    public BrushProperties getBrushProperties() {
        return this.brushProperties;
    }

    public Brush getBrush() {
        return this.brush;
    }

}

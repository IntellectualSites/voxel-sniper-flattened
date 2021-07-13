package com.thevoxelbox.voxelsniper.command.executor;

import com.thevoxelbox.voxelsniper.VoxelSniperPlugin;
import com.thevoxelbox.voxelsniper.brush.Brush;
import com.thevoxelbox.voxelsniper.brush.PerformerBrush;
import com.thevoxelbox.voxelsniper.brush.property.BrushProperties;
import com.thevoxelbox.voxelsniper.command.CommandExecutor;
import com.thevoxelbox.voxelsniper.performer.PerformerRegistry;
import com.thevoxelbox.voxelsniper.sniper.Sniper;
import com.thevoxelbox.voxelsniper.sniper.SniperRegistry;
import com.thevoxelbox.voxelsniper.sniper.snipe.Snipe;
import com.thevoxelbox.voxelsniper.sniper.toolkit.Toolkit;
import com.thevoxelbox.voxelsniper.sniper.toolkit.ToolkitProperties;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PerformerExecutor implements CommandExecutor {

    private final VoxelSniperPlugin plugin;

    public PerformerExecutor(VoxelSniperPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void executeCommand(CommandSender sender, String[] arguments) {
        SniperRegistry sniperRegistry = this.plugin.getSniperRegistry();
        Player player = (Player) sender;
        Sniper sniper = sniperRegistry.getSniper(player);
        if (sniper == null) {
            return;
        }
        Toolkit toolkit = sniper.getCurrentToolkit();
        if (toolkit == null) {
            return;
        }
        ToolkitProperties toolkitProperties = toolkit.getProperties();
        if (toolkitProperties == null) {
            return;
        }
        BrushProperties brushProperties = toolkit.getCurrentBrushProperties();
        Brush brush = toolkit.getCurrentBrush();
        if (!(brush instanceof PerformerBrush)) {
            sender.sendMessage("This brush is not a performer brush.");
            return;
        }
        PerformerBrush performer = (PerformerBrush) brush;
        String[] parameters = arguments.length == 0 ? new String[]{"m"} : arguments;
        Snipe snipe = new Snipe(sniper, toolkit, toolkitProperties, brushProperties, brush);
        PerformerRegistry performerRegistry = this.plugin.getPerformerRegistry();
        performer.handlePerformerCommand(parameters, snipe, performerRegistry);
    }

}

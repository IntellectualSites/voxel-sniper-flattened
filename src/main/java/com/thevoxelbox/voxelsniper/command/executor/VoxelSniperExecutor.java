package com.thevoxelbox.voxelsniper.command.executor;

import com.thevoxelbox.voxelsniper.VoxelSniperPlugin;
import com.thevoxelbox.voxelsniper.brush.BrushRegistry;
import com.thevoxelbox.voxelsniper.brush.property.BrushProperties;
import com.thevoxelbox.voxelsniper.command.CommandExecutor;
import com.thevoxelbox.voxelsniper.performer.PerformerRegistry;
import com.thevoxelbox.voxelsniper.performer.property.PerformerProperties;
import com.thevoxelbox.voxelsniper.sniper.Sniper;
import com.thevoxelbox.voxelsniper.sniper.SniperRegistry;
import com.thevoxelbox.voxelsniper.sniper.toolkit.Toolkit;
import com.thevoxelbox.voxelsniper.sniper.toolkit.ToolkitProperties;
import com.thevoxelbox.voxelsniper.util.text.NumericParser;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class VoxelSniperExecutor implements CommandExecutor {

    private final VoxelSniperPlugin plugin;

    public VoxelSniperExecutor(VoxelSniperPlugin plugin) {
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
        if (arguments.length >= 1) {
            String firstArgument = arguments[0];
            if (firstArgument.equalsIgnoreCase("brushes")) {
                BrushRegistry brushRegistry = this.plugin.getBrushRegistry();
                Map<String, BrushProperties> brushes = brushRegistry.getBrushesProperties();
                Set<String> aliases = brushes.keySet();
                String aliasesString = String.join(", ", aliases);
                sender.sendMessage(aliasesString);
                return;
            } else if (firstArgument.equalsIgnoreCase("range")) {
                Toolkit toolkit = sniper.getCurrentToolkit();
                if (toolkit == null) {
                    return;
                }
                ToolkitProperties toolkitProperties = toolkit.getProperties();
                if (toolkitProperties == null) {
                    return;
                }
                if (arguments.length == 2) {
                    Integer range = NumericParser.parseInteger(arguments[1]);
                    if (range == null) {
                        sender.sendMessage("Can't parse number.");
                        return;
                    }
                    if (range < 1) {
                        sender.sendMessage("Values less than 1 are not allowed.");
                    }
                    toolkitProperties.setBlockTracerRange(range);
                } else {
                    toolkitProperties.setBlockTracerRange(0);
                }
                Integer blockTracerRange = toolkitProperties.getBlockTracerRange();
                sender.sendMessage(ChatColor.GOLD + "Distance Restriction toggled " + ChatColor.DARK_RED + (blockTracerRange == null
                        ? "off"
                        : "on") + ChatColor.GOLD + ". Range is " + ChatColor.LIGHT_PURPLE + blockTracerRange);
                return;
            } else if (firstArgument.equalsIgnoreCase("perf")) {
                PerformerRegistry performerRegistry = this.plugin.getPerformerRegistry();
                Map<String, PerformerProperties> performerProperties = performerRegistry.getPerformerProperties();
                Set<String> aliases = performerProperties.keySet();
                String aliasesString = String.join(", ", aliases);
                sender.sendMessage(ChatColor.AQUA + "Available performers (abbreviated):");
                sender.sendMessage(aliasesString);
                return;
            } else if (firstArgument.equalsIgnoreCase("perflong")) {
                PerformerRegistry performerRegistry = this.plugin.getPerformerRegistry();
                String names = performerRegistry.getPerformerProperties()
                        .values()
                        .stream()
                        .map(PerformerProperties::getName)
                        .collect(Collectors.joining(", "));
                sender.sendMessage(ChatColor.AQUA + "Available performers:");
                sender.sendMessage(names);
                return;
            } else if (firstArgument.equalsIgnoreCase("enable")) {
                sniper.setEnabled(true);
                sender.sendMessage("FastAsyncVoxelSniper is " + (sniper.isEnabled() ? "enabled" : "disabled"));
                return;
            } else if (firstArgument.equalsIgnoreCase("disable")) {
                sniper.setEnabled(false);
                sender.sendMessage("FastAsyncVoxelSniper is " + (sniper.isEnabled() ? "enabled" : "disabled"));
                return;
            } else if (firstArgument.equalsIgnoreCase("toggle")) {
                sniper.setEnabled(!sniper.isEnabled());
                sender.sendMessage("FastAsyncVoxelSniper is " + (sniper.isEnabled() ? "enabled" : "disabled"));
                return;
            }
        }
        sender.sendMessage(ChatColor.DARK_RED + "FastAsyncVoxelSniper - Current Brush Settings:");
        sniper.sendInfo(sender);
    }

}

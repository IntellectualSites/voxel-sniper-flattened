package com.thevoxelbox.voxelsniper;

import org.bukkit.command.CommandExecutor;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.Nullable;

public class FastAsyncVoxelSniper {

    private final VoxelSniperPlugin plugin;

    public FastAsyncVoxelSniper(JavaPlugin plugin) {
        this.plugin = (VoxelSniperPlugin) plugin;
        try {
            this.initFavs();
        } catch (Throwable ignore) {
        }
    }

    public void initFavs() {

        setupCommand("/p", (sender, command, label, args) -> {
            if (sender instanceof Player && sender.hasPermission("voxelsniper.sniper")) {
                @Nullable PluginCommand cmd = plugin.getCommand("p");
                plugin.onCommand(sender, cmd, label, args);
            }
            return false;
        });
        setupCommand("/d", (sender, command, label, args) -> {
            if (sender instanceof Player && sender.hasPermission("voxelsniper.sniper")) {
                @Nullable PluginCommand cmd = plugin.getCommand("d");
                plugin.onCommand(sender, cmd, label, args);
            }
            return false;
        });
    }

    public void setupCommand(final String label, final CommandExecutor cmd) {
        plugin.getCommand(label).setExecutor(cmd);
    }

}

package com.thevoxelbox.voxelsniper.command.executor;

import com.thevoxelbox.voxelsniper.VoxelSniperPlugin;
import com.thevoxelbox.voxelsniper.command.CommandExecutor;
import com.thevoxelbox.voxelsniper.sniper.Sniper;
import com.thevoxelbox.voxelsniper.sniper.SniperRegistry;
import com.thevoxelbox.voxelsniper.sniper.toolkit.Toolkit;
import com.thevoxelbox.voxelsniper.sniper.toolkit.ToolkitProperties;
import com.thevoxelbox.voxelsniper.util.message.Messenger;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class VoxelHeightExecutor implements CommandExecutor {

    private final VoxelSniperPlugin plugin;

    public VoxelHeightExecutor(VoxelSniperPlugin plugin) {
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
        int height;
        try {
            height = Integer.parseInt(arguments[0]);
        } catch (NumberFormatException | ArrayIndexOutOfBoundsException ignored) {
            sender.sendMessage(ChatColor.RED + "Invalid input. Must be a number.");
            return;
        }
        toolkitProperties.setVoxelHeight(height);
        Messenger messenger = new Messenger(sender);
        messenger.sendVoxelHeightMessage(height);
    }

}

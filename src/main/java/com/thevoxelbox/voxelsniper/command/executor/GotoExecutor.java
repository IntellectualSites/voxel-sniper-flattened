package com.thevoxelbox.voxelsniper.command.executor;

import com.thevoxelbox.voxelsniper.command.CommandExecutor;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class GotoExecutor implements CommandExecutor {

    @Override
    public void executeCommand(CommandSender sender, String[] arguments) {
        Player player = (Player) sender;
        World world = player.getWorld();
        int x;
        int z;
        try {
            x = Integer.parseInt(arguments[0]);
            z = Integer.parseInt(arguments[1]);
        } catch (NumberFormatException | ArrayIndexOutOfBoundsException ignored) {
            sender.sendMessage(ChatColor.RED + "Invalid syntax. Must be a coordinate");
            return;
        }
        player.teleport(new Location(world, x, world.getHighestBlockYAt(x, z), z));
        sender.sendMessage(ChatColor.GREEN + "Woosh!");
    }

}

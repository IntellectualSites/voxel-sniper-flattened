package com.thevoxelbox.voxelsniper.command.executor;

import com.thevoxelbox.voxelsniper.command.CommandExecutor;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class VoxelChunkExecutor implements CommandExecutor {

    @SuppressWarnings("deprecation")
    @Override
    public void executeCommand(CommandSender sender, String[] arguments) {
        Player player = (Player) sender;
        World world = player.getWorld();
        Location location = player.getLocation();
        int x = location.getBlockX();
        int z = location.getBlockZ();
        world.refreshChunk(x, z);
        sender.sendMessage(ChatColor.BLUE + "Chunk has been refreshed.");
    }

}

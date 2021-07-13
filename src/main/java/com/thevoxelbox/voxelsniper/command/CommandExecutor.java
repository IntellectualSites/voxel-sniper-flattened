package com.thevoxelbox.voxelsniper.command;

import org.bukkit.command.CommandSender;

public interface CommandExecutor {

    void executeCommand(CommandSender sender, String[] arguments);

}

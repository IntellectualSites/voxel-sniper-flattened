package com.thevoxelbox.voxelsniper.command;

import org.bukkit.command.CommandSender;

import java.util.List;

public interface TabCompleter {

    List<String> complete(CommandSender sender, String[] arguments);

}

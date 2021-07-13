package com.thevoxelbox.voxelsniper.command.executor;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.thevoxelbox.voxelsniper.VoxelSniperPlugin;
import com.thevoxelbox.voxelsniper.command.CommandExecutor;
import com.thevoxelbox.voxelsniper.sniper.Sniper;
import com.thevoxelbox.voxelsniper.sniper.SniperRegistry;
import com.thevoxelbox.voxelsniper.sniper.toolkit.BlockTracer;
import com.thevoxelbox.voxelsniper.sniper.toolkit.Toolkit;
import com.thevoxelbox.voxelsniper.sniper.toolkit.ToolkitProperties;
import com.thevoxelbox.voxelsniper.util.message.Messenger;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class VoxelInkReplaceExecutor implements CommandExecutor {

    private final VoxelSniperPlugin plugin;

    public VoxelInkReplaceExecutor(VoxelSniperPlugin plugin) {
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
        BlockData blockData;
        if (arguments.length == 0) {
            BlockTracer blockTracer = toolkitProperties.createBlockTracer(player);
            Block targetBlock = blockTracer.getTargetBlock();
            blockData = targetBlock.getBlockData();
        } else {
            try {
                blockData = Bukkit.createBlockData(arguments[0]);
            } catch (IllegalArgumentException exception) {
                sender.sendMessage("Couldn't parse input.");
                return;
            }
        }
        toolkitProperties.setReplaceBlockData(BukkitAdapter.adapt(blockData));
        Messenger messenger = new Messenger(sender);
        messenger.sendReplaceBlockDataMessage(BukkitAdapter.adapt(blockData));
    }

}

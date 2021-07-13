package com.thevoxelbox.voxelsniper.listener;

import com.thevoxelbox.voxelsniper.VoxelSniperPlugin;
import com.thevoxelbox.voxelsniper.sniper.Sniper;
import com.thevoxelbox.voxelsniper.sniper.SniperRegistry;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public class PlayerInteractListener implements Listener<PlayerInteractEvent> {

    private final VoxelSniperPlugin plugin;

    public PlayerInteractListener(VoxelSniperPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    @Override
    public void listen(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (!player.hasPermission("voxelsniper.sniper")) {
            return;
        }
        SniperRegistry sniperRegistry = this.plugin.getSniperRegistry();
        Sniper sniper = sniperRegistry.getSniper(player);
        if (sniper == null) {
            return;
        }
        Action action = event.getAction();
        Material usedItem = event.getMaterial();
        Block clickedBlock = event.getClickedBlock();
        BlockFace clickedBlockFace = event.getBlockFace();
        if (sniper.isEnabled() && sniper.snipe(player, action, usedItem, clickedBlock, clickedBlockFace)) {
            event.setCancelled(true);
        }
    }

}

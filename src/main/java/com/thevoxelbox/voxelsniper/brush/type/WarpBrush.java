package com.thevoxelbox.voxelsniper.brush.type;

import com.fastasyncworldedit.core.Fawe;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.thevoxelbox.voxelsniper.sniper.Sniper;
import com.thevoxelbox.voxelsniper.sniper.snipe.Snipe;
import com.thevoxelbox.voxelsniper.sniper.snipe.message.SnipeMessenger;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

public class WarpBrush extends AbstractBrush {

    @Override
    public void handleArrowAction(Snipe snipe) {
        Sniper sniper = snipe.getSniper();
        Player player = sniper.getPlayer();
        BlockVector3 lastBlock = this.getLastBlock();
        if (lastBlock == null) {
            return;
        }
        Fawe.get().getQueueHandler().sync(() -> {//FAWE Add
            World world = BukkitAdapter.adapt(getEditSession().getWorld());
            Location location = BukkitAdapter.adapt(world, lastBlock);
            Location playerLocation = player.getLocation();
            location.setPitch(playerLocation.getPitch());
            location.setYaw(playerLocation.getYaw());
            player.teleport(location);
        });
    }

    @Override
    public void handleGunpowderAction(Snipe snipe) {
        Sniper sniper = snipe.getSniper();
        Player player = sniper.getPlayer();
        BlockVector3 lastBlock = this.getLastBlock();
        if (lastBlock == null) {
            return;
        }
        Fawe.get().getQueueHandler().sync(() -> {//FAWE add
            World world = BukkitAdapter.adapt(getEditSession().getWorld());
            Location location = BukkitAdapter.adapt(world, lastBlock);
            Location playerLocation = player.getLocation();
            location.setPitch(playerLocation.getPitch());
            location.setYaw(playerLocation.getYaw());
            world.strikeLightning(location);
            player.teleport(location);
            world.strikeLightning(location);
        });
    }

    @Override
    public void sendInfo(Snipe snipe) {
        SnipeMessenger messenger = snipe.createMessenger();
        messenger.sendBrushNameMessage();
    }

}

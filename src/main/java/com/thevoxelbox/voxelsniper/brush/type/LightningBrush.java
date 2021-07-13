package com.thevoxelbox.voxelsniper.brush.type;

import com.fastasyncworldedit.core.util.TaskManager;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.thevoxelbox.voxelsniper.sniper.snipe.Snipe;
import com.thevoxelbox.voxelsniper.sniper.snipe.message.SnipeMessenger;
import org.bukkit.ChatColor;
import org.bukkit.World;

public class LightningBrush extends AbstractBrush {

    @Override
    public void handleArrowAction(Snipe snipe) {
        BlockVector3 targetBlock = getTargetBlock();
        TaskManager.IMP.sync(() -> {
            World world = BukkitAdapter.adapt(getEditSession().getWorld());
            world.strikeLightning(BukkitAdapter.adapt(world, targetBlock));
            return null;
        });
    }

    @Override
    public void handleGunpowderAction(Snipe snipe) {
        BlockVector3 targetBlock = getTargetBlock();
        TaskManager.IMP.sync(() -> {
            World world = BukkitAdapter.adapt(getEditSession().getWorld());
            world.strikeLightning(BukkitAdapter.adapt(world, targetBlock));
            return null;
        });
    }

    @Override
    public void sendInfo(Snipe snipe) {
        SnipeMessenger messenger = snipe.createMessenger();
        messenger.sendBrushNameMessage();
        messenger.sendMessage(ChatColor.LIGHT_PURPLE + "Lightning Brush! Please use in moderation.");
    }

}

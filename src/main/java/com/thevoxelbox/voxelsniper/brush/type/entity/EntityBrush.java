package com.thevoxelbox.voxelsniper.brush.type.entity;

import com.fastasyncworldedit.core.util.TaskManager;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.thevoxelbox.voxelsniper.brush.type.AbstractBrush;
import com.thevoxelbox.voxelsniper.sniper.snipe.Snipe;
import com.thevoxelbox.voxelsniper.sniper.snipe.message.SnipeMessenger;
import com.thevoxelbox.voxelsniper.sniper.toolkit.ToolkitProperties;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;

import java.util.Arrays;
import java.util.stream.Collectors;

public class EntityBrush extends AbstractBrush {

    private EntityType entityType = EntityType.ZOMBIE;

    @SuppressWarnings("deprecation")
    @Override
    public void handleCommand(String[] parameters, Snipe snipe) {
        SnipeMessenger messenger = snipe.createMessenger();
        if (parameters[0].equalsIgnoreCase("info")) {
            messenger.sendMessage(ChatColor.BLUE + "The available entity types are as follows:");
            String names = Arrays.stream(EntityType.values())
                    .map(currentEntity -> ChatColor.AQUA + " | " + ChatColor.DARK_GREEN + currentEntity.getName())
                    .collect(Collectors.joining("", "", ChatColor.AQUA + " |"));
            messenger.sendMessage(names);
        } else {
            EntityType currentEntity = EntityType.fromName(parameters[1]);
            if (currentEntity != null) {
                this.entityType = currentEntity;
                messenger.sendMessage(ChatColor.GREEN + "Entity type set to " + this.entityType.getName());
            } else {
                messenger.sendMessage(ChatColor.RED + "This is not a valid entity!");
            }
        }
    }

    @Override
    public void handleArrowAction(Snipe snipe) {
        spawn(snipe);
    }

    @Override
    public void handleGunpowderAction(Snipe snipe) {
        spawn(snipe);
    }

    private void spawn(Snipe snipe) {
        ToolkitProperties toolkitProperties = snipe.getToolkitProperties();
        SnipeMessenger messenger = snipe.createMessenger();
        EditSession editSession = getEditSession();
        for (int x = 0; x < toolkitProperties.getBrushSize(); x++) {
            try {
                BlockVector3 lastBlock = getLastBlock();
                Class<? extends Entity> entityClass = this.entityType.getEntityClass();
                if (entityClass == null) {
                    return;
                }
                TaskManager.IMP.sync(() -> {
                    World world = BukkitAdapter.adapt(editSession.getWorld());
                    world.spawn(BukkitAdapter.adapt(world, lastBlock), entityClass);
                    return null;
                });
            } catch (IllegalArgumentException exception) {
                messenger.sendMessage(ChatColor.RED + "Cannot spawn entity!");
            }
        }
    }

    @SuppressWarnings("deprecation")
    @Override
    public void sendInfo(Snipe snipe) {
        snipe.createMessageSender()
                .message(ChatColor.LIGHT_PURPLE + "Entity brush" + " (" + this.entityType.getName() + ")")
                .brushSizeMessage()
                .send();
    }

}

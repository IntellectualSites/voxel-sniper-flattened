package com.thevoxelbox.voxelsniper.brush.type;

import com.fastasyncworldedit.core.Fawe;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.thevoxelbox.voxelsniper.sniper.Sniper;
import com.thevoxelbox.voxelsniper.sniper.snipe.Snipe;
import com.thevoxelbox.voxelsniper.sniper.snipe.message.SnipeMessenger;
import com.thevoxelbox.voxelsniper.sniper.toolkit.ToolkitProperties;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.plugin.PluginManager;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class JockeyBrush extends AbstractBrush {

    private static final int ENTITY_STACK_LIMIT = 50;
    private JockeyType jockeyType = JockeyType.NORMAL_ALL_ENTITIES;
    @Nullable
    private Entity jockeyedEntity;

    @Override
    public void handleCommand(String[] parameters, Snipe snipe) {
        SnipeMessenger messenger = snipe.createMessenger();
        boolean stack = false;
        boolean playerOnly = false;
        boolean inverse = false;
        for (String parameter : parameters) {
            int length = parameter.length();
            if (parameter.startsWith("-i:")) {
                inverse = parameter.charAt(length - 1) == 'y';
            }
            if (parameter.startsWith("-po:")) {
                playerOnly = parameter.charAt(length - 1) == 'y';
            }
            if (parameter.startsWith("-s:")) {
                stack = parameter.charAt(length - 1) == 'y';
            }
        }
        if (inverse) {
            this.jockeyType = playerOnly ? JockeyType.INVERSE_PLAYER_ONLY : JockeyType.INVERSE_ALL_ENTITIES;
        } else if (stack) {
            this.jockeyType = playerOnly ? JockeyType.STACK_PLAYER_ONLY : JockeyType.STACK_ALL_ENTITIES;
        } else {
            this.jockeyType = playerOnly ? JockeyType.NORMAL_PLAYER_ONLY : JockeyType.NORMAL_ALL_ENTITIES;
        }
        messenger.sendMessage("Current jockey mode: " + ChatColor.GREEN + this.jockeyType);
    }

    @Override
    public void handleArrowAction(Snipe snipe) {
        if (this.jockeyType == JockeyType.STACK_ALL_ENTITIES || this.jockeyType == JockeyType.STACK_PLAYER_ONLY) {
            stack(snipe);
        } else {
            sitOn(snipe);
        }
    }

    @Override
    public void handleGunpowderAction(Snipe snipe) {
        Sniper sniper = snipe.getSniper();
        Player player = sniper.getPlayer();
        if (this.jockeyType == JockeyType.INVERSE_PLAYER_ONLY || this.jockeyType == JockeyType.INVERSE_ALL_ENTITIES) {
            player.eject();
            player.sendMessage(ChatColor.GOLD + "The guy on top of you has been ejected!");
        } else {
            if (this.jockeyedEntity != null) {
                this.jockeyedEntity.eject();
                this.jockeyedEntity = null;
                player.sendMessage(ChatColor.GOLD + "You have been ejected!");
            }
        }
    }

    private void sitOn(Snipe snipe) {
        Sniper sniper = snipe.getSniper();
        Player player = sniper.getPlayer();
        BlockVector3 targetBlock = getTargetBlock();
        World world = BukkitAdapter.adapt(getEditSession().getWorld());
        int targetChunkX = targetBlock.getX() >> 4;
        int targetChunkZ = targetBlock.getZ() >> 4;
        double range = Double.MAX_VALUE;
        Entity closest = null;
        for (int x = targetChunkX - 1; x <= targetChunkX + 1; x++) {
            for (int y = targetChunkZ - 1; y <= targetChunkZ + 1; y++) {
                if (!world.isChunkLoaded(x, y)) {
                    continue;
                }

                for (Entity entity : world.getChunkAt(x, y).getEntities()) {
                    if (entity.getEntityId() == player.getEntityId()) {
                        continue;
                    }
                    if (this.jockeyType == JockeyType.NORMAL_PLAYER_ONLY || this.jockeyType == JockeyType.INVERSE_PLAYER_ONLY) {
                        if (!(entity instanceof Player)) {
                            continue;
                        }
                    }
                    Location entityLocation = entity.getLocation();
                    double entityDistance = entityLocation.distance(player.getLocation());
                    if (entityDistance < range) {
                        range = entityDistance;
                        closest = entity;
                    }
                }
            }
        }
        if (closest != null) {
            Entity finalClosest = closest;//FAWE ADDED
            Fawe.get().getQueueHandler().sync(() -> {
                PlayerTeleportEvent playerTeleportEvent = new PlayerTeleportEvent(
                        player,
                        player.getLocation(),
                        finalClosest.getLocation(),
                        PlayerTeleportEvent.TeleportCause.PLUGIN
                );
                PluginManager pluginManager = Bukkit.getPluginManager();
                pluginManager.callEvent(playerTeleportEvent);
                if (!playerTeleportEvent.isCancelled()) {
                    if (jockeyType == JockeyType.INVERSE_PLAYER_ONLY || jockeyType == JockeyType.INVERSE_ALL_ENTITIES) {
                        player.addPassenger(finalClosest);
                    } else {
                        finalClosest.addPassenger(player);
                        jockeyedEntity = finalClosest;
                    }
                    player.sendMessage(ChatColor.GREEN + "You are now saddles on entity: " + finalClosest.getEntityId());
                }
            });
        } else {
            player.sendMessage(ChatColor.RED + "Could not find any entities");
        }
    }

    private void stack(Snipe snipe) {
        ToolkitProperties toolkitProperties = snipe.getToolkitProperties();
        Sniper sniper = snipe.getSniper();
        Player player = sniper.getPlayer();
        int brushSizeDoubled = toolkitProperties.getBrushSize() * 2;
        List<Entity> nearbyEntities = player.getNearbyEntities(brushSizeDoubled, brushSizeDoubled, brushSizeDoubled);
        Entity lastEntity = player;
        int stackHeight = 0;
        for (Entity entity : nearbyEntities) {
            if (stackHeight >= ENTITY_STACK_LIMIT) {
                return;
            }
            if (this.jockeyType == JockeyType.STACK_ALL_ENTITIES) {
                lastEntity.addPassenger(entity);
                lastEntity = entity;
                stackHeight++;
            } else if (this.jockeyType == JockeyType.STACK_PLAYER_ONLY) {
                if (entity instanceof Player) {
                    lastEntity.addPassenger(entity);
                    lastEntity = entity;
                    stackHeight++;
                }
            } else {
                player.sendMessage("You broke stack! :O");
            }
        }
    }

    @Override
    public void sendInfo(Snipe snipe) {
        SnipeMessenger messenger = snipe.createMessenger();
        messenger.sendBrushNameMessage();
        messenger.sendMessage("Current jockey mode: " + ChatColor.GREEN + this.jockeyType);
    }

    /**
     * Available types of jockey modes.
     */
    private enum JockeyType {

        NORMAL_ALL_ENTITIES("Normal (All)"),
        NORMAL_PLAYER_ONLY("Normal (Player only)"),
        INVERSE_ALL_ENTITIES("Inverse (All)"),
        INVERSE_PLAYER_ONLY("Inverse (Player only)"),
        STACK_ALL_ENTITIES("Stack (All)"),
        STACK_PLAYER_ONLY("Stack (Player only)");

        private final String name;

        JockeyType(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return this.name;
        }
    }

}

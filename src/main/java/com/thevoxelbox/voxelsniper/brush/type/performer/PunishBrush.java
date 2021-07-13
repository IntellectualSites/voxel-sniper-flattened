package com.thevoxelbox.voxelsniper.brush.type.performer;

import com.fastasyncworldedit.core.util.TaskManager;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.thevoxelbox.voxelsniper.sniper.Sniper;
import com.thevoxelbox.voxelsniper.sniper.snipe.Snipe;
import com.thevoxelbox.voxelsniper.sniper.snipe.message.SnipeMessenger;
import com.thevoxelbox.voxelsniper.sniper.toolkit.ToolkitProperties;
import com.thevoxelbox.voxelsniper.util.Vectors;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.util.List;
import java.util.Random;

public class PunishBrush extends AbstractPerformerBrush {

    private static final int MAX_RANDOM_TELEPORTATION_RANGE = 400;
    private static final int TICKS_PER_SECOND = 20;
    private static final int INFINIPUNISH_SIZE = -3;
    private static final int DEFAULT_PUNISH_LEVEL = 10;
    private static final int DEFAULT_PUNISH_DURATION = 60;

    private Punishment punishment = Punishment.FIRE;
    private int punishLevel = DEFAULT_PUNISH_LEVEL;
    private int punishDuration = DEFAULT_PUNISH_DURATION;
    private boolean specificPlayer;
    private String punishPlayerName = "";
    private boolean hypnoAffectLandscape;
    private boolean hitsSelf;

    @Override
    public void handleCommand(String[] parameters, Snipe snipe) {
        SnipeMessenger messenger = snipe.createMessenger();
        for (int i = 0; i < parameters.length; i++) {
            String parameter = parameters[i].toLowerCase();
            if (parameter.equalsIgnoreCase("info")) {
                snipe.createMessageSender()
                        .message(ChatColor.GOLD + "Punish Brush Options:")
                        .message(ChatColor.AQUA + "Punishments can be set via /b p [punishment]")
                        .message(ChatColor.AQUA + "Punishment level can be set with /vc [level]")
                        .message(ChatColor.AQUA + "Punishment duration in seconds can be set with /vh [duration]")
                        .message(ChatColor.AQUA + "Parameter -toggleHypnoLandscape will make Hypno punishment only affect landscape.")
                        .message(ChatColor.AQUA + "Parameter -toggleSM [playername] will make punishbrush only affect that player.")
                        .message(ChatColor.AQUA + "Parameter -toggleSelf will toggle whether you get hit as well.")
                        .message(ChatColor.AQUA + "Available Punishment Options:")
                        .send();
                StringBuilder punishmentOptions = new StringBuilder();
                for (Punishment punishment : Punishment.values()) {
                    if (punishmentOptions.length() != 0) {
                        punishmentOptions.append(" | ");
                    }
                    punishmentOptions.append(punishment.name());
                }
                messenger.sendMessage(ChatColor.GOLD + punishmentOptions.toString());
                return;
            } else if (parameter.equalsIgnoreCase("-toggleSM")) {
                this.specificPlayer = !this.specificPlayer;
                if (this.specificPlayer) {
                    if (i + 1 >= parameters.length) {
                        messenger.sendMessage(ChatColor.AQUA + "You have to specify a player name after -toggleSM if you want to turn the specific player feature on.");
                    } else {
                        this.punishPlayerName = parameters[++i];
                    }
                }
            } else if (parameter.equalsIgnoreCase("-toggleSelf")) {
                this.hitsSelf = !this.hitsSelf;
                if (this.hitsSelf) {
                    messenger.sendMessage(ChatColor.AQUA + "Your punishments will now affect you too!");
                } else {
                    messenger.sendMessage(ChatColor.AQUA + "Your punishments will no longer affect you!");
                }
            } else if (parameter.equalsIgnoreCase("-toggleHypnoLandscape")) {
                this.hypnoAffectLandscape = !this.hypnoAffectLandscape;
            } else {
                try {
                    this.punishment = Punishment.valueOf(parameter.toUpperCase());
                    messenger.sendMessage(ChatColor.AQUA + this.punishment.name()
                            .toLowerCase() + " punishment selected.");
                } catch (IllegalArgumentException exception) {
                    messenger.sendMessage(ChatColor.AQUA + "No such Punishment.");
                }
            }
        }
    }

    @Override
    public void handleArrowAction(Snipe snipe) {
        ToolkitProperties toolkitProperties = snipe.getToolkitProperties();
        SnipeMessenger messenger = snipe.createMessenger();
        Sniper sniper = snipe.getSniper();
        Player player = sniper.getPlayer();
        if (!player.hasPermission("voxelsniper.punish")) {
            messenger.sendMessage("The server says no!");
            return;
        }
        this.punishDuration = toolkitProperties.getVoxelHeight();
        this.punishLevel = toolkitProperties.getCylinderCenter();
        if (this.specificPlayer) {
            TaskManager.IMP.sync(() -> {
                Player punishedPlayer = Bukkit.getPlayer(this.punishPlayerName);
                if (punishedPlayer == null) {
                    messenger.sendMessage("No player " + this.punishPlayerName + " found.");
                    return null;
                }
                applyPunishment(punishedPlayer, snipe);
                return null;
            });
            return;
        }
        int brushSize = toolkitProperties.getBrushSize();
        int brushSizeSquare = brushSize * brushSize;
        BlockVector3 targetBlock = getTargetBlock();
        TaskManager.IMP.sync(() -> {
            World world = BukkitAdapter.adapt(getEditSession().getWorld());
            Location targetLocation = new Location(world, targetBlock.getX(), targetBlock.getY(), targetBlock.getZ());
            List<LivingEntity> entities = world.getLivingEntities();
            int numPunishApps = 0;
            for (LivingEntity entity : entities) {
                if (player != entity || this.hitsSelf) {
                    if (brushSize >= 0) {
                        try {
                            Location location = entity.getLocation();
                            if (location.distanceSquared(targetLocation) <= brushSizeSquare) {
                                numPunishApps++;
                                applyPunishment(entity, snipe);
                            }
                        } catch (RuntimeException exception) {
                            exception.printStackTrace();
                            messenger.sendMessage("An error occured.");
                            return null;
                        }
                    } else if (brushSize == INFINIPUNISH_SIZE) {
                        numPunishApps++;
                        applyPunishment(entity, snipe);
                    }
                }
            }
            messenger.sendMessage(ChatColor.DARK_RED + "Punishment applied to " + numPunishApps + " living entities.");
            return null;
        });
    }

    @Override
    public void handleGunpowderAction(Snipe snipe) {
        ToolkitProperties toolkitProperties = snipe.getToolkitProperties();
        SnipeMessenger messenger = snipe.createMessenger();
        Sniper sniper = snipe.getSniper();
        Player player = sniper.getPlayer();
        if (!player.hasPermission("voxelsniper.punish")) {
            messenger.sendMessage("The server says no!");
            return;
        }
        int brushSize = toolkitProperties.getBrushSize();
        int brushSizeSquare = brushSize * brushSize;
        World world = BukkitAdapter.adapt(getEditSession().getWorld());//FAWE modified
        BlockVector3 targetBlock = getTargetBlock();
        Location targetLocation = new Location(world, targetBlock.getX(), targetBlock.getY(), targetBlock.getZ());
        List<LivingEntity> entities = world.getLivingEntities();
        for (LivingEntity entity : entities) {
            Location location = entity.getLocation();
            if (location.distanceSquared(targetLocation) < brushSizeSquare) {
                entity.setFireTicks(0);
                entity.removePotionEffect(PotionEffectType.BLINDNESS);
                entity.removePotionEffect(PotionEffectType.CONFUSION);
                entity.removePotionEffect(PotionEffectType.SLOW);
                entity.removePotionEffect(PotionEffectType.JUMP);
            }
        }
    }

    private void applyPunishment(LivingEntity entity, Snipe snipe) {
        ToolkitProperties toolkitProperties = snipe.getToolkitProperties();
        switch (this.punishment) {
            case FIRE:
                entity.setFireTicks(TICKS_PER_SECOND * this.punishDuration);
                break;
            case LIGHTNING:
                entity.getWorld()
                        .strikeLightning(entity.getLocation());
                break;
            case BLINDNESS:
                addEffect(entity, PotionEffectType.BLINDNESS);
                break;
            case DRUNK:
                addEffect(entity, PotionEffectType.CONFUSION);
                break;
            case SLOW:
                addEffect(entity, PotionEffectType.SLOW);
                break;
            case JUMP:
                addEffect(entity, PotionEffectType.JUMP);
                break;
            case ABSORPTION:
                addEffect(entity, PotionEffectType.ABSORPTION);
                break;
            case DAMAGE_RESISTANCE:
                addEffect(entity, PotionEffectType.DAMAGE_RESISTANCE);
                break;
            case FAST_DIGGING:
                addEffect(entity, PotionEffectType.FAST_DIGGING);
                break;
            case FIRE_RESISTANCE:
                addEffect(entity, PotionEffectType.FIRE_RESISTANCE);
                break;
            case HEAL:
                addEffect(entity, PotionEffectType.HEAL);
                break;
            case HEALTH_BOOST:
                addEffect(entity, PotionEffectType.HEALTH_BOOST);
                break;
            case HUNGER:
                addEffect(entity, PotionEffectType.HUNGER);
                break;
            case INCREASE_DAMAGE:
                addEffect(entity, PotionEffectType.INCREASE_DAMAGE);
                break;
            case INVISIBILITY:
                addEffect(entity, PotionEffectType.INVISIBILITY);
                break;
            case NIGHT_VISION:
                addEffect(entity, PotionEffectType.NIGHT_VISION);
                break;
            case POISON:
                addEffect(entity, PotionEffectType.POISON);
                break;
            case REGENERATION:
                addEffect(entity, PotionEffectType.REGENERATION);
                break;
            case SATURATION:
                addEffect(entity, PotionEffectType.SATURATION);
                break;
            case SLOW_DIGGING:
                addEffect(entity, PotionEffectType.SLOW_DIGGING);
                break;
            case SPEED:
                addEffect(entity, PotionEffectType.SPEED);
                break;
            case WATER_BREATHING:
                addEffect(entity, PotionEffectType.WATER_BREATHING);
                break;
            case WEAKNESS:
                addEffect(entity, PotionEffectType.WEAKNESS);
                break;
            case WITHER:
                addEffect(entity, PotionEffectType.WITHER);
                break;
            case KILL:
                entity.setHealth(0.0d);
                break;
            case RANDOMTP:
                Random random = new Random();
                Location targetLocation = entity.getLocation();
                targetLocation.setX(targetLocation.getX() + (random.nextInt(MAX_RANDOM_TELEPORTATION_RANGE) - MAX_RANDOM_TELEPORTATION_RANGE / 2.0));
                targetLocation.setZ(targetLocation.getZ() + (random.nextInt(MAX_RANDOM_TELEPORTATION_RANGE) - MAX_RANDOM_TELEPORTATION_RANGE / 2.0));
                entity.teleport(targetLocation);
                break;
            case ALL_POTION:
                addEffect(entity, PotionEffectType.BLINDNESS);
                addEffect(entity, PotionEffectType.CONFUSION);
                addEffect(entity, PotionEffectType.SLOW);
                addEffect(entity, PotionEffectType.JUMP);
                break;
            case FORCE:
                Vector playerVector = Vectors.toBukkit(getTargetBlock());
                Vector direction = entity.getLocation().toVector().clone();
                direction.subtract(playerVector);
                double length = direction.length();
                double strength = (1 - (length / toolkitProperties.getBrushSize())) * this.punishLevel;
                direction.normalize();
                direction.multiply(strength);
                entity.setVelocity(direction);
                break;
            case HYPNO:
                if (entity instanceof Player) {
                    Location location = entity.getLocation();
                    Location target = location.clone();
                    for (int z = this.punishLevel; z >= -this.punishLevel; z--) {
                        for (int x = this.punishLevel; x >= -this.punishLevel; x--) {
                            for (int y = this.punishLevel; y >= -this.punishLevel; y--) {
                                target.setX(location.getX() + x);
                                target.setY(location.getY() + y);
                                target.setZ(location.getZ() + z);
                                if (this.hypnoAffectLandscape && target.getBlock()
                                        .getType() == Material.AIR) {
                                    continue;
                                }
                                target = location.clone();
                                target.add(x, y, z);
                                ((Player) entity).sendBlockChange(target, BukkitAdapter.adapt(toolkitProperties.getBlockData()));
                            }
                        }
                    }
                }
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + this.punishment);
        }
    }

    private void addEffect(LivingEntity entity, PotionEffectType type) {
        PotionEffect effect = new PotionEffect(type, TICKS_PER_SECOND * this.punishDuration, this.punishLevel);
        entity.addPotionEffect(effect, true);
    }

    @Override
    public void sendInfo(Snipe snipe) {
        snipe.createMessageSender()
                .brushNameMessage()
                .message(ChatColor.GREEN + "Punishment: " + this.punishment)
                .brushSizeMessage()
                .cylinderCenterMessage()
                .send();
    }

    private enum Punishment {
        FIRE,
        LIGHTNING,
        BLINDNESS,
        DRUNK,
        KILL,
        RANDOMTP,
        ALL_POTION,
        SLOW,
        JUMP,
        ABSORPTION,
        DAMAGE_RESISTANCE,
        FAST_DIGGING,
        FIRE_RESISTANCE,
        HEAL,
        HEALTH_BOOST,
        HUNGER,
        INCREASE_DAMAGE,
        INVISIBILITY,
        NIGHT_VISION,
        POISON,
        REGENERATION,
        SATURATION,
        SLOW_DIGGING,
        SPEED,
        WATER_BREATHING,
        WEAKNESS,
        WITHER,
        FORCE,
        HYPNO
    }

}

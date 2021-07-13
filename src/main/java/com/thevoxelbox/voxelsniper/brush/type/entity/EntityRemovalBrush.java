package com.thevoxelbox.voxelsniper.brush.type.entity;

import com.fastasyncworldedit.core.util.TaskManager;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.thevoxelbox.voxelsniper.brush.type.AbstractBrush;
import com.thevoxelbox.voxelsniper.sniper.snipe.Snipe;
import com.thevoxelbox.voxelsniper.sniper.snipe.message.SnipeMessenger;
import com.thevoxelbox.voxelsniper.sniper.toolkit.ToolkitProperties;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.entity.Entity;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.PatternSyntaxException;

public class EntityRemovalBrush extends AbstractBrush {

    private final List<String> exemptions = new ArrayList<>(3);

    public EntityRemovalBrush() {
        this.exemptions.add("org.bukkit.entity.Player");
        this.exemptions.add("org.bukkit.entity.Hanging");
        this.exemptions.add("org.bukkit.entity.NPC");
    }

    @Override
    public void handleCommand(String[] parameters, Snipe snipe) {
        SnipeMessenger messenger = snipe.createMessenger();
        for (String currentParam : parameters) {
            if (!currentParam.isEmpty() && (currentParam.charAt(0) == '+' || currentParam.charAt(0) == '-')) {
                boolean isAddOperation = currentParam.charAt(0) == '+';
                // +#/-# will suppress auto-prefixing
                String exemptionPattern = currentParam.startsWith("+#") || currentParam.startsWith("-#") ? currentParam.substring(
                        2) : (currentParam.contains(".") ? currentParam.substring(1) : ".*." + currentParam.substring(1));
                if (isAddOperation) {
                    this.exemptions.add(exemptionPattern);
                    messenger.sendMessage(String.format("Added %s to entity exemptions list.", exemptionPattern));
                } else {
                    this.exemptions.remove(exemptionPattern);
                    messenger.sendMessage(String.format("Removed %s from entity exemptions list.", exemptionPattern));
                }
            }
            if (currentParam.equalsIgnoreCase("list-exemptions") || currentParam.equalsIgnoreCase("lex")) {
                for (String exemption : this.exemptions) {
                    messenger.sendMessage(ChatColor.LIGHT_PURPLE + exemption);
                }
            }
        }
    }

    @Override
    public void handleArrowAction(Snipe snipe) {
        radialRemoval(snipe);
    }

    @Override
    public void handleGunpowderAction(Snipe snipe) {
        radialRemoval(snipe);
    }

    private void radialRemoval(Snipe snipe) {
        SnipeMessenger messenger = snipe.createMessenger();
        ToolkitProperties toolkitProperties = snipe.getToolkitProperties();
        BlockVector3 targetBlock = getTargetBlock();
        int chunkX = targetBlock.getX() >> 4;
        int chunkZ = targetBlock.getZ() >> 4;
        int entityCount = 0;
        int chunkCount = 0;
        try {
            int radius = Math.round(toolkitProperties.getBrushSize() / 16.0F);
            for (int x = chunkX - radius; x <= chunkX + radius; x++) {
                for (int z = chunkZ - radius; z <= chunkZ + radius; z++) {
                    entityCount += removeEntities(x, z);
                    chunkCount++;
                }
            }
        } catch (PatternSyntaxException exception) {
            exception.printStackTrace();
            messenger.sendMessage(ChatColor.RED + "Error in RegEx: " + ChatColor.LIGHT_PURPLE + exception.getPattern());
            messenger.sendMessage(ChatColor.RED + String.format(
                    "%s (Index: %d)",
                    exception.getDescription(),
                    exception.getIndex()
            ));
        }
        messenger.sendMessage(ChatColor.GREEN + "Removed " + ChatColor.RED + entityCount + ChatColor.GREEN + " entities out of " + ChatColor.BLUE + chunkCount + ChatColor.GREEN + (
                chunkCount == 1
                        ? " chunk."
                        : " chunks."));
    }

    private int removeEntities(int chunkX, int chunkZ) {
        return TaskManager.IMP.sync(() -> {
            World world = BukkitAdapter.adapt(getEditSession().getWorld());
            int entityCount = 0;
            if (!world.isChunkLoaded(chunkX, chunkZ)) {
                return entityCount;
            }

            for (Entity entity : world.getChunkAt(chunkX, chunkZ).getEntities()) {
                if (!isClassInExemptionList(entity.getClass())) {
                    entity.remove();
                    entityCount++;
                }
            }
            return entityCount;
        });
    }

    private boolean isClassInExemptionList(Class<? extends Entity> entityClass) {
        // Create a list of superclasses and interfaces implemented by the current entity type
        List<String> entityClassHierarchy = new ArrayList<>();
        Class<?> currentClass = entityClass;
        while (currentClass != null && !currentClass.equals(Object.class)) {
            entityClassHierarchy.add(currentClass.getCanonicalName());
            for (Class<?> interfaceClass : currentClass.getInterfaces()) {
                entityClassHierarchy.add(interfaceClass.getCanonicalName());
            }
            currentClass = currentClass.getSuperclass();
        }
        return this.exemptions.stream()
                .anyMatch(exemptionPattern -> entityClassHierarchy.stream()
                        .anyMatch(typeName -> typeName.matches(exemptionPattern)));
    }

    @Override
    public void sendInfo(Snipe snipe) {
        snipe.createMessageSender()
                .brushNameMessage()
                .message(ChatColor.GREEN + "Exemptions: " + ChatColor.LIGHT_PURPLE + String.join(", ", this.exemptions))
                .brushSizeMessage()
                .send();
    }

}

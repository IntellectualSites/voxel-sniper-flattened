package com.thevoxelbox.voxelsniper.brush.type.performer;

import com.sk89q.worldedit.math.BlockVector3;
import com.thevoxelbox.voxelsniper.brush.property.BrushProperties;
import com.thevoxelbox.voxelsniper.sniper.snipe.Snipe;
import com.thevoxelbox.voxelsniper.sniper.snipe.message.SnipeMessenger;
import com.thevoxelbox.voxelsniper.sniper.toolkit.ToolkitProperties;
import org.bukkit.ChatColor;

public class CheckerVoxelDiscBrush extends AbstractPerformerBrush {

    private boolean useWorldCoordinates = true;

    @Override
    public void handleCommand(String[] parameters, Snipe snipe) {
        SnipeMessenger messenger = snipe.createMessenger();
        for (String param : parameters) {
            String parameter = param.toLowerCase();
            if (parameter.equals("info")) {
                BrushProperties brushProperties = snipe.getBrushProperties();
                messenger.sendMessage(ChatColor.GOLD + brushProperties.getName() + " Parameters:");
                messenger.sendMessage(ChatColor.AQUA + "true  -- Enables using World Coordinates.");
                messenger.sendMessage(ChatColor.AQUA + "false -- Disables using World Coordinates.");
                return;
            }
            if (parameter.startsWith("true")) {
                this.useWorldCoordinates = true;
                messenger.sendMessage(ChatColor.AQUA + "Enabled using World Coordinates.");
            } else if (parameter.startsWith("false")) {
                this.useWorldCoordinates = false;
                messenger.sendMessage(ChatColor.AQUA + "Disabled using World Coordinates.");
            } else {
                messenger.sendMessage(ChatColor.RED + "Invalid brush parameters! use the info parameter to display parameter info.");
                break;
            }
        }
    }

    @Override
    public void handleArrowAction(Snipe snipe) {
        BlockVector3 targetBlock = getTargetBlock();
        applyBrush(snipe, targetBlock);
    }

    @Override
    public void handleGunpowderAction(Snipe snipe) {
        BlockVector3 lastBlock = getLastBlock();
        applyBrush(snipe, lastBlock);
    }

    private void applyBrush(Snipe snipe, BlockVector3 target) {
        ToolkitProperties toolkitProperties = snipe.getToolkitProperties();
        int brushSize = toolkitProperties.getBrushSize();
        for (int x = brushSize; x >= -brushSize; x--) {
            for (int y = brushSize; y >= -brushSize; y--) {
                int sum = this.useWorldCoordinates ? target.getX() + x + target.getZ() + y : x + y;
                if (sum % 2 != 0) {
                    this.performer.perform(
                            getEditSession(),
                            target.getX() + x,
                            clampY(target.getY()),
                            target.getZ() + y,
                            this.clampY(target.getX() + x, target.getY(), target.getZ() + y)
                    );
                }
            }
        }
    }

    @Override
    public void sendInfo(Snipe snipe) {
        snipe.createMessageSender()
                .brushNameMessage()
                .brushSizeMessage()
                .send();
    }

}

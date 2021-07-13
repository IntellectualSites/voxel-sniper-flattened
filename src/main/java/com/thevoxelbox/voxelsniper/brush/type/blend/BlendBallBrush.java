package com.thevoxelbox.voxelsniper.brush.type.blend;

import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.world.block.BlockType;
import com.thevoxelbox.voxelsniper.sniper.snipe.Snipe;
import com.thevoxelbox.voxelsniper.sniper.snipe.message.SnipeMessenger;
import com.thevoxelbox.voxelsniper.sniper.toolkit.ToolkitProperties;
import com.thevoxelbox.voxelsniper.util.math.MathHelper;
import com.thevoxelbox.voxelsniper.util.painter.Painters;
import org.bukkit.ChatColor;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class BlendBallBrush extends AbstractBlendBrush {

    @Override
    public void handleCommand(String[] parameters, Snipe snipe) {
        SnipeMessenger messenger = snipe.createMessenger();
        if (parameters[0].equalsIgnoreCase("info")) {
            messenger.sendMessage(ChatColor.GOLD + "Blend Ball Parameters:");
            messenger.sendMessage(ChatColor.AQUA + "/b bb water -- toggle include or exclude (default: exclude) water");
            return;
        }
        super.handleCommand(parameters, snipe);
    }

    @Override
    public void blend(Snipe snipe) {
        ToolkitProperties toolkitProperties = snipe.getToolkitProperties();
        int brushSize = toolkitProperties.getBrushSize();
        BlockVector3 targetBlock = getTargetBlock();
        int smallSphereVolume = (int) MathHelper.sphereVolume(brushSize);
        Set<BlockVector3> smallSphere = new HashSet<>(smallSphereVolume);
        Map<BlockVector3, BlockType> smallSphereBlockTypes = new HashMap<>(smallSphereVolume);
        Painters.sphere()
                .center(targetBlock)
                .radius(brushSize)
                .blockSetter(position -> {
                    BlockType type = getBlockType(position);
                    smallSphere.add(position);
                    smallSphereBlockTypes.put(position, type);
                })
                .paint();
        for (BlockVector3 smallSphereBlock : smallSphere) {
            Map<BlockType, Integer> blockTypesFrequencies = new HashMap<>();
            Painters.cube()
                    .center(smallSphereBlock)
                    .radius(1)
                    .blockSetter(position -> {
                        if (position.equals(smallSphereBlock)) {
                            return;
                        }
                        BlockType type = getBlockType(position);
                        blockTypesFrequencies.merge(type, 1, Integer::sum);
                    })
                    .paint();
            CommonMaterial commonMaterial = findCommonMaterial(blockTypesFrequencies);
            BlockType type = commonMaterial.getBlockType();
            if (type != null) {
                smallSphereBlockTypes.put(smallSphereBlock, type);
            }
        }
        setBlocks(smallSphereBlockTypes);
    }

}

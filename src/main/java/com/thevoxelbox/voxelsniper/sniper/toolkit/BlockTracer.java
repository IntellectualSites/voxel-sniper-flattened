package com.thevoxelbox.voxelsniper.sniper.toolkit;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.util.BlockIterator;

import java.util.Iterator;

public class BlockTracer {

    private Block targetBlock;
    private Block lastBlock;

    public BlockTracer(Player player, int distance) {//FAWE modify (make public)
        Location eyeLocation = player.getEyeLocation();
        Block block = eyeLocation.getBlock();
        this.targetBlock = block;
        this.lastBlock = block;
        Iterator<Block> iterator = new BlockIterator(player, distance);
        iterate(iterator);
    }

    private void iterate(Iterator<? extends Block> iterator) {
        while (iterator.hasNext()) {
            Block block = iterator.next();
            this.lastBlock = this.targetBlock;
            this.targetBlock = block;
            if (!block.getType().isEmpty()) {
                return;
            }
        }
    }

    public Block getTargetBlock() {
        return this.targetBlock;
    }

    public Block getLastBlock() {
        return this.lastBlock;
    }

}

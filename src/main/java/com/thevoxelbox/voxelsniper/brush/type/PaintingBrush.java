package com.thevoxelbox.voxelsniper.brush.type;

import com.thevoxelbox.voxelsniper.sniper.Sniper;
import com.thevoxelbox.voxelsniper.sniper.snipe.Snipe;
import com.thevoxelbox.voxelsniper.sniper.snipe.message.SnipeMessenger;
import com.thevoxelbox.voxelsniper.util.ArtHelper;
import org.bukkit.entity.Player;

/**
 * Painting scrolling Brush.
 */
public class PaintingBrush extends AbstractBrush {

    /**
     * Scroll painting forward.
     *
     * @param snipe Sniper caller
     */
    @Override
    public void handleArrowAction(Snipe snipe) {
        Sniper sniper = snipe.getSniper();
        Player player = sniper.getPlayer();
        ArtHelper.paintAuto(player, false);
    }

    /**
     * Scroll painting backwards.
     *
     * @param snipe Sniper caller
     */
    @Override
    public void handleGunpowderAction(Snipe snipe) {
        Sniper sniper = snipe.getSniper();
        Player player = sniper.getPlayer();
        ArtHelper.paintAuto(player, true);
    }

    @Override
    public void sendInfo(Snipe snipe) {
        SnipeMessenger messenger = snipe.createMessenger();
        messenger.sendBrushNameMessage();
    }

}

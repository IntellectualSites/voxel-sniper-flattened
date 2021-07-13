package com.thevoxelbox.voxelsniper.brush;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.math.BlockVector3;
import com.thevoxelbox.voxelsniper.sniper.snipe.Snipe;
import com.thevoxelbox.voxelsniper.sniper.toolkit.ToolAction;

public interface Brush {

    /**
     * Handles parameters passed to brushes.
     *
     * @param parameters Array of string containing parameters
     * @param snipe      Snipe
     */
    void handleCommand(String[] parameters, Snipe snipe);

    void perform(Snipe snipe, ToolAction action, EditSession editSession, BlockVector3 targetBlock, BlockVector3 lastBlock);

    /**
     * The arrow action. Executed when a player RightClicks with an Arrow
     *
     * @param snipe Snipe
     */
    void handleArrowAction(Snipe snipe);

    /**
     * The powder action. Executed when a player RightClicks with Gunpowder
     *
     * @param snipe Snipe
     */
    void handleGunpowderAction(Snipe snipe);

    void sendInfo(Snipe snipe);

}

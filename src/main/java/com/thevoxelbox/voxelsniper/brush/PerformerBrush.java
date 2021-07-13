package com.thevoxelbox.voxelsniper.brush;

import com.thevoxelbox.voxelsniper.performer.PerformerRegistry;
import com.thevoxelbox.voxelsniper.sniper.snipe.Snipe;

public interface PerformerBrush extends Brush {

    void handlePerformerCommand(String[] parameters, Snipe snipe, PerformerRegistry performerRegistry);

    void initialize(Snipe snipe);

    void sendPerformerInfo(Snipe snipe);

}

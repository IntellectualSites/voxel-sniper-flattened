package com.thevoxelbox.voxelsniper.performer.property;

import com.thevoxelbox.voxelsniper.performer.Performer;

@FunctionalInterface
public interface PerformerCreator {

    Performer create();

}

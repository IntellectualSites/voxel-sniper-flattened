package com.thevoxelbox.voxelsniper.brush.property;

import com.thevoxelbox.voxelsniper.brush.Brush;

@FunctionalInterface
public interface BrushCreator {

    Brush create();

}

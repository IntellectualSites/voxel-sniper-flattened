package com.thevoxelbox.voxelsniper.sniper.toolkit;

import org.jetbrains.annotations.Nullable;

import java.util.Arrays;

public enum ToolAction {

    ARROW,
    GUNPOWDER;

    @Nullable
    public static ToolAction getToolAction(String name) {
        return Arrays.stream(values())
                .filter(toolAction -> name.equalsIgnoreCase(toolAction.name()))
                .findFirst()
                .orElse(null);
    }
}

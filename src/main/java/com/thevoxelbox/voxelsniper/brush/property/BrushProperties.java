package com.thevoxelbox.voxelsniper.brush.property;

import org.jetbrains.annotations.Nullable;

import java.util.List;

public class BrushProperties {

    private final String name;
    @Nullable
    private final String permission;
    private final List<String> aliases;
    private final BrushCreator creator;

    BrushProperties(String name, @Nullable String permission, List<String> aliases, BrushCreator creator) {
        this.name = name;
        this.permission = permission;
        this.aliases = aliases;
        this.creator = creator;
    }

    public static BrushPropertiesBuilder builder() {
        return new BrushPropertiesBuilder();
    }

    public String getName() {
        return this.name;
    }

    @Nullable
    public String getPermission() {
        return this.permission;
    }

    public List<String> getAliases() {
        return this.aliases;
    }

    public BrushCreator getCreator() {
        return this.creator;
    }

}

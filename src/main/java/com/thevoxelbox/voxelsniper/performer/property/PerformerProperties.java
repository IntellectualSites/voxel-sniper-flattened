package com.thevoxelbox.voxelsniper.performer.property;

import java.util.List;

public class PerformerProperties {

    private final String name;
    private final boolean usingReplaceMaterial;
    private final List<String> aliases;
    private final PerformerCreator creator;

    PerformerProperties(String name, boolean usingReplaceMaterial, List<String> aliases, PerformerCreator creator) {
        this.name = name;
        this.usingReplaceMaterial = usingReplaceMaterial;
        this.aliases = aliases;
        this.creator = creator;
    }

    public static PerformerPropertiesBuilder builder() {
        return new PerformerPropertiesBuilder();
    }

    public String getName() {
        return this.name;
    }

    public boolean isUsingReplaceMaterial() {
        return this.usingReplaceMaterial;
    }

    public List<String> getAliases() {
        return this.aliases;
    }

    public PerformerCreator getCreator() {
        return this.creator;
    }

}

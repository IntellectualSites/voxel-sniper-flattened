package com.thevoxelbox.voxelsniper.brush;

import com.thevoxelbox.voxelsniper.brush.property.BrushProperties;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BrushRegistry {

    private final Map<String, BrushProperties> brushesProperties = new HashMap<>();

    public void register(BrushProperties properties) {
        List<String> aliases = properties.getAliases();
        for (String alias : aliases) {
            this.brushesProperties.put(alias, properties);
        }
    }

    @Nullable
    public BrushProperties getBrushProperties(String alias) {
        return this.brushesProperties.get(alias);
    }

    public Map<String, BrushProperties> getBrushesProperties() {
        return Collections.unmodifiableMap(this.brushesProperties);
    }

}

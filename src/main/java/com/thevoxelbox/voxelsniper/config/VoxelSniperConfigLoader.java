package com.thevoxelbox.voxelsniper.config;

import org.bukkit.configuration.file.FileConfiguration;

import java.util.List;

/**
 * Configuration storage defining global configurations for VoxelSniper.
 */
public class VoxelSniperConfigLoader {

    private static final String MESSAGE_ON_LOGIN_ENABLED = "message-on-login-enabled";
    private static final String LITESNIPER_MAX_BRUSH_SIZE = "litesniper-max-brush-size";
    private static final String LITESNIPER_RESTRICTED_MATERIALS = "litesniper-restricted-materials";
    private static final boolean DEFAULT_MESSAGE_ON_LOGIN_ENABLED = false;
    private static final int DEFAULT_LITESNIPER_MAX_BRUSH_SIZE = 5;

    private final FileConfiguration config;

    /**
     * @param config Configuration that is going to be used.
     */
    public VoxelSniperConfigLoader(FileConfiguration config) {
        this.config = config;
    }

    /**
     * Returns if the login message is enabled.
     *
     * @return true if message on login is enabled, false otherwise.
     */
    public boolean isMessageOnLoginEnabled() {
        return this.config.getBoolean(MESSAGE_ON_LOGIN_ENABLED, DEFAULT_MESSAGE_ON_LOGIN_ENABLED);
    }

    /**
     * Set the message on login to be enabled or disabled.
     *
     * @param enabled Messages on Login enabled
     */
    public void setMessageOnLoginEnabled(boolean enabled) {
        this.config.set(MESSAGE_ON_LOGIN_ENABLED, enabled);
    }

    /**
     * Returns maximum size of brushes that LiteSnipers can use.
     *
     * @return maximum size
     */
    public int getLitesniperMaxBrushSize() {
        return this.config.getInt(LITESNIPER_MAX_BRUSH_SIZE, DEFAULT_LITESNIPER_MAX_BRUSH_SIZE);
    }

    /**
     * Set maximum size of brushes that LiteSnipers can use.
     *
     * @param size maximum size
     */
    public void setLitesniperMaxBrushSize(int size) {
        this.config.set(LITESNIPER_MAX_BRUSH_SIZE, size);
    }

    /**
     * Returns List of restricted Litesniper Materials.
     *
     * @return List of restricted Litesniper Materials
     */
    public List<String> getLitesniperRestrictedMaterials() {
        return this.config.getStringList(LITESNIPER_RESTRICTED_MATERIALS);
    }

    /**
     * Set new list of restricted Litesniper Materials.
     *
     * @param restrictedMaterials List of restricted Litesniper Materials
     */
    public void setLitesniperRestrictedMaterials(List<String> restrictedMaterials) {
        this.config.set(LITESNIPER_RESTRICTED_MATERIALS, restrictedMaterials);
    }

}

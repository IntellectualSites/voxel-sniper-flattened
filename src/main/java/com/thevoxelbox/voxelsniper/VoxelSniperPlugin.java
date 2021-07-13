package com.thevoxelbox.voxelsniper;

import com.thevoxelbox.voxelsniper.brush.BrushRegistry;
import com.thevoxelbox.voxelsniper.command.CommandRegistry;
import com.thevoxelbox.voxelsniper.config.VoxelSniperConfig;
import com.thevoxelbox.voxelsniper.config.VoxelSniperConfigLoader;
import com.thevoxelbox.voxelsniper.listener.PlayerInteractListener;
import com.thevoxelbox.voxelsniper.listener.PlayerJoinListener;
import com.thevoxelbox.voxelsniper.performer.PerformerRegistry;
import com.thevoxelbox.voxelsniper.sniper.SniperRegistry;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.incendo.serverlib.ServerLib;

import java.io.File;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class VoxelSniperPlugin extends JavaPlugin {

    private VoxelSniperConfig voxelSniperConfig;
    private BrushRegistry brushRegistry;
    private PerformerRegistry performerRegistry;
    private SniperRegistry sniperRegistry;

    public static VoxelSniperPlugin plugin;

    public static JavaPlugin getPlugin() {
        return plugin;
    }

    @Override
    public void onEnable() {
        this.voxelSniperConfig = loadConfig();
        this.brushRegistry = loadBrushRegistry();
        this.performerRegistry = loadPerformerRegistry();
        this.sniperRegistry = new SniperRegistry();
        loadCommands();
        loadListeners();
        new FastAsyncVoxelSniper(this);//FAWE add
        // Enable metrics
        Metrics metrics = new Metrics(this, 6405);
        // Check if we are in a safe environment
        ServerLib.checkUnsafeForks();
        ServerLib.checkJavaLTS();
    }

    private VoxelSniperConfig loadConfig() {
        saveDefaultConfig();
        FileConfiguration config = getConfig();
        VoxelSniperConfigLoader voxelSniperConfigLoader = new VoxelSniperConfigLoader(config);
        boolean messageOnLoginEnabled = voxelSniperConfigLoader.isMessageOnLoginEnabled();
        int litesniperMaxBrushSize = voxelSniperConfigLoader.getLitesniperMaxBrushSize();
        List<Material> litesniperRestrictedMaterials = voxelSniperConfigLoader.getLitesniperRestrictedMaterials()
                .stream()
                .map(Material::matchMaterial)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        return new VoxelSniperConfig(messageOnLoginEnabled, litesniperMaxBrushSize, litesniperRestrictedMaterials);
    }

    private BrushRegistry loadBrushRegistry() {
        BrushRegistry brushRegistry = new BrushRegistry();
        File dataFolder = getDataFolder();
        BrushRegistrar brushRegistrar = new BrushRegistrar(brushRegistry, dataFolder);
        brushRegistrar.registerBrushes();
        return brushRegistry;
    }

    private PerformerRegistry loadPerformerRegistry() {
        PerformerRegistry performerRegistry = new PerformerRegistry();
        PerformerRegistrar performerRegistrar = new PerformerRegistrar(performerRegistry);
        performerRegistrar.registerPerformers();
        return performerRegistry;
    }

    private void loadCommands() {
        CommandRegistry commandRegistry = new CommandRegistry(this);
        CommandRegistrar commandRegistrar = new CommandRegistrar(this, commandRegistry);
        commandRegistrar.registerCommands();
    }

    private void loadListeners() {
        PluginManager pluginManager = Bukkit.getPluginManager();
        pluginManager.registerEvents(new PlayerJoinListener(this), this);
        pluginManager.registerEvents(new PlayerInteractListener(this), this);
    }

    public VoxelSniperConfig getVoxelSniperConfig() {
        return this.voxelSniperConfig;
    }

    public BrushRegistry getBrushRegistry() {
        return this.brushRegistry;
    }

    public PerformerRegistry getPerformerRegistry() {
        return this.performerRegistry;
    }

    public SniperRegistry getSniperRegistry() {
        return this.sniperRegistry;
    }

}

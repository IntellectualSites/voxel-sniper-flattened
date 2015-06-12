/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014 The Voxel Plugineering Team
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.voxelplugineering.voxelsniper.forge;

import static com.google.common.base.Preconditions.checkArgument;

import java.io.File;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLModDisabledEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerAboutToStartEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.event.FMLServerStoppingEvent;

import org.apache.logging.log4j.Logger;

import com.google.common.base.Optional;
import com.voxelplugineering.voxelsniper.api.brushes.GlobalBrushManager;
import com.voxelplugineering.voxelsniper.api.service.command.CommandHandler;
import com.voxelplugineering.voxelsniper.api.service.command.CommandRegistrar;
import com.voxelplugineering.voxelsniper.core.Gunsmith;
import com.voxelplugineering.voxelsniper.core.util.Context;
import com.voxelplugineering.voxelsniper.core.util.defaults.DefaultBrushBuilder;
import com.voxelplugineering.voxelsniper.forge.service.command.ForgeCommandRegistrar;
import com.voxelplugineering.voxelsniper.forge.util.SpongeDetector;

/**
 * The core class of VoxelSniper for minecraft forge.
 */
@Mod(modid = "voxelsniperforge", name = "VoxelSniper-Forge", version = "7.0.0", acceptableRemoteVersions = "*", canBeDeactivated = true)
public class VoxelSniperForge
{

    //@formatter:off
    
    @Instance(value = "voxelsniperforge")
    public static VoxelSniperForge voxelsniper;

    @SidedProxy(clientSide = "com.voxelplugineering.voxelsniper.forge.ClientProxy",
                serverSide = "com.voxelplugineering.voxelsniper.forge.ServerProxy")
    private static CommonProxy proxy;

    //@formatter:on

    private Logger logger;
    private File configDir;
    private boolean disabled = false;

    /**
     * The preinitialization event.
     * 
     * @param event the event
     */
    @EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        this.configDir = event.getModConfigurationDirectory();
        this.logger = event.getModLog();
    }

    /**
     * The Initialization event.
     * 
     * @param event the event
     */
    @EventHandler
    public void onInit(FMLInitializationEvent event)
    {
    }

    /**
     * About to start, used to initialize gunsmith for the session.
     * 
     * @param event The event
     */
    @EventHandler
    public void serverPreStart(FMLServerAboutToStartEvent event)
    {
        if (!SpongeDetector.isSponge())
        {
            Gunsmith.getServiceManager().register(proxy);
            Gunsmith.getServiceManager().start();

            Context context = Gunsmith.getServiceManager().getContext();

            Optional<GlobalBrushManager> bm = context.get(GlobalBrushManager.class);
            checkArgument(bm.isPresent(), "GlobalBrushManager service was not found in the current context.");

            DefaultBrushBuilder.buildBrushes();
            DefaultBrushBuilder.loadAll(bm.get());
        } else
        {
            this.disabled = true;
            this.logger.info("Detected Sponge: disabling VoxelSniper-Forge in favour of sponge version.");
            // Apparently calling this throws errors as the mod lists are backed
            // by immutable maps
            // Loader.instance().runtimeDisableMod("voxelsniperforge");
        }
    }

    /**
     * Server Starting event, this is the location to register commands.
     * 
     * @param event The event
     */
    @EventHandler
    public void serverStarting(FMLServerStartingEvent event)
    {
        if (!SpongeDetector.isSponge())
        {
            Context context = Gunsmith.getServiceManager().getContext();

            Optional<CommandHandler> cmd = context.get(CommandHandler.class);
            checkArgument(cmd.isPresent(), "CommandHandler service was not found in the current context.");

            Optional<CommandRegistrar> registrar = cmd.get().getRegistrar();
            if (registrar.isPresent())
            {
                ((ForgeCommandRegistrar) registrar.get()).flush(event);
            }
        }
    }

    /**
     * If sniper is disabled perform shutdown sequences.
     * 
     * @param event The event
     */
    @EventHandler
    public void onDisabled(FMLModDisabledEvent event)
    {
        if (Gunsmith.getServiceManager().isInitialized())
        {
            Gunsmith.getServiceManager().shutdown();
        }
    }

    /**
     * The shutdown event.
     * 
     * @param event the event
     */
    @EventHandler
    public void onShutdown(FMLServerStoppingEvent event)
    {
        if (Gunsmith.getServiceManager().isInitialized())
        {
            Gunsmith.getServiceManager().shutdown();
        }
    }

    /**
     * Returns the {@link Logger}.
     * 
     * @return the logger
     */
    public Logger getLogger()
    {
        return this.logger;
    }

    /**
     * Returns the {@link SidedProxy}.
     * 
     * @return the proxy
     */
    public CommonProxy getSidedProxy()
    {
        return proxy;
    }

    public File getConfigDir()
    {
        return this.configDir;
    }
}

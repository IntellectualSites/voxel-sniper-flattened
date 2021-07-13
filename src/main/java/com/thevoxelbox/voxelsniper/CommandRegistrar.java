package com.thevoxelbox.voxelsniper;

import com.thevoxelbox.voxelsniper.command.CommandRegistry;
import com.thevoxelbox.voxelsniper.command.executor.BrushExecutor;
import com.thevoxelbox.voxelsniper.command.executor.BrushToolkitExecutor;
import com.thevoxelbox.voxelsniper.command.executor.DefaultExecutor;
import com.thevoxelbox.voxelsniper.command.executor.GotoExecutor;
import com.thevoxelbox.voxelsniper.command.executor.PaintExecutor;
import com.thevoxelbox.voxelsniper.command.executor.PerformerExecutor;
import com.thevoxelbox.voxelsniper.command.executor.VoxelCenterExecutor;
import com.thevoxelbox.voxelsniper.command.executor.VoxelChunkExecutor;
import com.thevoxelbox.voxelsniper.command.executor.VoxelExecutor;
import com.thevoxelbox.voxelsniper.command.executor.VoxelHeightExecutor;
import com.thevoxelbox.voxelsniper.command.executor.VoxelInkExecutor;
import com.thevoxelbox.voxelsniper.command.executor.VoxelInkReplaceExecutor;
import com.thevoxelbox.voxelsniper.command.executor.VoxelListExecutor;
import com.thevoxelbox.voxelsniper.command.executor.VoxelReplaceExecutor;
import com.thevoxelbox.voxelsniper.command.executor.VoxelSniperExecutor;
import com.thevoxelbox.voxelsniper.command.property.CommandProperties;
import org.bukkit.entity.Player;

public class CommandRegistrar {

    private final VoxelSniperPlugin plugin;
    private final CommandRegistry registry;

    public CommandRegistrar(VoxelSniperPlugin plugin, CommandRegistry registry) {
        this.plugin = plugin;
        this.registry = registry;
    }

    public void registerCommands() {
        registerBrushCommand();
        registerBrushToolCommand();
        registerVoxelCenterCommand();
        registerDefaultCommand();
        registerGotoCommand();
        registerVoxelHeightCommand();
        registerVoxelInkCommand();
        registerVoxelInkReplaceCommand();
        registerVoxelListCommand();
        registerPaintCommand();
        registerPerformerCommand();
        registerVoxelReplaceCommand();
        registerVoxelSniperCommand();
        registerVoxelCommand();
        registerVoxelChunkCommand();
    }

    private void registerBrushCommand() {
        CommandProperties properties = CommandProperties.builder()
                .name("brush")
                .description("Brush.")
                .permission("voxelsniper.sniper")
                .alias("b")
                .usage("/b [brush-size]")
                .usage("Example: /b 5 -- Sets a brush radius size of 5 (11 spaces across).")
                .usage("Example: /b b cm -- Sets your brush to the Ball Brush with the combo-mat performer.")
                .sender(Player.class)
                .build();
        BrushExecutor executor = new BrushExecutor(this.plugin);
        this.registry.register(properties, executor);
    }

    private void registerBrushToolCommand() {
        CommandProperties properties = CommandProperties.builder()
                .name("brush_toolkit")
                .description("Brush Toolkit command.")
                .permission("voxelsniper.sniper")
                .alias("btool")
                .usage("/btool -- Prints out command usage information.")
                .sender(Player.class)
                .build();
        BrushToolkitExecutor executor = new BrushToolkitExecutor(this.plugin);
        this.registry.register(properties, executor);
    }

    private void registerVoxelCenterCommand() {
        CommandProperties properties = CommandProperties.builder()
                .name("voxel_center")
                .description("VoxelCenter. VoxelCentroid.")
                .permission("voxelsniper.sniper")
                .alias("vc")
                .usage("/vc")
                .usage("Example: /vc -1 -- Sets the Clone Cylinder's Y value for the base relative to the Clone Point.")
                .sender(Player.class)
                .build();
        VoxelCenterExecutor executor = new VoxelCenterExecutor(this.plugin);
        this.registry.register(properties, executor);
    }

    private void registerDefaultCommand() {
        CommandProperties properties = CommandProperties.builder()
                .name("default")
                .description("FastAsyncVoxelSniper Default.")
                .permission("voxelsniper.sniper")
                .alias("d")
                .usage("/d")
                .usage("Example: /d -- Resets the brush settings to their default values.")
                .sender(Player.class)
                .build();
        DefaultExecutor executor = new DefaultExecutor(this.plugin);
        this.registry.register(properties, executor);
    }

    private void registerGotoCommand() {
        CommandProperties properties = CommandProperties.builder()
                .name("goto")
                .description("Warps to the specified coordinates.")
                .permission("voxelsniper.goto")
                .usage("/goto [X] [Z]")
                .usage("Example: /goto 100 -100 -- Takes the user to the coordinates X: 100, Z: -100. The Y-coordinate will always be 1 more than the Y-coordinate of the highest block at the X and Z-coordinates provided.")
                .sender(Player.class)
                .build();
        GotoExecutor executor = new GotoExecutor();
        this.registry.register(properties, executor);
    }

    private void registerVoxelHeightCommand() {
        CommandProperties properties = CommandProperties.builder()
                .name("voxel_height")
                .description("VoxelHeight.")
                .permission("voxelsniper.sniper")
                .alias("vh")
                .usage("/vh [voxel-height]")
                .usage("Example: /vh -- Sets the brush height.")
                .sender(Player.class)
                .build();
        VoxelHeightExecutor executor = new VoxelHeightExecutor(this.plugin);
        this.registry.register(properties, executor);
    }

    private void registerVoxelInkCommand() {
        CommandProperties properties = CommandProperties.builder()
                .name("voxel_ink")
                .description("VoxelInk (Data Value).")
                .permission("voxelsniper.sniper")
                .alias("vi")
                .usage("/vi [0-16]")
                .usage("Example: /vi -- sets a data value of 6 (e.g. pink wool).")
                .sender(Player.class)
                .build();
        VoxelInkExecutor executor = new VoxelInkExecutor(this.plugin);
        this.registry.register(properties, executor);
    }

    private void registerVoxelInkReplaceCommand() {
        CommandProperties properties = CommandProperties.builder()
                .name("voxel_ink_replace")
                .description("VoxelInkReplace.")
                .permission("voxelsniper.sniper")
                .alias("vir")
                .usage("/vir [0-16]")
                .usage("Example: /vir 12 -- Sets a replace ink brush to select data 6 blocks for replacement.")
                .sender(Player.class)
                .build();
        VoxelInkReplaceExecutor executor = new VoxelInkReplaceExecutor(this.plugin);
        this.registry.register(properties, executor);
    }

    private void registerVoxelListCommand() {
        CommandProperties properties = CommandProperties.builder()
                .name("voxel_list")
                .description("VoxelBlockExclusion list")
                .permission("voxelsniper.sniper")
                .alias("vl")
                .usage("/vl")
                .usage("Example: /vl 89 -5 -- Adds glowstone to the voxel list and removes planks from the voxel list.")
                .sender(Player.class)
                .build();
        VoxelListExecutor executor = new VoxelListExecutor(this.plugin);
        this.registry.register(properties, executor);
    }

    private void registerPaintCommand() {
        CommandProperties properties = CommandProperties.builder()
                .name("paint")
                .description("Change the selected painting to another painting.")
                .permission("voxelsniper.sniper")
                .usage("/paint")
                .usage("Example: /paint -- Cycles through paintings. You must be adjacent to the painting and aiming at it.")
                .sender(Player.class)
                .build();
        PaintExecutor executor = new PaintExecutor();
        this.registry.register(properties, executor);
    }

    private void registerPerformerCommand() {
        CommandProperties properties = CommandProperties.builder()
                .name("performer")
                .description("FastAsyncVoxelSniper performer.")
                .permission("voxelsniper.sniper")
                .alias("p")
                .alias("perf")
                .usage("/p")
                .usage("Example: /p -- Sets the performer of the current brush to \"m\".")
                .usage("/p <performer>")
                .usage("Example: /p <performer> -- Sets the performer of the current brush to given performer.")
                .sender(Player.class)
                .build();
        PerformerExecutor executor = new PerformerExecutor(this.plugin);
        this.registry.register(properties, executor);
    }

    private void registerVoxelReplaceCommand() {
        CommandProperties properties = CommandProperties.builder()
                .name("voxel_replace")
                .description("VoxelReplace.")
                .permission("voxelsniper.sniper")
                .alias("vr")
                .usage("/vr [0-159]")
                .usage("Example: /vr 12 -- Sets a replace brush to select sand blocks for replacement.")
                .sender(Player.class)
                .build();
        VoxelReplaceExecutor executor = new VoxelReplaceExecutor(this.plugin);
        this.registry.register(properties, executor);
    }

    private void registerVoxelSniperCommand() {
        CommandProperties properties = CommandProperties.builder()
                .name("voxel_sniper")
                .description("FastAsyncVoxelSniper Settings.")
                .permission("voxelsniper.sniper")
                .alias("vs")
                .usage("/vs")
                .usage("Example: /vs -- Returns the current brush settings.")
                .sender(Player.class)
                .build();
        VoxelSniperExecutor executor = new VoxelSniperExecutor(this.plugin);
        this.registry.register(properties, executor);
    }

    private void registerVoxelCommand() {
        CommandProperties properties = CommandProperties.builder()
                .name("voxel")
                .description("Voxel input.")
                .permission("voxelsniper.sniper")
                .alias("v")
                .usage("/v [0-159]")
                .usage("Example: /v 1 -- Loads the sniper with Stone blocks.")
                .sender(Player.class)
                .build();
        VoxelExecutor executor = new VoxelExecutor(this.plugin);
        this.registry.register(properties, executor);
    }

    private void registerVoxelChunkCommand() {
        CommandProperties properties = CommandProperties.builder()
                .name("voxel_chunk")
                .description("Update the chunk you are standing in.")
                .permission("voxelsniper.sniper")
                .alias("vchunk")
                .usage("/vchunk")
                .usage("Example: /vchunk -- Loads the chunk you're standing in.")
                .sender(Player.class)
                .build();
        VoxelChunkExecutor executor = new VoxelChunkExecutor();
        this.registry.register(properties, executor);
    }

}

package com.thevoxelbox.voxelsniper;

import com.thevoxelbox.voxelsniper.performer.PerformerRegistry;
import com.thevoxelbox.voxelsniper.performer.property.PerformerProperties;
import com.thevoxelbox.voxelsniper.performer.type.combo.ComboComboNoPhysicsPerformer;
import com.thevoxelbox.voxelsniper.performer.type.combo.ComboComboPerformer;
import com.thevoxelbox.voxelsniper.performer.type.combo.ComboInkNoPhysicsPerformer;
import com.thevoxelbox.voxelsniper.performer.type.combo.ComboInkPerformer;
import com.thevoxelbox.voxelsniper.performer.type.combo.ComboMaterialNoPhysicsPerformer;
import com.thevoxelbox.voxelsniper.performer.type.combo.ComboMaterialPerformer;
import com.thevoxelbox.voxelsniper.performer.type.combo.ComboNoPhysicsPerformer;
import com.thevoxelbox.voxelsniper.performer.type.combo.ComboPerformer;
import com.thevoxelbox.voxelsniper.performer.type.combo.ExcludeComboPerformer;
import com.thevoxelbox.voxelsniper.performer.type.combo.IncludeComboPerformer;
import com.thevoxelbox.voxelsniper.performer.type.ink.ExcludeInkPerformer;
import com.thevoxelbox.voxelsniper.performer.type.ink.IncludeInkPerformer;
import com.thevoxelbox.voxelsniper.performer.type.ink.InkComboNoPhysicsPerformer;
import com.thevoxelbox.voxelsniper.performer.type.ink.InkComboPerformer;
import com.thevoxelbox.voxelsniper.performer.type.ink.InkInkNoPhysicsPerformer;
import com.thevoxelbox.voxelsniper.performer.type.ink.InkInkPerformer;
import com.thevoxelbox.voxelsniper.performer.type.ink.InkMaterialNoPhysicsPerformer;
import com.thevoxelbox.voxelsniper.performer.type.ink.InkMaterialPerformer;
import com.thevoxelbox.voxelsniper.performer.type.ink.InkNoPhysicsPerformer;
import com.thevoxelbox.voxelsniper.performer.type.ink.InkPerformer;
import com.thevoxelbox.voxelsniper.performer.type.material.ExcludeMaterialPerformer;
import com.thevoxelbox.voxelsniper.performer.type.material.IncludeMaterialPerformer;
import com.thevoxelbox.voxelsniper.performer.type.material.MaterialComboNoPhysicsPerformer;
import com.thevoxelbox.voxelsniper.performer.type.material.MaterialComboPerformer;
import com.thevoxelbox.voxelsniper.performer.type.material.MaterialInkNoPhysicsPerformer;
import com.thevoxelbox.voxelsniper.performer.type.material.MaterialInkPerformer;
import com.thevoxelbox.voxelsniper.performer.type.material.MaterialMaterialNoPhysicsPerformer;
import com.thevoxelbox.voxelsniper.performer.type.material.MaterialMaterialPerformer;
import com.thevoxelbox.voxelsniper.performer.type.material.MaterialNoPhysicsPerformer;
import com.thevoxelbox.voxelsniper.performer.type.material.MaterialPerformer;

public class PerformerRegistrar {

    public static final PerformerProperties DEFAULT_PERFORMER_PROPERTIES = PerformerProperties.builder()
            .name("Material")
            .alias("m")
            .alias("material")
            .creator(MaterialPerformer::new)
            .build();

    private final PerformerRegistry registry;

    public PerformerRegistrar(PerformerRegistry registry) {
        this.registry = registry;
    }

    public void registerPerformers() {
        registerMaterialPerformers();
        registerInkPerformers();
        registerComboPerformers();
    }

    private void registerMaterialPerformers() {
        registerMaterialPerformer();
        registerMaterialNoPhysicsPerformer();
        registerMaterialMaterialPerformer();
        registerMaterialMaterialNoPhysicsPerformer();
        registerMaterialInkPerformer();
        registerMaterialInkNoPhysicsPerformer();
        registerMaterialComboPerformer();
        registerMaterialComboNoPhysicsPerformer();
        registerExcludeMaterialPerformer();
        registerIncludeMaterialPerformer();
    }

    private void registerMaterialPerformer() {
        this.registry.register(DEFAULT_PERFORMER_PROPERTIES);
    }

    private void registerMaterialNoPhysicsPerformer() {
        PerformerProperties properties = PerformerProperties.builder()
                .name("Material No Physics")
                .alias("mp")
                .alias("mat-nophys")
                .creator(MaterialNoPhysicsPerformer::new)
                .build();
        this.registry.register(properties);
    }

    private void registerMaterialMaterialPerformer() {
        PerformerProperties properties = PerformerProperties.builder()
                .name("Material Material")
                .usingReplaceMaterial()
                .alias("mm")
                .alias("mat-mat")
                .creator(MaterialMaterialPerformer::new)
                .build();
        this.registry.register(properties);
    }

    private void registerMaterialMaterialNoPhysicsPerformer() {
        PerformerProperties properties = PerformerProperties.builder()
                .name("Material Material No Physics")
                .usingReplaceMaterial()
                .alias("mmp")
                .alias("mat-mat-nophys")
                .creator(MaterialMaterialNoPhysicsPerformer::new)
                .build();
        this.registry.register(properties);
    }

    private void registerMaterialInkPerformer() {
        PerformerProperties properties = PerformerProperties.builder()
                .name("Material Ink")
                .usingReplaceMaterial()
                .alias("mi")
                .alias("mat-ink")
                .creator(MaterialInkPerformer::new)
                .build();
        this.registry.register(properties);
    }

    private void registerMaterialInkNoPhysicsPerformer() {
        PerformerProperties properties = PerformerProperties.builder()
                .name("Material Ink No Physics")
                .usingReplaceMaterial()
                .alias("mip")
                .alias("mat-ink-nophys")
                .creator(MaterialInkNoPhysicsPerformer::new)
                .build();
        this.registry.register(properties);
    }

    private void registerMaterialComboPerformer() {
        PerformerProperties properties = PerformerProperties.builder()
                .name("Material Combo")
                .usingReplaceMaterial()
                .alias("mc")
                .alias("mat-combo")
                .creator(MaterialComboPerformer::new)
                .build();
        this.registry.register(properties);
    }

    private void registerMaterialComboNoPhysicsPerformer() {
        PerformerProperties properties = PerformerProperties.builder()
                .name("Material Combo No Physics")
                .usingReplaceMaterial()
                .alias("mcp")
                .alias("mat-combo-nophys")
                .creator(MaterialComboNoPhysicsPerformer::new)
                .build();
        this.registry.register(properties);
    }

    private void registerExcludeMaterialPerformer() {
        PerformerProperties properties = PerformerProperties.builder()
                .name("Exclude Material")
                .alias("xm")
                .alias("exclude-mat")
                .creator(ExcludeMaterialPerformer::new)
                .build();
        this.registry.register(properties);
    }

    private void registerIncludeMaterialPerformer() {
        PerformerProperties properties = PerformerProperties.builder()
                .name("Include Material")
                .alias("nm")
                .alias("include-mat")
                .creator(IncludeMaterialPerformer::new)
                .build();
        this.registry.register(properties);
    }

    private void registerInkPerformers() {
        registerInkPerformer();
        registerInkMaterialPerformer();
        registerInkInkPerformer();
        registerInkComboPerformer();
        registerInkInkNoPhysicsPerformer();
        registerInkNoPhysicsPerformer();
        registerInkMaterialNoPhysicsPerformer();
        registerInkComboNoPhysicsPerformer();
        registerExcludeInkPerformer();
        registerIncludeInkPerformer();
    }

    private void registerInkPerformer() {
        PerformerProperties properties = PerformerProperties.builder()
                .name("Ink")
                .alias("i")
                .alias("ink")
                .creator(InkPerformer::new)
                .build();
        this.registry.register(properties);
    }

    private void registerInkMaterialPerformer() {
        PerformerProperties properties = PerformerProperties.builder()
                .name("Ink Material")
                .usingReplaceMaterial()
                .alias("im")
                .alias("ink-mat")
                .creator(InkMaterialPerformer::new)
                .build();
        this.registry.register(properties);
    }

    private void registerInkInkPerformer() {
        PerformerProperties properties = PerformerProperties.builder()
                .name("Ink Ink")
                .usingReplaceMaterial()
                .alias("ii")
                .alias("ink-ink")
                .creator(InkInkPerformer::new)
                .build();
        this.registry.register(properties);
    }

    private void registerInkComboPerformer() {
        PerformerProperties properties = PerformerProperties.builder()
                .name("Ink Combo")
                .usingReplaceMaterial()
                .alias("ic")
                .alias("ink-combo")
                .creator(InkComboPerformer::new)
                .build();
        this.registry.register(properties);
    }

    private void registerInkInkNoPhysicsPerformer() {
        PerformerProperties properties = PerformerProperties.builder()
                .name("Ink Ink No Physics")
                .usingReplaceMaterial()
                .alias("iip")
                .alias("ink-ink-nophys")
                .creator(InkInkNoPhysicsPerformer::new)
                .build();
        this.registry.register(properties);
    }

    private void registerInkNoPhysicsPerformer() {
        PerformerProperties properties = PerformerProperties.builder()
                .name("Ink No Physics")
                .alias("ip")
                .alias("ink-nophys")
                .creator(InkNoPhysicsPerformer::new)
                .build();
        this.registry.register(properties);
    }

    private void registerInkMaterialNoPhysicsPerformer() {
        PerformerProperties properties = PerformerProperties.builder()
                .name("Ink Material No Physics")
                .usingReplaceMaterial()
                .alias("imp")
                .alias("ink-mat-nophys")
                .creator(InkMaterialNoPhysicsPerformer::new)
                .build();
        this.registry.register(properties);
    }

    private void registerInkComboNoPhysicsPerformer() {
        PerformerProperties properties = PerformerProperties.builder()
                .name("Ink Combo No Physics")
                .usingReplaceMaterial()
                .alias("icp")
                .alias("ink-combo-nophys")
                .creator(InkComboNoPhysicsPerformer::new)
                .build();
        this.registry.register(properties);
    }

    private void registerExcludeInkPerformer() {
        PerformerProperties properties = PerformerProperties.builder()
                .name("Exclude Ink")
                .alias("xi")
                .alias("exclude-ink")
                .creator(ExcludeInkPerformer::new)
                .build();
        this.registry.register(properties);
    }

    private void registerIncludeInkPerformer() {
        PerformerProperties properties = PerformerProperties.builder()
                .name("Include Ink")
                .alias("ni")
                .alias("include-ink")
                .creator(IncludeInkPerformer::new)
                .build();
        this.registry.register(properties);
    }

    private void registerComboPerformers() {
        registerComboPerformer();
        registerComboNoPhysicsPerformer();
        registerComboMaterialPerformer();
        registerComboMaterialNoPhysicsPerformer();
        registerComboInkPerformer();
        registerComboInkNoPhysicsPerformer();
        registerComboComboPerformer();
        registerComboComboNoPhysicsPerformer();
        registerExcludeComboPerformer();
        registerIncludeComboPerformer();
    }

    private void registerComboPerformer() {
        PerformerProperties properties = PerformerProperties.builder()
                .name("Combo")
                .alias("c")
                .alias("combo")
                .creator(ComboPerformer::new)
                .build();
        this.registry.register(properties);
    }

    private void registerComboNoPhysicsPerformer() {
        PerformerProperties properties = PerformerProperties.builder()
                .name("Combo No Physics")
                .alias("cp")
                .alias("combo-nophys")
                .creator(ComboNoPhysicsPerformer::new)
                .build();
        this.registry.register(properties);
    }

    private void registerComboMaterialPerformer() {
        PerformerProperties properties = PerformerProperties.builder()
                .name("Combo Material")
                .usingReplaceMaterial()
                .alias("cm")
                .alias("combo-mat")
                .creator(ComboMaterialPerformer::new)
                .build();
        this.registry.register(properties);
    }

    private void registerComboMaterialNoPhysicsPerformer() {
        PerformerProperties properties = PerformerProperties.builder()
                .name("Combo Material No Physics")
                .usingReplaceMaterial()
                .alias("cmp")
                .alias("combo-mat-nophys")
                .creator(ComboMaterialNoPhysicsPerformer::new)
                .build();
        this.registry.register(properties);
    }

    private void registerComboInkPerformer() {
        PerformerProperties properties = PerformerProperties.builder()
                .name("Combo Ink")
                .usingReplaceMaterial()
                .alias("ci")
                .alias("combo-ink")
                .creator(ComboInkPerformer::new)
                .build();
        this.registry.register(properties);
    }

    private void registerComboInkNoPhysicsPerformer() {
        PerformerProperties properties = PerformerProperties.builder()
                .name("Combo Ink No Physics")
                .usingReplaceMaterial()
                .alias("cip")
                .alias("combo-ink-nophys")
                .creator(ComboInkNoPhysicsPerformer::new)
                .build();
        this.registry.register(properties);
    }

    private void registerComboComboPerformer() {
        PerformerProperties properties = PerformerProperties.builder()
                .name("Combo Combo")
                .usingReplaceMaterial()
                .alias("cc")
                .alias("combo-combo")
                .creator(ComboComboPerformer::new)
                .build();
        this.registry.register(properties);
    }

    private void registerComboComboNoPhysicsPerformer() {
        PerformerProperties properties = PerformerProperties.builder()
                .name("Combo Combo No Physics")
                .usingReplaceMaterial()
                .alias("ccp")
                .alias("combo-combo-nophys")
                .creator(ComboComboNoPhysicsPerformer::new)
                .build();
        this.registry.register(properties);
    }

    private void registerExcludeComboPerformer() {
        PerformerProperties properties = PerformerProperties.builder()
                .name("Exclude Combo")
                .alias("xc")
                .alias("exclude-combo")
                .creator(ExcludeComboPerformer::new)
                .build();
        this.registry.register(properties);
    }

    private void registerIncludeComboPerformer() {
        PerformerProperties properties = PerformerProperties.builder()
                .name("Include Combo")
                .alias("nc")
                .alias("include-combo")
                .creator(IncludeComboPerformer::new)
                .build();
        this.registry.register(properties);
    }

}

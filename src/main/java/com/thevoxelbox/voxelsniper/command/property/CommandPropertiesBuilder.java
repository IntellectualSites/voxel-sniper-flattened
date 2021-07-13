package com.thevoxelbox.voxelsniper.command.property;

import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;

public class CommandPropertiesBuilder {

    private final List<String> aliases = new ArrayList<>(0);
    private final List<String> usageMessages = new ArrayList<>(1);
    private String name;
    private String description;
    private String permission;
    private Class<? extends CommandSender> senderType;

    public CommandPropertiesBuilder name(String name) {
        this.name = name;
        return this;
    }

    public CommandPropertiesBuilder description(String description) {
        this.description = description;
        return this;
    }

    public CommandPropertiesBuilder permission(String permission) {
        this.permission = permission;
        return this;
    }

    public CommandPropertiesBuilder alias(String alias) {
        this.aliases.add(alias);
        return this;
    }

    public CommandPropertiesBuilder usage(String message) {
        this.usageMessages.add(message);
        return this;
    }

    public CommandPropertiesBuilder sender(Class<? extends CommandSender> senderType) {
        this.senderType = senderType;
        return this;
    }

    public CommandProperties build() {
        if (this.name == null) {
            throw new RuntimeException("Command name must be specified");
        }
        return new CommandProperties(
                this.name,
                this.description,
                this.permission,
                this.aliases,
                this.usageMessages,
                this.senderType
        );
    }

}

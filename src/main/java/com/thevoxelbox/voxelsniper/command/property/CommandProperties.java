package com.thevoxelbox.voxelsniper.command.property;

import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class CommandProperties {

    private static final String DEFAULT_DESCRIPTION = "";
    private static final Class<? extends CommandSender> DEFAULT_SENDER_TYPE = CommandSender.class;

    private final String name;
    @Nullable
    private final String description;
    @Nullable
    private final String permission;
    private final List<String> aliases;
    private final List<String> usageLines;
    @Nullable
    private final Class<? extends CommandSender> senderType;

    CommandProperties(
            String name,
            @Nullable String description,
            @Nullable String permission,
            List<String> aliases,
            List<String> usageLines,
            @Nullable Class<? extends CommandSender> senderType
    ) {
        this.name = name;
        this.description = description;
        this.permission = permission;
        this.aliases = aliases;
        this.usageLines = usageLines;
        this.senderType = senderType;
    }

    public static CommandPropertiesBuilder builder() {
        return new CommandPropertiesBuilder();
    }

    public String getDescriptionOrDefault() {
        return this.description == null ? DEFAULT_DESCRIPTION : this.description;
    }

    public String getUsage() {
        return String.join("\n", this.usageLines);
    }

    public Class<? extends CommandSender> getSenderTypeOrDefault() {
        return this.senderType == null ? DEFAULT_SENDER_TYPE : this.senderType;
    }

    public String getName() {
        return this.name;
    }

    @Nullable
    public String getDescription() {
        return this.description;
    }

    @Nullable
    public String getPermission() {
        return this.permission;
    }

    public List<String> getAliases() {
        return this.aliases;
    }

    public List<String> getUsageLines() {
        return this.usageLines;
    }

    @Nullable
    public Class<? extends CommandSender> getSenderType() {
        return this.senderType;
    }

}

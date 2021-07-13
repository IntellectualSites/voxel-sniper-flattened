package com.thevoxelbox.voxelsniper.sniper;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SniperRegistry {

    private final Map<UUID, Sniper> snipers = new HashMap<>();

    public void register(Sniper sniper) {
        UUID uuid = sniper.getUuid();
        this.snipers.put(uuid, sniper);
    }

    @Nullable
    public Sniper getSniper(Player player) {
        UUID uuid = player.getUniqueId();
        return getSniper(uuid);
    }

    @Nullable
    public Sniper getSniper(UUID uuid) {
        return this.snipers.get(uuid);
    }

    public Map<UUID, Sniper> getSnipers() {
        return Collections.unmodifiableMap(this.snipers);
    }

}

package com.thevoxelbox.voxelsniper.listener;

import org.bukkit.event.Event;

public interface Listener<T extends Event> extends org.bukkit.event.Listener {

    void listen(T event);

}

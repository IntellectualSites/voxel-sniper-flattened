package com.thevoxelbox.voxelsniper.event;

import com.thevoxelbox.voxelsniper.Sniper;
import org.bukkit.block.data.BlockData;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 *
 */
public class SniperMaterialChangedEvent extends Event {

	private static final HandlerList handlers = new HandlerList();
	private final Sniper sniper;
	private final BlockData originalMaterial;
	private final BlockData newMaterial;
	private final String toolId;

	public SniperMaterialChangedEvent(Sniper sniper, String toolId, BlockData originalMaterial, BlockData newMaterial) {
		this.sniper = sniper;
		this.originalMaterial = originalMaterial;
		this.newMaterial = newMaterial;
		this.toolId = toolId;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}

	public BlockData getOriginalMaterial() {
		return this.originalMaterial;
	}

	public BlockData getNewMaterial() {
		return this.newMaterial;
	}

	public Sniper getSniper() {
		return this.sniper;
	}

	public String getToolId() {
		return this.toolId;
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}
}
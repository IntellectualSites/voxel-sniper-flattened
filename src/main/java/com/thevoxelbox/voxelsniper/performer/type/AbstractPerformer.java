package com.thevoxelbox.voxelsniper.performer.type;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.world.block.BlockState;
import com.sk89q.worldedit.world.block.BlockType;
import com.thevoxelbox.voxelsniper.performer.Performer;
import com.thevoxelbox.voxelsniper.sniper.Undo;

public abstract class AbstractPerformer implements Performer {

	private Undo undo;

	public void setBlockType(EditSession editSession, int x, int y, int z, BlockType type) {
		try {
			editSession.setBlock(x, y, z, type.getDefaultState());
		} catch (WorldEditException e) {
			throw new RuntimeException(e);
		}
	}

	public void setBlockData(EditSession editSession, int x, int y, int z, BlockState blockState) {
		try {
			editSession.setBlock(x, y, z, blockState);
		} catch (WorldEditException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void initializeUndo() {
		this.undo = new Undo();
	}

	@Override
	public Undo getUndo() {
		return this.undo;
	}
}

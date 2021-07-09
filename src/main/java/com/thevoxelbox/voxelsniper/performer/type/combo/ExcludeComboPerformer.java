package com.thevoxelbox.voxelsniper.performer.type.combo;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.world.block.BlockState;
import com.thevoxelbox.voxelsniper.performer.type.AbstractPerformer;
import com.thevoxelbox.voxelsniper.sniper.Undo;
import com.thevoxelbox.voxelsniper.sniper.snipe.performer.PerformerSnipe;
import com.thevoxelbox.voxelsniper.sniper.toolkit.ToolkitProperties;

import java.util.List;

public class ExcludeComboPerformer extends AbstractPerformer {

	private List<BlockState> excludeList;
	private BlockState blockData;

	@Override
	public void initialize(PerformerSnipe snipe) {
		ToolkitProperties toolkitProperties = snipe.getToolkitProperties();
		this.blockData = toolkitProperties.getBlockData();
		this.excludeList = toolkitProperties.getVoxelList();
	}

	@Override
	public void perform(EditSession editSession, int x, int y, int z, BlockState block) {
		if (!this.excludeList.contains(block)) {
			Undo undo = getUndo();
			undo.put(block);
			setBlockData(editSession, x, y, z, this.blockData);
		}
	}

	@Override
	public void sendInfo(PerformerSnipe snipe) {
		snipe.createMessageSender()
			.performerNameMessage()
			.voxelListMessage()
			.blockTypeMessage()
			.blockDataMessage()
			.send();
	}
}

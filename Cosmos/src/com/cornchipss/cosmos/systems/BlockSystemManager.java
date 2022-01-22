package com.cornchipss.cosmos.systems;

import java.util.HashMap;
import java.util.Map;

import com.cornchipss.cosmos.blocks.StructureBlock;
import com.cornchipss.cosmos.blocks.modifiers.BlockSystemFactories;
import com.cornchipss.cosmos.blocks.modifiers.ISystemBlock;
import com.cornchipss.cosmos.netty.action.PlayerAction;

public class BlockSystemManager
{
	private Map<String, BlockSystem> systems;

	public BlockSystemManager()
	{
		systems = new HashMap<>();
	}

	public void addBlock(StructureBlock added)
	{
		if (added.block() instanceof ISystemBlock)
		{
			for (String id : ((ISystemBlock) added.block()).systemIds())
			{
				if (!systems.containsKey(id))
					systems.put(id,
						BlockSystemFactories.get(id).create(added.structure()));

				systems.get(id).addBlock(added);
			}
		}
	}

	public void removeBlock(StructureBlock removed)
	{
		if (removed.block() instanceof ISystemBlock)
		{
			for (String id : ((ISystemBlock) removed.block()).systemIds())
			{
				systems.get(id).removeBlock(removed);
			}
		}
	}

	public void update(float delta)
	{
		for (BlockSystem s : systems.values())
		{
			s.update(delta);
		}
	}

	public void sendAction(PlayerAction action)
	{
		for (BlockSystem s : systems.values())
		{
			if (s instanceof IPlayerActionReceiver)
			{
				((IPlayerActionReceiver) s).receiveAction(action);
			}
		}
	}

	/**
	 * The system, null if it doesn't exist
	 * @param id the system's ID
	 * @return The system, null if it doesn't exist
	 */
	public BlockSystem systemFromID(String id)
	{
		return systems.get(id);
	}
}

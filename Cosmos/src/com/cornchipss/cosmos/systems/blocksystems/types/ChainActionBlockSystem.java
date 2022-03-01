package com.cornchipss.cosmos.systems.blocksystems.types;

import org.joml.Vector3i;

import com.cornchipss.cosmos.netty.action.PlayerAction;
import com.cornchipss.cosmos.structures.Structure;
import com.cornchipss.cosmos.systems.IPlayerActionReceiver;

public abstract class ChainActionBlockSystem extends ChainBlockSystem
	implements IPlayerActionReceiver
{
	public ChainActionBlockSystem(Structure s)
	{
		super(s);
	}

	@Override
	public void receiveAction(PlayerAction action)
	{

		for (Node n : nodes())
		{
			performActionAt(n.start(), n.count());

		}
	}

	public abstract void performActionAt(Vector3i position, int chainCount);
}

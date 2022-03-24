package com.cornchipss.cosmos.systems.blocksystems;

import org.joml.Vector3f;
import org.joml.Vector3i;

import com.cornchipss.cosmos.netty.action.PlayerAction;
import com.cornchipss.cosmos.structures.Structure;
import com.cornchipss.cosmos.systems.BlockSystemIDs;
import com.cornchipss.cosmos.systems.blocksystems.types.ChainActionBlockSystem;

public class MiningLaserSystem extends ChainActionBlockSystem
{
	private PlayerAction lastAction = new PlayerAction(0);

	public MiningLaserSystem(Structure s)
	{
		super(s);
	}

	@Override
	public void update(float delta)
	{
		if (lastAction.isFiring())
		{
			receiveAction(lastAction);
		}
	}

	@Override
	public String id()
	{
		return BlockSystemIDs.LASER_CANNON_ID;
	}

	@Override
	public void performActionAt(Vector3i position, int chainCount)
	{
		Vector3i pos = new Vector3i(position.x, position.y,
			position.z - chainCount);
		Vector3f coords = structure().blockCoordsToWorldCoords(pos,
			new Vector3f());

//		structure().world().sendRaycast(coords, structure().body().transform().orientation(), 1000);
	}

	@Override
	public void receiveAction(PlayerAction action)
	{
		lastAction = action;

		if (action.isFiring())
		{
			super.receiveAction(action);
		}
	}
}

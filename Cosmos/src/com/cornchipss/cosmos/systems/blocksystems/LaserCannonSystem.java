package com.cornchipss.cosmos.systems.blocksystems;

import org.joml.Vector3f;
import org.joml.Vector3i;

import com.bulletphysics.dynamics.RigidBody;
import com.cornchipss.cosmos.netty.action.PlayerAction;
import com.cornchipss.cosmos.physx.RigidBodyProxy;
import com.cornchipss.cosmos.structures.Structure;
import com.cornchipss.cosmos.systems.BlockSystemIDs;
import com.cornchipss.cosmos.systems.blocksystems.types.ChainActionBlockSystem;
import com.cornchipss.cosmos.utils.Maths;
import com.cornchipss.cosmos.world.entities.Laser;

public class LaserCannonSystem extends ChainActionBlockSystem
{
	private long lastFireTime = 0;

	private PlayerAction lastAction = new PlayerAction(0);

	public LaserCannonSystem(Structure s)
	{
		super(s);

		lastFireTime = System.currentTimeMillis();
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

		float baseSpeed = structure().body().velocity()
			.dot(structure().body().velocity());
		baseSpeed = Maths.sqrt(baseSpeed);

		Laser laser = new Laser(structure().world(), baseSpeed + 1000,
			structure(), chainCount);
		
		RigidBody rb = laser.createRigidBody(coords, structure().body().orientation().quaternion());

		laser.addToWorld(new RigidBodyProxy(rb));
	}

	@Override
	public void receiveAction(PlayerAction action)
	{
		lastAction = action;

		if (action.isFiring()
			&& System.currentTimeMillis() - 100 > lastFireTime)
		{
			lastFireTime = System.currentTimeMillis();
			super.receiveAction(action);
		}
	}
}

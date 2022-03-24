package com.cornchipss.cosmos.structures;

import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.dynamics.RigidBodyConstructionInfo;
import com.bulletphysics.linearmath.DefaultMotionState;
import com.bulletphysics.linearmath.Transform;
import com.cornchipss.cosmos.world.World;

public class Planet extends Structure
{
	public Planet(World world, int width, int height, int length, int id)
	{
		super(world, width, height, length, id);
	}

	public Planet(World world, int id)
	{
		super(world, id);
	}

	@Override
	protected RigidBody createRigidBody(Transform trans)
	{
		RigidBodyConstructionInfo rbInfo = new RigidBodyConstructionInfo(0.0f,
			new DefaultMotionState(trans), createStructureShape(trans));

		rbInfo.restitution = 0.25f;
		rbInfo.angularDamping = 0.25f;
		rbInfo.friction = 0.25f;

		return new RigidBody(rbInfo);
	}
}

package com.cornchipss.cosmos.structures;

import javax.vecmath.Matrix4f;
import javax.vecmath.Quat4f;

import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.joml.Vector3i;

import com.bulletphysics.collision.shapes.CompoundShape;
import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.dynamics.RigidBodyConstructionInfo;
import com.bulletphysics.linearmath.DefaultMotionState;
import com.bulletphysics.linearmath.Transform;
import com.cornchipss.cosmos.physx.Orientation;
import com.cornchipss.cosmos.utils.Maths;
import com.cornchipss.cosmos.world.Chunk;
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
		CompoundShape shape = new CompoundShape();

		for (Chunk c : this.chunks())
		{
			Vector3f coords = this.chunkCoordsToRelativeWorldCoords(c,
				new Vector3i(0),
				new Vector3f());

			// Could be done better
			new Orientation(Maths.convert(trans.getRotation(new Quat4f()),
				new Quaternionf())).applyRotation(coords, coords);

			shape.addChildShape(
				new Transform(
					new Matrix4f(new Quat4f(),
						new javax.vecmath.Vector3f(coords.x, coords.y,
							coords.z),
						1.0f)),
				c.createCollider());
		}

		RigidBodyConstructionInfo rbInfo = new RigidBodyConstructionInfo(0.0f,
			new DefaultMotionState(trans), shape);

		rbInfo.restitution = 0.25f;
		rbInfo.angularDamping = 0.25f;
		rbInfo.friction = 0.25f;

		return new RigidBody(rbInfo);
	}
}

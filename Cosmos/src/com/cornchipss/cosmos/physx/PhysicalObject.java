package com.cornchipss.cosmos.physx;

import javax.vecmath.Matrix4f;
import javax.vecmath.Quat4f;

import org.joml.Quaternionfc;
import org.joml.Vector3fc;

import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.linearmath.Transform;
import com.cornchipss.cosmos.physx.collision.obb.OBBCollider;
import com.cornchipss.cosmos.world.World;

public abstract class PhysicalObject
{
	private RigidBodyProxy body;
	private World world;

	public abstract OBBCollider OBB();

	public PhysicalObject(World world)
	{
		this.world = world;
	}

	public PhysicalObject(World world, RigidBodyProxy b)
	{
		this.world = world;
		body = b;
	}

	public void addToWorld(RigidBodyProxy body)
	{
		body(body);
		world().addObject(this);
	}

	public boolean initialized()
	{
		return body != null;
	}

	public Vector3fc position()
	{
		return body.position();
	}

	public Orientation orientation()
	{
		return body.orientation();
	}

	public World world()
	{
		return world;
	}

	public void world(World world)
	{
		this.world = world;
	}

	public RigidBodyProxy body()
	{
		return body;
	}

	public void body(RigidBodyProxy body)
	{
		this.body = body;
	}

	public boolean shouldCollideWith(PhysicalObject other)
	{
		return true;
	}

	public RigidBody createRigidBody(Vector3fc startingPos, Quaternionfc startingRotation)
	{
		return createRigidBody(new Transform(new Matrix4f(
			new Quat4f(startingRotation.x(), startingRotation.y(), startingRotation.z(), startingRotation.w()),
			new javax.vecmath.Vector3f(startingPos.x(), startingPos.y(), startingPos.z()), 1.0f)));
	}

	protected abstract RigidBody createRigidBody(Transform transform);
}

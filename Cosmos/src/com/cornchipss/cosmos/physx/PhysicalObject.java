package com.cornchipss.cosmos.physx;

import org.joml.Vector3fc;

import com.cornchipss.cosmos.physx.collision.obb.OBBCollider;
import com.cornchipss.cosmos.world.World;

public abstract class PhysicalObject
{
	private RigidBody body;
	private World world;

	public abstract OBBCollider OBB();

	public PhysicalObject(World world)
	{
		this.world = world;
	}

	public PhysicalObject(World world, RigidBody b)
	{
		this.world = world;
		body = b;
	}

	public abstract void addToWorld(Transform transform);

	public boolean initialized()
	{
		return body != null;
	}

	public Vector3fc position()
	{
		return body.transform().position();
	}

	public World world()
	{
		return world;
	}

	public void world(World world)
	{
		this.world = world;
	}

	public RigidBody body()
	{
		return body;
	}

	public void body(RigidBody body)
	{
		this.body = body;
	}

	public boolean shouldCollideWith(PhysicalObject other)
	{
		return true;
	}
}

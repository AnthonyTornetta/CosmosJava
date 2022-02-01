package com.cornchipss.cosmos.physx.collision;

import org.joml.Vector3f;

import com.cornchipss.cosmos.memory.IReusable;

public class CollisionInfo implements IReusable
{
	public Vector3f normal;
	public float distanceSquared;
	public Vector3f collisionPoint;

	public CollisionInfo()
	{
		normal = new Vector3f();
		collisionPoint = new Vector3f();
		distanceSquared = Float.MAX_VALUE;
	}

	public void set(CollisionInfo i)
	{
		normal.set(i.normal);
		distanceSquared = i.distanceSquared;
		collisionPoint.set(i.collisionPoint);
	}

	@Override
	public void reuse()
	{
		normal.set(0);
		distanceSquared = Float.MAX_VALUE;
		collisionPoint.set(0);
	}
}

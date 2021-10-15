package com.cornchipss.cosmos.physx.collision;

import org.joml.Vector3f;

public class CollisionInfo
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
}

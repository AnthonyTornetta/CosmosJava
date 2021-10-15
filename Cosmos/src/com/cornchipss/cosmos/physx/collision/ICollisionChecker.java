package com.cornchipss.cosmos.physx.collision;

import org.joml.Vector3fc;

import com.cornchipss.cosmos.physx.PhysicalObject;

public interface ICollisionChecker
{
	public boolean colliding(PhysicalObject a, PhysicalObject b, Vector3fc deltaA, CollisionInfo info);
}

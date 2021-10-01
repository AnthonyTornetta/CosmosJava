package com.cornchipss.cosmos.physx.collision;

import org.joml.Vector3f;

import com.cornchipss.cosmos.physx.PhysicalObject;

public interface ICollisionChecker
{
	public boolean colliding(PhysicalObject a, PhysicalObject b, Vector3f normal);
}

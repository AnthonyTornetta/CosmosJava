package com.cornchipss.cosmos.physx.collision;

import org.joml.Vector3f;

import com.cornchipss.cosmos.physx.PhysicalObject;

public interface ICollisionChecker
{
	/**
	 * Checks if two physical objects are touching.  If so, returns the normal of the collision of a onto b
	 * @param a The first physical object
	 * @param b The second physical object
	 * @param normal The normal of the collision assuming A hit B - remains unchanged if no collision
	 * @return true if a collision happened, false if not
	 */
	public boolean colliding(PhysicalObject a, PhysicalObject b, Vector3f normal);
}

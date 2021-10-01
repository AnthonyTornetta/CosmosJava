package com.cornchipss.cosmos.physx.collision.obb;

import org.joml.Vector3f;

public interface IOBBCollisionChecker
{
	/**
	 * In respect to a hitting b
	 * @param a The OBB hitting B
	 * @param b The OBB being hit by A
	 * @param collisionNormal The normal vector result of the collision - unchanged if no collision.  Leave null if no normal is needed
	 * @return If the two OBBs are hitting each other
	 */
	public boolean testOBBOBB(OBBCollider a, OBBCollider b, Vector3f collisionNormal);
}

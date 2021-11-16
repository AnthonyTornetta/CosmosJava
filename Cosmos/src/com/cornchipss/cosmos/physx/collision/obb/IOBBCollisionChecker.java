package com.cornchipss.cosmos.physx.collision.obb;

import org.joml.Vector3fc;

import com.cornchipss.cosmos.physx.collision.CollisionInfo;

public interface IOBBCollisionChecker
{
	/**
	 * In respect to a hitting b
	 * 
	 * @param a The OBB hitting B
	 * @param b The OBB being hit by A
	 * @return If the two OBBs are hitting each other
	 */
	public boolean testOBBOBB(OBBCollider a, OBBCollider b);

	/**
	 * Tests if a moving OBB intersects with a stationary OBB
	 * 
	 * @param aDeltaPos The change in A's position
	 * @param a         The moving OBB
	 * @param b         The stationary OBB
	 * @param normal    The normal of the collision - remains unchanged if no
	 *                  collision happened
	 * @return True if they hit - false if not
	 */
	public boolean testMovingOBBOBB(Vector3fc aDeltaPos, OBBCollider a, OBBCollider b, CollisionInfo info);

	/**
	 * Checks if a line intersections with an OBB
	 * 
	 * @param lineStart The line's start point
	 * @param lineDelta How far the line goes (start + delta = end)
	 * @param c         The collider to check
	 * @param info      The info generated from the collision - leave null if no
	 *                  info needed
	 * @return True if a hit happened, false if not
	 */
	public boolean testLineOBB(Vector3fc lineStart, Vector3fc lineDelta, OBBCollider c, CollisionInfo info);
}

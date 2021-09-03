package com.cornchipss.cosmos.physx.shapes;

import org.joml.Vector3f;
import org.joml.Vector3fc;

import com.cornchipss.cosmos.physx.Orientation;

public interface PhysicsShape
{
	/**
	 * Returns true if the given coordinates are within the shape
	 * @param point The point to check
	 * @param position The center of the shape
	 * @param orientation The orientation of the shape
	 * @return true if it intersects, false if not
	 */
	public boolean pointIntersects(Vector3fc point, Vector3fc position, Orientation orientation);
	
	public boolean lineIntersects(Vector3fc lineStart, Vector3fc lineEnd, Vector3fc position, Orientation orientation, Vector3f res);
}

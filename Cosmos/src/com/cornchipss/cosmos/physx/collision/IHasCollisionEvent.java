package com.cornchipss.cosmos.physx.collision;

import com.cornchipss.cosmos.physx.PhysicalObject;

public interface IHasCollisionEvent
{
	/**
	 * If this is false, cancel the collision
	 * 
	 * @param obj
	 * @return
	 */
	public boolean onCollide(PhysicalObject obj);
}

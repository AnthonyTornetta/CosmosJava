package com.cornchipss.cosmos.physx.collision;

import com.cornchipss.cosmos.physx.PhysicalObject;

public interface IHasCollisionEvent
{
	public void onCollide(PhysicalObject obj);
}

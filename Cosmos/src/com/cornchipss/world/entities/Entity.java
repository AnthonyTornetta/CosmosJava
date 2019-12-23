package com.cornchipss.world.entities;

import com.cornchipss.physics.collision.hitbox.Hitbox;
import com.cornchipss.world.objects.PhysicalObject;

public abstract class Entity extends PhysicalObject
{
	public Entity(float x, float y, float z, Hitbox hitbox)
	{
		super(x, y, z, hitbox);
	}
	
	public abstract void onUpdate();
	
	@Override
	public boolean createsGravity()
	{
		return false;
	}
}

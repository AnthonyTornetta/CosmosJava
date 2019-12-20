package com.cornchipss.entities;

import com.cornchipss.objects.PhysicalObject;
import com.cornchipss.physics.collision.hitbox.Hitbox;

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

package com.cornchipss.world.entities;

import org.joml.Vector3fc;

import com.cornchipss.physics.collision.hitbox.Hitbox;
import com.cornchipss.utils.Maths;
import com.cornchipss.world.Location;

public abstract class ProjectileEntity extends Entity
{	
	public ProjectileEntity(float x, float y, float z, Hitbox hitbox)
	{
		super(x, y, z, hitbox);
	}

	public void fire(Vector3fc vel)
	{
		
	}
	
	public void fire(float velMagnitude, float rx, float ry)
	{
		fire(Maths.toComponents(rx, ry, velMagnitude));
	}
	
	public abstract boolean onHitBlock(Location loc);
	
	public abstract boolean onHitEntity(Entity e);
}

package com.cornchipss.entities;

import org.joml.Vector3fc;

import com.cornchipss.world.Location;

public abstract class ProjectileEntity
{
	public void fire(Vector3fc vel)
	{
		
	}
	
	public void fire(float vel, float rx, float ry)
	{
		
	}
	
	public abstract void onHitBlock(Location loc);
	
	public abstract void onHitEntity(Entity e);
}

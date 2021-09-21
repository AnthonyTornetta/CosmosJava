package com.cornchipss.cosmos.physx.simulation;

import java.util.LinkedList;
import java.util.List;

import org.joml.AABBf;
import org.joml.Vector3f;

import com.cornchipss.cosmos.physx.PhysicalObject;

public class PhysicsWorld
{
	/**
	 * Sector dimensions in meters
	 */
	public static final int SECTOR_DIMENSIONS = 10_000;
	
	private List<PhysicalObject> bodies;
	
	private boolean locked = false;
	
	private List<PhysicalObject> bodiesToAdd;
	
	public PhysicsWorld()
	{
		bodies = new LinkedList<>();
		bodiesToAdd = new LinkedList<>();
	}
	
	public void addPhysicalObject(PhysicalObject bdy)
	{
		if(!locked)
			bodies.add(bdy);
		else
			bodiesToAdd.add(bdy);
	}
	
	public void update(float delta)
	{
		Vector3f vel = new Vector3f();
		Vector3f pos = new Vector3f();
		
		for(PhysicalObject a : bodies)
		{
			vel.set(a.body().velocity()).mul(delta);
			vel.add(a.body().transform().position(), pos);
			
			for(PhysicalObject b : bodies)
			{
				if(!b.equals(a))
				{
					AABBf aA = new AABBf();
					AABBf aB = new AABBf();
					
					if(a.aabb(a.position(), aA).testAABB(b.aabb(b.position(), aB))
							||
							a.aabb(pos, aA).testAABB(aB))
					{
						handlePotentialCollision(a, b, vel, pos, delta, aA, aB);
					}
				}
			}
			
			a.body().transform().position(pos);
		}
	}
	
	private void handlePotentialCollision(PhysicalObject a, PhysicalObject b, Vector3f vel, Vector3f pos, float delta, AABBf aaBBa, AABBf aaBBb)
	{
		a.shape().verticesWithin(aaBBa);
		
		a.body().velocity(a.body().velocity().negate(vel)); // any vector could replace vel here, but it's being re-assigned down below so it doesn't matter
		
		vel.set(a.body().velocity()).mul(delta);
		vel.add(a.body().transform().position(), pos);
	}

	public boolean locked()
	{
		return locked;
	}
	
	public void lock()
	{
		locked = true;
	}
	
	public void unlock()
	{
		locked = false;
		
		while(bodiesToAdd.size() != 0)
			addPhysicalObject(bodiesToAdd.remove(0));
	}
}

package com.cornchipss.cosmos.physx.simulation;

import java.util.LinkedList;
import java.util.List;

import org.joml.AABBf;
import org.joml.Vector3f;
import org.joml.Vector3fc;

import com.cornchipss.cosmos.physx.PhysicalObject;
import com.cornchipss.cosmos.physx.collision.CollisionInfo;
import com.cornchipss.cosmos.physx.collision.DefaultCollisionChecker;
import com.cornchipss.cosmos.physx.collision.ICollisionChecker;
import com.cornchipss.cosmos.utils.Utils;

public class PhysicsWorld
{
	/**
	 * Sector dimensions in meters
	 */
	public static final int SECTOR_DIMENSIONS = 10_000;
	
	private List<PhysicalObject> bodies;
	
	private boolean locked = false;
	
	private List<PhysicalObject> bodiesToAdd;
	
	private ICollisionChecker strategy;
	
	public PhysicsWorld()
	{
		bodies = new LinkedList<>();
		bodiesToAdd = new LinkedList<>();
		
		strategy = new DefaultCollisionChecker();
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
		for(PhysicalObject a : bodies)
		{
			if(a.body().velocity().x() != 0 
					|| a.body().velocity().y() != 0 
					|| a.body().velocity().z() != 0)
			{
				Vector3f deltaA = a.body().velocity().mul(delta, new Vector3f());
	
				for(PhysicalObject b : bodies)
				{
					if(!b.equals(a))
					{
						handlePotentialCollision(a, b, deltaA);
					}
				}
				
				a.body().transform().position(
						a.body().transform().position().add(
								deltaA, new Vector3f()));
			}
		}
	}
	
	private void handlePotentialCollision(PhysicalObject a, PhysicalObject b, Vector3fc deltaA)
	{
		CollisionInfo info = new CollisionInfo();
		
		if(strategy.colliding(a, b, deltaA, info))
		{
			Utils.println(a.getClass().getSimpleName() + " hit " + b.getClass().getSimpleName() + " NORM: " + Utils.toString(info.normal));
			
//			a.body().velocity(a.body().velocity().add(a.body().velocity().mul(info.normal, new Vector3f()), new Vector3f()));
			
		}
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

package com.cornchipss.cosmos.physx.simulation;

import java.util.LinkedList;
import java.util.List;

import org.joml.Vector3f;

import com.cornchipss.cosmos.physx.PhysicalObject;
import com.cornchipss.cosmos.physx.Transform;
import com.cornchipss.cosmos.physx.qu3e.collision.Q3BoxDef;
import com.cornchipss.cosmos.physx.qu3e.dynamics.Q3Body;
import com.cornchipss.cosmos.physx.qu3e.dynamics.Q3Body.BodyStatus;
import com.cornchipss.cosmos.physx.qu3e.dynamics.Q3BodyDef;
import com.cornchipss.cosmos.physx.qu3e.scene.Q3Scene;
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
	private List<PhysicalObject> bodiesToRemove;
	
	private Q3Scene scene;

	private Q3Body body, floor;
	
	public PhysicsWorld()
	{
		bodies = new LinkedList<>();
		bodiesToAdd = new LinkedList<>();
		bodiesToRemove = new LinkedList<>();
		
		scene = new Q3Scene(1.0f / 60.0f, new Vector3f(0, -10, 0), 20);
		
		Q3BodyDef bdyDef = new Q3BodyDef();
		bdyDef.position = new Vector3f(0, 10, 0);
		bdyDef.bodyType = BodyStatus.DynamicBody;
		
		Q3BodyDef floorDef = new Q3BodyDef();
		floorDef.position = new Vector3f(0, 0, 0);
		floorDef.bodyType = BodyStatus.StaticBody;
		
		body = scene.CreateBody(bdyDef);
		floor = scene.CreateBody(floorDef);
		
		Q3BoxDef boxDef = new Q3BoxDef();
		boxDef.set(new Transform(), new Vector3f(0.5f, 0.5f, 0.5f));
		body.AddBox(boxDef);
		
		Q3BoxDef floorBoxDef = new Q3BoxDef();
		floorBoxDef.set(new Transform(), new Vector3f(10.0f, 2.5f, 10.0f));
		floor.AddBox(floorBoxDef);
	}

	protected void addObjectDuringUnlock(PhysicalObject bdy)
	{
		bodies.add(bdy);
	}

	protected void removeObjectDuringUnlock(PhysicalObject bdy)
	{
		bodies.remove(bdy);
	}

	public void addObject(PhysicalObject bdy)
	{
		if (!locked)
			addObjectDuringUnlock(bdy);
		else
			bodiesToAdd.add(bdy);
	}

	public void removeObject(PhysicalObject obj)
	{
		if (!locked)
			removeObjectDuringUnlock(obj);
		else
			bodiesToRemove.add(obj);
	}

	public void update(float delta)
	{
		// TODO: make sure this works w/ delta time
		scene.Step();
		
		Utils.println(body.rb.transform().position());
		
		
		Utils.println(floor.rb.transform().position());
		
		for (PhysicalObject a : bodies)
		{
//			Vector3f deltaA = a.body().velocity().mul(delta, new Vector3f());
//
//			if (deltaA.x != 0 || deltaA.y != 0 || deltaA.z != 0)
//			{
//				for (PhysicalObject b : bodies)
//				{
//					if (!b.equals(a) && b.shouldCollideWith(a)
//						&& a.shouldCollideWith(b))
//					{
//						handlePotentialCollision(a, b, deltaA);
//					}
//				}
//			}
//
//			a.body().velocity().mul(delta, deltaA);
//
//			a.body().transform()
//				.position(a.body().transform().position().add(deltaA, deltaA));
//
//			Vector3f deltaR = a.body().angularVelocity().mul(delta,
//				new Vector3f());
//
//			a.body().transform().rotateRelative(deltaR);
		}
	}

//	private void handlePotentialCollision(PhysicalObject a, PhysicalObject b,
//		Vector3fc deltaA)
//	{
//		CollisionInfo info;
//		
//		// TODO: make this work
//		
//		for (int i = 0; i < 1 && strategy.colliding(a, b, deltaA, info = MemoryPool.getInstanceOrCreate(CollisionInfo.class)); i++)
//		{
//			if (a instanceof IHasCollisionEvent)
//			{
//				if (!((IHasCollisionEvent) a).onCollide(b, info))
//					return;
//			}
//			
//			Vector3f mulBy = MemoryPool.getInstanceOrCreate(Vector3f.class);
//			
//			mulBy.x = Math.signum(a.body().velocity().x()) == Math
//				.signum(info.normal.x) ? 1 : -1;
//			mulBy.y = Math.signum(a.body().velocity().y()) == Math
//				.signum(info.normal.y) ? 1 : -1;
//			mulBy.z = Math.signum(a.body().velocity().z()) == Math
//				.signum(info.normal.z) ? 1 : -1;
//
//			info.normal.x = Math.abs(info.normal.x) * mulBy.x;
//			info.normal.y = Math.abs(info.normal.y) * mulBy.y;
//			info.normal.z = Math.abs(info.normal.z) * mulBy.z;
//
//			info.normal.mul(0.5f);
//
//			if (info.normal.x == 0)
//				info.normal.x = 1;
//			if (info.normal.y == 0)
//				info.normal.y = 1;
//			if (info.normal.z == 0)
//				info.normal.z = 1;
//			
////			Utils.println(info.normal);
////			Utils.println(a.body().velocity().y());
////			a.body().velocity(a.body().velocity().mul(info.normal, mulBy));
////			Utils.println("-> " + a.body().velocity().y());
//			
//			MemoryPool.addToPool(mulBy);
//			MemoryPool.addToPool(info);
//			a.body().velocity(Maths.zero());
//		}
//	}

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

		while (bodiesToAdd.size() != 0)
		{
			if (!bodiesToRemove.remove(bodiesToAdd.get(0)))
			{
				addObjectDuringUnlock(bodiesToAdd.remove(0));
			}
			else
			{
				bodiesToAdd.remove(0);
			}
		}

		while (bodiesToRemove.size() != 0)
			removeObjectDuringUnlock(bodiesToRemove.remove(0));
	}
}

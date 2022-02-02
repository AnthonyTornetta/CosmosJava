package com.cornchipss.cosmos.physx.simulation;

import java.util.LinkedList;
import java.util.List;

import org.joml.Vector3f;
import org.joml.Vector3fc;

import com.cornchipss.cosmos.memory.MemoryPool;
import com.cornchipss.cosmos.physx.PhysicalObject;
import com.cornchipss.cosmos.physx.collision.CollisionInfo;
import com.cornchipss.cosmos.physx.collision.DefaultCollisionChecker;
import com.cornchipss.cosmos.physx.collision.ICollisionChecker;
import com.cornchipss.cosmos.physx.collision.IHasCollisionEvent;
import com.cornchipss.cosmos.utils.Maths;

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

	private ICollisionChecker strategy;

	public PhysicsWorld()
	{
		bodies = new LinkedList<>();
		bodiesToAdd = new LinkedList<>();
		bodiesToRemove = new LinkedList<>();

		strategy = new DefaultCollisionChecker();
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
		for (PhysicalObject a : bodies)
		{
			Vector3f deltaA = a.body().velocity().mul(delta, new Vector3f());

			if (deltaA.x != 0 || deltaA.y != 0 || deltaA.z != 0)
			{
				for (PhysicalObject b : bodies)
				{
					if (!b.equals(a) && b.shouldCollideWith(a)
						&& a.shouldCollideWith(b))
					{
						handlePotentialCollision(a, b, deltaA);
					}
				}
			}

			a.body().velocity().mul(delta, deltaA);

			a.body().transform()
				.position(a.body().transform().position().add(deltaA, deltaA));

			Vector3f deltaR = a.body().angularVelocity().mul(delta,
				new Vector3f());

			a.body().transform().rotateRelative(deltaR);
		}
	}

	private void handlePotentialCollision(PhysicalObject a, PhysicalObject b,
		Vector3fc deltaA)
	{
		CollisionInfo info;
		
		// TODO: make this work
		
		for (int i = 0; i < 1 && strategy.colliding(a, b, deltaA, info = MemoryPool.getInstanceOrCreate(CollisionInfo.class)); i++)
		{
			if (a instanceof IHasCollisionEvent)
			{
				if (!((IHasCollisionEvent) a).onCollide(b, info))
					return;
			}
			
			Vector3f mulBy = MemoryPool.getInstanceOrCreate(Vector3f.class);
			
			mulBy.x = Math.signum(a.body().velocity().x()) == Math
				.signum(info.normal.x) ? 1 : -1;
			mulBy.y = Math.signum(a.body().velocity().y()) == Math
				.signum(info.normal.y) ? 1 : -1;
			mulBy.z = Math.signum(a.body().velocity().z()) == Math
				.signum(info.normal.z) ? 1 : -1;

			info.normal.x = Math.abs(info.normal.x) * mulBy.x;
			info.normal.y = Math.abs(info.normal.y) * mulBy.y;
			info.normal.z = Math.abs(info.normal.z) * mulBy.z;

			info.normal.mul(0.5f);

			if (info.normal.x == 0)
				info.normal.x = 1;
			if (info.normal.y == 0)
				info.normal.y = 1;
			if (info.normal.z == 0)
				info.normal.z = 1;
			
//			Utils.println(info.normal);
//			Utils.println(a.body().velocity().y());
//			a.body().velocity(a.body().velocity().mul(info.normal, mulBy));
//			Utils.println("-> " + a.body().velocity().y());
			
			MemoryPool.addToPool(mulBy);
			MemoryPool.addToPool(info);
			a.body().velocity(Maths.zero());
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

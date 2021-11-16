package com.cornchipss.cosmos.physx.simulation;

import java.util.LinkedList;
import java.util.List;

import org.joml.Vector3f;
import org.joml.Vector3fc;

import com.cornchipss.cosmos.physx.PhysicalObject;
import com.cornchipss.cosmos.physx.collision.CollisionInfo;
import com.cornchipss.cosmos.physx.collision.DefaultCollisionChecker;
import com.cornchipss.cosmos.physx.collision.ICollisionChecker;
import com.cornchipss.cosmos.physx.collision.IHasCollisionEvent;

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

	public void addPhysicalObject(PhysicalObject bdy)
	{
		if (!locked)
			bodies.add(bdy);
		else
			bodiesToAdd.add(bdy);
	}

	public void removePhysicalObject(PhysicalObject obj)
	{
		if (!locked)
			bodies.remove(obj);
		else
			bodiesToRemove.add(obj);
	}

	public void update(float delta)
	{
		for (PhysicalObject a : bodies)
		{
			{
				Vector3f deltaA = a.body().velocity().mul(delta, new Vector3f());

				if (deltaA.x != 0 || deltaA.y != 0 || deltaA.z != 0)
				{
					for (PhysicalObject b : bodies)
					{
						if (!b.equals(a))
						{
							handlePotentialCollision(a, b, deltaA);
						}
					}
				}

				a.body().velocity().mul(delta, deltaA);

				a.body().transform().position(a.body().transform().position().add(deltaA, deltaA));

				Vector3f deltaR = a.body().angularVelocity().mul(delta, new Vector3f());

				a.body().transform().rotateRelative(deltaR);
			}
		}
	}

	private void handlePotentialCollision(PhysicalObject a, PhysicalObject b, Vector3fc deltaA)
	{
		CollisionInfo info = new CollisionInfo();

		if (strategy.colliding(a, b, deltaA, info))
		{
			if (a instanceof IHasCollisionEvent)
			{
				if (!((IHasCollisionEvent) a).onCollide(b))
					return;
			}
			Vector3f mulBy = new Vector3f();
			mulBy.x = Math.signum(a.body().velocity().x()) == Math.signum(info.normal.x) ? 1 : -1;
			mulBy.y = Math.signum(a.body().velocity().y()) == Math.signum(info.normal.y) ? 1 : -1;
			mulBy.z = Math.signum(a.body().velocity().z()) == Math.signum(info.normal.z) ? 1 : -1;

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

			a.body().velocity(a.body().velocity().mul(info.normal, new Vector3f()));
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
				addPhysicalObject(bodiesToAdd.remove(0));
			}
			else
			{
				bodiesToAdd.remove(0);
			}
		}

		while (bodiesToRemove.size() != 0)
			removePhysicalObject(bodiesToRemove.remove(0));
	}
}

package com.cornchipss.cosmos.world.entities;

import org.joml.Vector3f;

import com.cornchipss.cosmos.physx.PhysicalObject;
import com.cornchipss.cosmos.physx.RigidBody;
import com.cornchipss.cosmos.physx.Transform;
import com.cornchipss.cosmos.physx.collision.IHasCollisionEvent;
import com.cornchipss.cosmos.physx.collision.obb.OBBCollider;
import com.cornchipss.cosmos.utils.Utils;
import com.cornchipss.cosmos.world.World;

public class Laser extends PhysicalObject implements IHasCollisionEvent
{
	private Vector3f halfwidths = new Vector3f(0.1f, 0.1f, 0.4f);

	private float speed;

	public Laser(World world, float speed)
	{
		super(world);
		this.speed = speed;
	}

	@Override
	public OBBCollider OBB()
	{
		return new OBBCollider(this.position(), this.body().transform().orientation(), halfwidths);
	}

	@Override
	public void addToWorld(Transform transform)
	{
		body(new RigidBody(transform));
		world().addPhysicalObject(this);

		this.body().velocity(this.body().transform().orientation().forward().mul(speed, new Vector3f()));
	}

	@Override
	public void onCollide(PhysicalObject obj)
	{
		Utils.println("BYE!");
		this.world().removePhysicalObject(this);
	}
}
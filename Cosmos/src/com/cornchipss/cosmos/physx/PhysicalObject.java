package com.cornchipss.cosmos.physx;

import org.joml.Vector3fc;

import com.cornchipss.cosmos.world.ZaWARUDO;

public abstract class PhysicalObject
{
	private RigidBody body;
	private ZaWARUDO world;
	
	private AABB aabb;
	
	public AABB aabb() { return aabb; }
	public void aabb(AABB aabb) { this.aabb = aabb; }
	
	public PhysicalObject(ZaWARUDO world)
	{
		this.world = world;
	}
	
	public PhysicalObject(ZaWARUDO world, RigidBody b)
	{
		this.world = world;
		body = b;
	}
	
	public abstract void addToWorld(Transform transform);
	
	public boolean initialized()
	{
		return body != null;
	}
	
	public Vector3fc position()
	{
		return body.transform().position();
	}
	
	public ZaWARUDO world() { return world; }
	public void world(ZaWARUDO world) { this.world = world; }
	
	public RigidBody body() { return body; }
	public void body(RigidBody body) { this.body = body; }
}

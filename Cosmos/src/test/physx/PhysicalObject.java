package test.physx;

import javax.vecmath.Vector3f;

import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.linearmath.Transform;

import test.Vec3;
import test.world.ZaWARUDO;

public abstract class PhysicalObject
{
	private RigidBody body;
	private ZaWARUDO world;
	
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
	
	public Vec3 position()
	{
		return new Vec3(body.getCenterOfMassPosition(new Vector3f()));
	}
	
	public ZaWARUDO world() { return world; }
	public void world(ZaWARUDO world) { this.world = world; }
	
	public RigidBody body() { return body; }
	public void body(RigidBody body) { this.body = body; }
}

package test.physx;

import javax.vecmath.Vector3f;

import com.bulletphysics.dynamics.RigidBody;

import test.Vec3;

public class PhysicalObject
{
	private RigidBody body;
	
	public PhysicalObject(RigidBody b)
	{
		body = b;
	}
	
	public Vec3 position()
	{
		return new Vec3(body.getCenterOfMassPosition(new Vector3f()));
	}
	
	public RigidBody body() { return body; }
	public void body(RigidBody body) { this.body = body; }
}

package test.physx;

import javax.vecmath.Vector3f;

import com.bulletphysics.dynamics.RigidBody;

public class PhysicalObject
{
	private RigidBody body;
	
	public PhysicalObject(RigidBody b)
	{
		body = b;
	}
	
	public Vector3f position()
	{
		return body.getCenterOfMassPosition(new Vector3f());
	}
	
	public RigidBody body() { return body; }
	public void body(RigidBody body) { this.body = body; }
}

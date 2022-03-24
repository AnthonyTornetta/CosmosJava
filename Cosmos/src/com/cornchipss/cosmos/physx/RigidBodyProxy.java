package com.cornchipss.cosmos.physx;

import javax.vecmath.Quat4f;

import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Quaternionfc;
import org.joml.Vector3f;
import org.joml.Vector3fc;

import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.linearmath.Transform;
import com.cornchipss.cosmos.utils.Utils;

public class RigidBodyProxy implements IHasPositionAndRotation
{
	private RigidBody linkedBody;

	private javax.vecmath.Vector3f reusableVector = new javax.vecmath.Vector3f();
	private Vector3f velocity = new Vector3f();
	private Vector3f position = new Vector3f();
	
	public RigidBodyProxy(RigidBody link)
	{
		linkedBody = link;
	}
	
	public void navigateTowards(Vector3fc vec)
	{
		position(vec);
	}
	
	@Override
	public Vector3fc position()
	{
		linkedBody.getCenterOfMassPosition(reusableVector);
		position.set(reusableVector.x, reusableVector.y, reusableVector.z);
		return position;
	}
	
	public void position(Vector3fc vec)
	{
		Transform worldPos = new Transform();
		linkedBody.getWorldTransform(worldPos);
		worldPos.origin.set(vec.x(), vec.y(), vec.z());
		linkedBody.setWorldTransform(worldPos);
	}

	public void rotateTowards(Quaternionfc quat)
	{
		Transform worldPos = new Transform();
		linkedBody.getWorldTransform(worldPos);

		Quat4f q = new Quat4f();
		worldPos.getRotation(q);
		
		Quaternionf f = new Quaternionf(q.x, q.y, q.z, q.w).nlerp(quat, 0.1f);
		
		q.set(f.x, f.y, f.z, f.w);
		
		worldPos.setRotation(q);
		linkedBody.setWorldTransform(worldPos);
	}

	/**
	 * @return the velocity
	 */
	public Vector3fc velocity()
	{
		linkedBody.getLinearVelocity(reusableVector);
		velocity.set(reusableVector.x, reusableVector.y, reusableVector.z);
		return velocity;
	}

	/**
	 * @param velocity the velocity to set
	 */
	public void velocity(Vector3fc velocity)
	{
		reusableVector.x = velocity.x();
		reusableVector.y = velocity.y();
		reusableVector.z = velocity.z();
		
		linkedBody.setLinearVelocity(reusableVector);
	}

	/**
	 * @return the angularVelocity
	 */
	public Vector3fc angularVelocity()
	{
		linkedBody.getAngularVelocity(reusableVector);
		velocity.set(reusableVector.x, reusableVector.y, reusableVector.z);
		return velocity;
	}

	/**
	 * @param angularVelocity the angularVelocity to set
	 */
	public void angularVelocity(Vector3fc angularVelocity)
	{
		reusableVector.set(angularVelocity.x(), angularVelocity.y(), angularVelocity.z());
		linkedBody.setAngularVelocity(reusableVector);
	}
	
	@Override
	public Orientation orientation()
	{
		Quat4f q = linkedBody.getWorldTransform(new Transform()).getRotation(new Quat4f());
		return new Orientation(new Quaternionf(q.x, q.y, q.z, q.w));
	}
	
	public void orientation(Orientation o)
	{
		Transform t = linkedBody.getWorldTransform(new Transform());
		t.setRotation(new Quat4f(o.quaternion().x(), o.quaternion().y(), o.quaternion().z(), o.quaternion().w()));
		linkedBody.setWorldTransform(t);
	}

	public void rotateRelative(Vector3f deltaR)
	{
		Orientation o = orientation();
		o.rotateRelative(deltaR);
		orientation(o);
	}
	
	public Matrix4f matrix()
	{
		Matrix4f transMatrix = new Matrix4f();
		
		transMatrix.identity();
		
		transMatrix.translate(position);

		orientation().applyRotation(transMatrix);

		return transMatrix;
	}
	
	public Matrix4f invertedMatrix()
	{
		return matrix().invert();
	}

	public Vector3fc forward()
	{
		return orientation().forward();
	}

	public Vector3fc right()
	{
		return orientation().right();
	}

	public Vector3fc up()
	{
		return orientation().up();
	}
	
	/**
	 * Used internally - please avoid modifying this - can mess other stuff up
	 * @return The JBullet's rigid body
	 */
	public RigidBody jbulletRigidBody()
	{
		return linkedBody;
	}
	
	@Override
	public String toString()
	{
		Vector3fc pos = position();
		Vector3fc vel = velocity();
//		Orientation or = orientation();
		return "Body [ Pos: " + Utils.toEasyString(pos) + " Vel: " + Utils.toEasyString(vel) + " ]";
	}
	
	@Override
	public boolean equals(Object o)
	{
		return o instanceof RigidBodyProxy
			&&
			((RigidBodyProxy)o).linkedBody.equals(linkedBody);
	}
	
	@Override
	public int hashCode()
	{
		return linkedBody.hashCode();
	}
}

package com.cornchipss.cosmos.physx;

import org.joml.Quaternionfc;
import org.joml.Vector3f;
import org.joml.Vector3fc;

import com.cornchipss.cosmos.utils.Maths;

public class RigidBody
{
	private Transform transform;
	private Vector3f velocity;
	private Vector3f angularVelocity;

	public RigidBody(Transform t)
	{
		this(t, Maths.zero(), Maths.zero());
	}

	public RigidBody(Transform t, Vector3fc vel)
	{
		this(t, vel, Maths.zero());
	}

	public RigidBody(Transform t, Vector3fc vel, Vector3fc angularVel)
	{
		this.transform = t;
		this.velocity = new Vector3f().set(vel);
		this.angularVelocity = new Vector3f().set(angularVel);
	}

	public void navigateTowards(Vector3fc vec)
	{
		transform.position(vec);
//		vec.sub(transform.position(), this.velocity);
	}

	public void rotateTowards(Quaternionfc quat)
	{
		transform.orientation().lerpTowards(quat, 0.1f);
	}

	/**
	 * @return the transform
	 */
	public Transform transform()
	{
		return transform;
	}

	/**
	 * @param trans the transform to set
	 */
	public void transform(Transform trans)
	{
		this.transform = trans;
	}

	/**
	 * @return the velocity
	 */
	public Vector3fc velocity()
	{
		return velocity;
	}

	/**
	 * @param velocity the velocity to set
	 */
	public void velocity(Vector3fc velocity)
	{
		this.velocity.set(velocity);
	}

	/**
	 * @return the angularVelocity
	 */
	public Vector3fc angularVelocity()
	{
		return angularVelocity;
	}

	/**
	 * @param angularVelocity the angularVelocity to set
	 */
	public void angularVelocity(Vector3fc angularVelocity)
	{
		this.angularVelocity.set(angularVelocity);
	}
}

package com.cornchipss.cosmos.physx.qu3e.collision;

import org.joml.Vector3f;
import org.joml.Vector3fc;

import com.cornchipss.cosmos.physx.Transform;

public class Q3BoxDef
{
	public float friction, restitution, density;
	public boolean sensor;
	public Transform tx;
	public Vector3f extents;

	public Q3BoxDef()
	{
		friction = 0.4f;
		restitution = 0.2f;
		density = 1.0f;
		sensor = false;
	}

	// https://github.com/RandyGaul/qu3e/blob/master/src/collision/q3Box.inl


	public void set(Transform tx, Vector3fc extents )
	{
		this.tx = tx;
		if(this.extents == null)
			this.extents = new Vector3f();
		this.extents.set(extents).mul(0.5f);
	}
	
	public Transform tx()
	{
		return tx;
	}
	
	public Vector3fc extents()
	{
		return extents;
	}
	
	// --------------------------------------------------------------------------------------------------
	void restitution(float restitution)
	{
		this.restitution = restitution;
	}
	
	public float restitution()
	{
		return this.restitution;
	}

	// --------------------------------------------------------------------------------------------------
	void friction(float friction)
	{
		this.friction = friction;
	}
	
	public float friction()
	{
		return friction;
	}

	// --------------------------------------------------------------------------------------------------

	void density(float density)
	{
		this.density = density;
	}

	public float density()
	{
		return density;
	}
	
	// --------------------------------------------------------------------------------------------------

	void sensor(boolean sensor)
	{
		this.sensor = sensor;
	}
	
	public boolean sensor()
	{
		return sensor;
	}
}

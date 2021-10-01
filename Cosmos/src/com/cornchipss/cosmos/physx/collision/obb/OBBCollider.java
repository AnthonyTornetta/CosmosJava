package com.cornchipss.cosmos.physx.collision.obb;

import org.joml.Vector3f;
import org.joml.Vector3fc;

import com.cornchipss.cosmos.physx.Orientation;

public class OBBCollider
{
	private Vector3f center;
	private Vector3f[] localAxis = new Vector3f[3];
	private Vector3f halfwidths;
	private Orientation or;
	
	public OBBCollider(Vector3fc center, Orientation orientation, Vector3fc halfwidths)
	{
		this.center = new Vector3f(center);
		localAxis[0] = new Vector3f(orientation.right());
		localAxis[1] = new Vector3f(orientation.up());
		localAxis[2] = new Vector3f(orientation.forward());
		this.halfwidths = new Vector3f(halfwidths);
		this.or = orientation;
	}
	
	public Vector3f center() { return center; }
	public Vector3f[] localAxis() { return localAxis; }
	public Vector3f halfwidths() { return halfwidths; }
	public Orientation orientation() { return or; }
}

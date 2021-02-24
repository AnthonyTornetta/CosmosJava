package com.cornchipss.cosmos.physx;

import org.joml.Quaternionf;
import org.joml.Quaternionfc;
import org.joml.Vector3f;
import org.joml.Vector3fc;

import com.cornchipss.cosmos.utils.Maths;

public class Transform
{
	private Vector3f position;
	private Quaternionf rotation;
	
	public Transform()
	{
		this(0, 0, 0);
	}

	public Transform(float x, float y, float z)
	{
		position = new Vector3f(x, y, z);
		rotation = Maths.blankQuaternion();
	}
	
	public Transform(Vector3fc pos)
	{
		this(pos.x(), pos.y(), pos.z());
	}
	
	public void position(Vector3fc p)
	{
		position.set(p);
	}
	
	public Vector3fc position()
	{
		return position;
	}
	
	public Quaternionfc rotation()
	{
		return rotation;
	}
}

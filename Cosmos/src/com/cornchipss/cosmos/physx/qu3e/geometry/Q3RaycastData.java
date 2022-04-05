package com.cornchipss.cosmos.physx.qu3e.geometry;

import org.joml.Vector3f;
import org.joml.Vector3fc;

public class Q3RaycastData
{
	public Vector3f start;
	public Vector3f dir; // normalized
	
	public float t; // time specifying ray endpoint
	
	public float toi; // solved time of impact
	
	public Vector3f normal; // surface normal at impact
	
	void Set(Vector3fc startPoint, Vector3fc dir, float endPointTime)
	{
		this.start = new Vector3f().set(startPoint);
		this.dir = new Vector3f().set(dir);
		this.t = endPointTime;
	}
	
	public Vector3f GetImpactPoint()
	{
		return dir.mul(toi, new Vector3f()).add(start);
	}
}

package com.cornchipss.cosmos.physx.qu3e.dynamics.contact;

import org.joml.Vector3f;

import com.cornchipss.cosmos.physx.qu3e.collision.Q3Box;

public class Q3Manifold
{
	public void SetPair( Q3Box a, Q3Box b )
	{
		this.A = a;
		this.B = b;
	}
	
	public Q3Box A;
	public Q3Box B;

	public Vector3f normal;				// From A to B
	public Vector3f tangentVectors[] = new Vector3f[2];	// Tangent vectors
	public Q3Contact contacts[] = new Q3Contact[8];
	public int contactCount;

	public Q3Manifold next;
	public Q3Manifold prev;

	public boolean sensor;
}

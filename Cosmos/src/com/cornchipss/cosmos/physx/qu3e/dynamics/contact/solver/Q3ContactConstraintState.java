package com.cornchipss.cosmos.physx.qu3e.dynamics.contact.solver;

import org.joml.Matrix3f;
import org.joml.Vector3f;

public class Q3ContactConstraintState
{
	public Q3ContactState contacts[] = new Q3ContactState[8];
	public int contactCount;
	public Vector3f tangentVectors[] = new Vector3f[2]; // Tangent vectors
	public Vector3f normal; // From A to B
	public Vector3f centerA;
	public Vector3f centerB;
	public Matrix3f iA;
	public Matrix3f iB;
	public float mA;
	public float mB;
	public float restitution;
	public float friction;
	public int indexA;
	public int indexB;
}

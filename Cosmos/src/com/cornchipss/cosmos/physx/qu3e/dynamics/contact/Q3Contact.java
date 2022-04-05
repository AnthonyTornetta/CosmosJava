package com.cornchipss.cosmos.physx.qu3e.dynamics.contact;

import org.joml.Vector3f;

public class Q3Contact
{
	public Vector3f position;			// World coordinate of contact
	public float penetration;			// Depth of penetration from collision
	public float normalImpulse;			// Accumulated normal impulse
	public float[] tangentImpulse = new float[2];	// Accumulated friction impulse
	public float bias;					// Restitution + baumgarte
	public float normalMass;				// Normal constraint mass
	public float[] tangentMass = new float[2];		// Tangent constraint mass
	public Q3FeaturePair fp;			// Features on A and B for this contact
	public char warmStarted;				// Used for debug rendering

}

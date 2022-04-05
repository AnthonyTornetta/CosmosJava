package com.cornchipss.cosmos.physx.qu3e.dynamics.contact.solver;

import org.joml.Vector3f;

public class Q3ContactState
{
	public Vector3f ra; // Vector from C.O.M to contact position
	public Vector3f rb; // Vector from C.O.M to contact position
	public float penetration; // Depth of penetration from collision
	public float normalImpulse; // Accumulated normal impulse
	public float tangentImpulse[] = new float[2]; // Accumulated friction
													// impulse
	public float bias; // Restitution + baumgarte
	public float normalMass; // Normal constraint mass
	public float tangentMass[] = new float[2]; // Tangent constraint mass
}

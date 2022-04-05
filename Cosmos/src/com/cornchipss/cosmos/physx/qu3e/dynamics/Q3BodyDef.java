package com.cornchipss.cosmos.physx.qu3e.dynamics;

import org.joml.Quaternionf;
import org.joml.Vector3f;

public class Q3BodyDef
{
	public static int DEFAULT_LAYER = 1;
	
	public Q3BodyDef()
	{
		// Set all initial positions/velocties to zero
		rotation = new Quaternionf();
		position = new Vector3f();
		linearVelocity = new Vector3f();
		angularVelocity = new Vector3f();

		// Usually a gravity scale of 1 is the best
		gravityScale = 1.0f;

		// Common default values
		bodyType = Q3Body.BodyStatus.StaticBody;
		layers = DEFAULT_LAYER;
		userData = null;
		allowSleep = true;
		awake = true;
		active = true;
		lockAxisX = false;
		lockAxisY = false;
		lockAxisZ = false;

		linearDamping = 0.0f;
		angularDamping = 1.0f;
	}

	public Quaternionf rotation; // Initial world transformation. Radians.
	public Vector3f position; // Initial world transformation.
	public Vector3f linearVelocity; // Initial linear velocity in world space.
	public Vector3f angularVelocity; // Initial angular velocity in world space.
	public float gravityScale; // Convenient scale values for gravity x, y and z
						// directions.
	public int layers; // Bitmask of collision layers. Bodies matching at least one
				// layer can collide.
	public Object userData; // Use to store application specific data.

	public float linearDamping;
	public float angularDamping;

	// Static, dynamic or kinematic. Dynamic bodies with zero mass are defaulted
	// to a mass of 1. Static bodies never move or integrate, and are very CPU
	// efficient. Static bodies have infinite mass. Kinematic bodies have
	// infinite mass, but *do* integrate and move around. Kinematic bodies do
	// not resolve any collisions.
	public Q3Body.BodyStatus bodyType;

	public boolean allowSleep; // Sleeping lets a body assume a non-moving state.
						// Greatly reduces CPU usage.
	public boolean awake; // Initial sleep state. True means awake.
	public boolean active; // A body can start out inactive and just sits in memory.
	public boolean lockAxisX; // Locked rotation on the x axis.
	public boolean lockAxisY; // Locked rotation on the y axis.
	public boolean lockAxisZ; // Locked rotation on the z axis.
}

package com.cornchipss.cosmos.physx.qu3e.dynamics.contact;

import com.cornchipss.cosmos.physx.qu3e.collision.Q3Box;
import com.cornchipss.cosmos.physx.qu3e.dynamics.Q3Body;

public class Q3ContactConstraint
{
	public void SolveCollision()
	{
		
	}

	public Q3Box A, B;
	public Q3Body bodyA, bodyB;

	public Q3ContactEdge edgeA;
	public Q3ContactEdge edgeB;
	public Q3ContactConstraint next;
	public Q3ContactConstraint prev;

	public float friction;
	public float restitution;

	public Q3Manifold manifold;

	public static enum Q3ContactConstraintTypes
	{		
		eColliding(0x00000001), // Set when contact collides during a step
		eWasColliding(0x00000002), // Set when two objects stop colliding
		eIsland(0x00000004); // For internal marking during island forming
		
		public final int code;
		
		Q3ContactConstraintTypes(int c)
		{
			this.code = c;
		}
	};

	public int m_flags;
}

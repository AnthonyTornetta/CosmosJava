package com.cornchipss.cosmos.physx.qu3e.dynamics.contact.solver;

import org.joml.Vector3f;

import com.cornchipss.cosmos.physx.qu3e.dynamics.Q3Island;
import com.cornchipss.cosmos.physx.qu3e.dynamics.Q3VelocityState;
import com.cornchipss.cosmos.physx.qu3e.dynamics.contact.Q3Contact;
import com.cornchipss.cosmos.physx.qu3e.dynamics.contact.Q3ContactConstraint;
import com.cornchipss.cosmos.physx.qu3e.memory.Q3Settings;
import com.cornchipss.cosmos.utils.Maths;

public class Q3ContactSolver
{
	public Q3Island island;
	public Q3ContactConstraintState[] contacts;
	public int contactCount;
	public Q3VelocityState[] velocities;

	public boolean enableFriction;
	
	public void Initialize(Q3Island island)
	{
		this.island = island;
		contactCount = island.contactCount;
		contacts = island.contactStates;
		velocities = island.velocities;
		enableFriction = island.enableFriction;
	}

	public void ShutDown()
	{
		for ( int i = 0; i < contactCount; ++i )
		{
			Q3ContactConstraintState c = contacts[i];
			Q3ContactConstraint cc = island.contacts[i];

			for ( int j = 0; j < c.contactCount; ++j )
			{
				Q3Contact oc = cc.manifold.contacts[j];
				Q3ContactState cs = c.contacts[j];
				oc.normalImpulse = cs.normalImpulse;
				oc.tangentImpulse[ 0 ] = cs.tangentImpulse[ 0 ];
				oc.tangentImpulse[ 1 ] = cs.tangentImpulse[ 1 ];
			}
		}
	}
	
	private static float q3Invert(float a)
	{
		return a != 0.0f ? 1.0f / a : 0.0f;
	}

	// TODO: Use MemoryPool
	public void PreSolve(float dt)
	{
		for ( int i = 0; i < contactCount; ++i )
		{
			Q3ContactConstraintState cs = contacts[i];

			Vector3f vA = velocities[ cs.indexA ].v;
			Vector3f wA = velocities[ cs.indexA ].w;
			Vector3f vB = velocities[ cs.indexB ].v;
			Vector3f wB = velocities[ cs.indexB ].w;

			for ( int j = 0; j < cs.contactCount; ++j )
			{
				Q3ContactState c = cs.contacts[j];

				// Precalculate JM^-1JT for contact and friction constraints
				Vector3f raCn = c.ra.cross(cs.normal, new Vector3f());
				Vector3f rbCn = c.rb.cross(cs.normal, new Vector3f());
				
				float nm = cs.mA + cs.mB;
				float tm[] = new float[2];
				tm[ 0 ] = nm;
				tm[ 1 ] = nm;

				nm += raCn.dot(cs.iA.transform(raCn, new Vector3f())) + rbCn.dot(cs.iB.transform(rbCn, new Vector3f()));
				c.normalMass = q3Invert( nm );

				for ( int k = 0; k < 2; ++k )
				{
					Vector3f raCt = cs.tangentVectors[ k ].cross(c.ra, new Vector3f());
					Vector3f rbCt = cs.tangentVectors[ k ].cross(c.rb, new Vector3f());
					
					tm[ k ] += raCt.dot(cs.iA.transform(raCt, new Vector3f())) + rbCt.dot(cs.iB.transform(rbCt, new Vector3f()));
					
					c.tangentMass[ k ] = q3Invert( tm[ k ] );
				}

				// Precalculate bias factor
				c.bias = -Q3Settings.BAUMGARTE * (1.0f / dt) * Math.min( 0.0f, c.penetration + Q3Settings.PENETRATION_SLOP );

				// Warm start contact
				Vector3f P = cs.normal.mul(c.normalImpulse, new Vector3f());

				if ( enableFriction )
				{
					P.add(cs.tangentVectors[ 0 ].mul(c.tangentImpulse[ 0 ], new Vector3f()));
					P.add(cs.tangentVectors[ 1 ].mul(c.tangentImpulse[ 1 ], new Vector3f()));
				}

				vA.sub(P.mul(cs.mA, new Vector3f()));
				wA.sub(cs.iA.transform(c.ra.cross(P, new Vector3f())));

				vB.add(P.mul(cs.mB, new Vector3f()));
				wB.add(cs.iB.transform(c.rb.cross(P, new Vector3f())));

				// Add in restitution bias
				float dv = vB.add(wB.cross(c.rb, new Vector3f())).sub(vA, new Vector3f()).sub(wA.cross(c.ra, new Vector3f())).dot(cs.normal);

				if ( dv < -1.0f )
					c.bias += -(cs.restitution) * dv;
			}

			velocities[ cs.indexA ].v = vA;
			velocities[ cs.indexA ].w = wA;
			velocities[ cs.indexB ].v = vB;
			velocities[ cs.indexB ].w = wB;
		}
	}

	public void Solve()
	{
		for ( int i = 0; i < contactCount; ++i )
		{
			Q3ContactConstraintState cs = contacts[i];

			Vector3f vA = velocities[ cs.indexA ].v;
			Vector3f wA = velocities[ cs.indexA ].w;
			Vector3f vB = velocities[ cs.indexB ].v;
			Vector3f wB = velocities[ cs.indexB ].w;

			for ( int j = 0; j < cs.contactCount; ++j )
			{
				Q3ContactState c = cs.contacts[j];

				// relative velocity at contact
				Vector3f dv = vB.add(wB.cross(c.rb).sub(vA, new Vector3f()).sub(wA.cross( c.ra, new Vector3f() ), new Vector3f()), new Vector3f());

				// Friction
				if ( enableFriction )
				{
					for ( int k = 0; k < 2; ++k )
					{
						float lambda = -dv.dot(cs.tangentVectors[ k ] ) * c.tangentMass[ k ];

						// Calculate frictional impulse
						float maxLambda = cs.friction * c.normalImpulse;

						// Clamp frictional impulse
						float oldPT = c.tangentImpulse[ k ];
						c.tangentImpulse[ k ] = Maths.clamp( oldPT + lambda, -maxLambda, maxLambda );
						lambda = c.tangentImpulse[ k ] - oldPT;

						// Apply friction impulse
						Vector3f impulse = cs.tangentVectors[ k ].mul(lambda, new Vector3f());
						vA.sub(impulse.mul(cs.mA, new Vector3f()));
						wA.sub(cs.iA.transform(c.ra.cross(impulse, new Vector3f()), new Vector3f()));

						vB.add(impulse.mul(cs.mB, new Vector3f()));
						wB.add(cs.iB.transform(c.rb.cross(impulse, new Vector3f()), new Vector3f()));
					}
				}

				// Normal
				{
					dv = vB.add(wB.cross(c.rb, new Vector3f()).sub(vA, new Vector3f()).sub(wA.cross(c.ra, new Vector3f()), new Vector3f()), new Vector3f());

					// Normal impulse
					float vn = dv.dot(cs.normal);

					// Factor in positional bias to calculate impulse scalar j
					float lambda = c.normalMass * (-vn + c.bias);

					// Clamp impulse
					float tempPN = c.normalImpulse;
					c.normalImpulse = Math.max( tempPN + lambda, 0.0f);
					lambda = c.normalImpulse - tempPN;

					// Apply impulse
					Vector3f impulse = cs.normal.mul(lambda, new Vector3f());
					vA.sub(impulse.mul(cs.mA, new Vector3f()));
					wA.sub(cs.iA.transform(c.ra.cross(impulse, new Vector3f()), new Vector3f()));

					vB.add(impulse.mul(cs.mB, new Vector3f()));
					wB.add(cs.iB.transform(c.rb.cross(impulse, new Vector3f()), new Vector3f()));
				}
			}

			velocities[ cs.indexA ].v = vA;
			velocities[ cs.indexA ].w = wA;
			velocities[ cs.indexB ].v = vB;
			velocities[ cs.indexB ].w = wB;
		}
	}
}

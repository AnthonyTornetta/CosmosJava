package com.cornchipss.cosmos.physx.qu3e.dynamics;

import org.joml.Vector3f;

import com.cornchipss.cosmos.physx.qu3e.broadphase.Q3BroadPhase;
import com.cornchipss.cosmos.physx.qu3e.collision.Q3Box;
import com.cornchipss.cosmos.physx.qu3e.dynamics.contact.Q3Contact;
import com.cornchipss.cosmos.physx.qu3e.dynamics.contact.Q3ContactConstraint;
import com.cornchipss.cosmos.physx.qu3e.dynamics.contact.Q3ContactEdge;
import com.cornchipss.cosmos.physx.qu3e.dynamics.contact.Q3Manifold;
import com.cornchipss.cosmos.physx.qu3e.geometry.Q3Math;
import com.cornchipss.cosmos.physx.qu3e.scene.Q3ContactListener;

public class Q3ContactManager 
{
	//private:
	public Q3ContactConstraint m_contactList;
	public int m_contactCount;
	public Q3BroadPhase m_broadphase;
	public Q3ContactListener m_contactListener;
	
	
// public:
	public Q3ContactManager()
	{
		this.m_broadphase = new Q3BroadPhase(this);
		
		m_contactList = null;
		m_contactCount = 0;
		m_contactListener = null;
	}

	// Add a new contact constraint for a pair of objects
	// unless the contact constraint already exists
	public void AddContact( Q3Box A, Q3Box B )
	{
		Q3Body bodyA = A.body;
		Q3Body bodyB = B.body;
		if ( !bodyA.CanCollide( bodyB ) )
			return;

		// Search for existing matching contact
		// Return if found duplicate to avoid duplicate constraints
		// Mark pre-existing duplicates as active
		Q3ContactEdge edge = A.body.m_contactList;
		while ( edge != null )
		{
			if ( edge.other == bodyB )
			{
				Q3Box shapeA = edge.constraint.A;
				Q3Box shapeB = edge.constraint.B;

				// @TODO: Verify this against Box2D; not sure if this is all we need here
				if( (A == shapeA) && (B == shapeB) )
					return;
			}

			edge = edge.next;
		}

		// Create new contact
		Q3ContactConstraint contact = new Q3ContactConstraint();
		contact.A = A;
		contact.B = B;
		contact.bodyA = A.body;
		contact.bodyB = B.body;
		contact.manifold.SetPair( A, B );
		contact.m_flags = 0;
		contact.friction = Q3Math.q3MixFriction( A, B );
		contact.restitution = Q3Math.q3MixRestitution( A, B );
		contact.manifold.contactCount = 0;

		for ( int i = 0; i < 8; ++i )
			contact.manifold.contacts[ i ].warmStarted = 0;

		contact.prev = null;
		contact.next = m_contactList;
		if ( m_contactList != null)
			m_contactList.prev = contact;
		m_contactList = contact;

		// Connect A
		contact.edgeA.constraint = contact;
		contact.edgeA.other = bodyB;

		contact.edgeA.prev = null;
		contact.edgeA.next = bodyA.m_contactList;
		if ( bodyA.m_contactList != null )
			bodyA.m_contactList.prev = contact.edgeA;
		bodyA.m_contactList = contact.edgeA;

		// Connect B
		contact.edgeB.constraint = contact;
		contact.edgeB.other = bodyA;

		contact.edgeB.prev = null;
		contact.edgeB.next = bodyB.m_contactList;
		if ( bodyB.m_contactList != null)
			bodyB.m_contactList.prev = contact.edgeB;
		bodyB.m_contactList = contact.edgeB;

		bodyA.SetToAwake( );
		bodyB.SetToAwake( );

		++m_contactCount;
	}

	// Has broadphase find all contacts and call AddContact on the
	// ContactManager for each pair found
	public void FindNewContacts( )
	{
		m_broadphase.UpdatePairs( );
	}

	// Remove a specific contact
	public void RemoveContact( Q3ContactConstraint contact )
	{
		Q3Body A = contact.bodyA;
		Q3Body B = contact.bodyB;

		// Remove from A
		if ( contact.edgeA.prev != null )
			contact.edgeA.prev.next = contact.edgeA.next;

		if ( contact.edgeA.next != null )
			contact.edgeA.next.prev = contact.edgeA.prev;

		if ( contact.edgeA == A.m_contactList )
			A.m_contactList = contact.edgeA.next;

		// Remove from B
		if ( contact.edgeB.prev != null )
			contact.edgeB.prev.next = contact.edgeB.next;

		if ( contact.edgeB.next != null )
			contact.edgeB.next.prev = contact.edgeB.prev;

		if ( contact.edgeB == B.m_contactList )
			B.m_contactList = contact.edgeB.next;

		A.SetToAwake( );
		B.SetToAwake( );

		// Remove contact from the manager
		if ( contact.prev != null )
			contact.prev.next = contact.next;

		if ( contact.next != null )
			contact.next.prev = contact.prev;

		if ( contact == m_contactList )
			m_contactList = contact.next;

		--m_contactCount;
	}

	// Remove all contacts from a body
	public void RemoveContactsFromBody( Q3Body body )
	{
		Q3ContactEdge edge = body.m_contactList;

		while( edge != null )
		{
			Q3ContactEdge next = edge.next;
			RemoveContact( edge.constraint );
			edge = next;
		}
	}
	
	public void RemoveFromBroadphase( Q3Body body )
	{
		Q3Box box = body.m_boxes;

		while ( box != null )
		{
			m_broadphase.RemoveBox( box );
			box = box.next;
		}
	}

	// Remove contacts without broadphase overlap
	// Solves contact manifolds
	public void TestCollisions( )
	{
		Q3ContactConstraint constraint = m_contactList;

		while( constraint != null)
		{
			Q3Box A = constraint.A;
			Q3Box B = constraint.B;
			Q3Body bodyA = A.body;
			Q3Body bodyB = B.body;

			constraint.m_flags &= ~Q3ContactConstraint.Q3ContactConstraintTypes.eIsland.code;

			if( !bodyA.IsAwake( ) && !bodyB.IsAwake( ) )
			{
				constraint = constraint.next;
				continue;
			}

			if ( !bodyA.CanCollide( bodyB ) )
			{
				Q3ContactConstraint next = constraint.next;
				RemoveContact( constraint );
				constraint = next;
				continue;
			}

			// Check if contact should persist
			if ( !m_broadphase.TestOverlap( A.broadPhaseIndex, B.broadPhaseIndex ) )
			{
				Q3ContactConstraint next = constraint.next;
				RemoveContact( constraint );
				constraint = next;
				continue;
			}
			Q3Manifold manifold = constraint.manifold;
			Q3Manifold oldManifold = constraint.manifold;
			Vector3f ot0 = oldManifold.tangentVectors[ 0 ];
			Vector3f ot1 = oldManifold.tangentVectors[ 1 ];
			constraint.SolveCollision( );
			Q3Math.q3ComputeBasis( manifold.normal, manifold.tangentVectors[0], manifold.tangentVectors[1] );

			for ( int i = 0; i < manifold.contactCount; ++i )
			{
				Q3Contact c = manifold.contacts[i];
				c.tangentImpulse[ 0 ] = c.tangentImpulse[ 1 ] = c.normalImpulse = 0.0f;
				char oldWarmStart = c.warmStarted;
				c.warmStarted = (char) 0 ;

				for ( int j = 0; j < oldManifold.contactCount; ++j )
				{
					Q3Contact oc = oldManifold.contacts[j];
					if ( c.fp.key() == oc.fp.key() )
					{
						c.normalImpulse = oc.normalImpulse;

						// Attempt to re-project old friction solutions
						Vector3f friction = ot0.mul(oc.tangentImpulse[ 0 ], new Vector3f()).add(ot1.mul(oc.tangentImpulse[ 1 ], new Vector3f()));
						
						c.tangentImpulse[ 0 ] = friction.dot(manifold.tangentVectors[ 0 ] );
						c.tangentImpulse[ 1 ] = friction.dot(manifold.tangentVectors[ 1 ] );
						c.warmStarted = (char)Math.max((int)oldWarmStart, ( oldWarmStart + 1 ) );
						break;
					}
				}
			}

			if ( m_contactListener != null )
			{
				int now_colliding = constraint.m_flags & Q3ContactConstraint.Q3ContactConstraintTypes.eColliding.code;
				int was_colliding = constraint.m_flags & Q3ContactConstraint.Q3ContactConstraintTypes.eWasColliding.code;

				if ( now_colliding != 0 && was_colliding == 0 )
					m_contactListener.beginContact( constraint );

				else if ( now_colliding == 0 && was_colliding != 0)
					m_contactListener.endContact( constraint );
			}

			constraint = constraint.next;
		}
	}
	
	public static void SolveCollision( Object param )
	{
		
	}

//	void RenderContacts( q3Render* debugDrawer )
	
}

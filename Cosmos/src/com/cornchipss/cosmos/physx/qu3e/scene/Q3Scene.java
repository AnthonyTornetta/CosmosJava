package com.cornchipss.cosmos.physx.qu3e.scene;

import org.joml.AABBf;
import org.joml.Vector3f;
import org.joml.Vector3fc;

import com.cornchipss.cosmos.physx.qu3e.collision.Q3Box;
import com.cornchipss.cosmos.physx.qu3e.dynamics.Q3Body;
import com.cornchipss.cosmos.physx.qu3e.dynamics.Q3BodyDef;
import com.cornchipss.cosmos.physx.qu3e.dynamics.Q3ContactManager;
import com.cornchipss.cosmos.physx.qu3e.dynamics.Q3Island;
import com.cornchipss.cosmos.physx.qu3e.dynamics.Q3VelocityState;
import com.cornchipss.cosmos.physx.qu3e.dynamics.contact.Q3ContactConstraint;
import com.cornchipss.cosmos.physx.qu3e.dynamics.contact.Q3ContactEdge;
import com.cornchipss.cosmos.physx.qu3e.dynamics.contact.solver.Q3ContactConstraintState;
import com.cornchipss.cosmos.physx.qu3e.geometry.Q3RaycastData;

public class Q3Scene
{
	public Q3ContactManager m_contactManager;

	private int m_bodyCount;
	private Q3Body m_bodyList;

	private Vector3f m_gravity;
	private float m_dt;
	private int m_iterations;

	public boolean m_newBox;
	public boolean m_allowSleep;
	public boolean m_enableFriction;

	/**
	 * 
	 * @param dt
	 * @param gravity
	 * @param iterations 0 for default (20)
	 */
	public Q3Scene(float dt, Vector3fc gravity, int iterations)
	{
		this.m_dt = dt;
		this.m_gravity = new Vector3f().set(gravity);
		this.m_iterations = iterations != 0 ? iterations : 20;

		m_contactManager = new Q3ContactManager();
		m_bodyCount = 0;
		m_bodyList = null;
		m_newBox = false;
		m_allowSleep = true;
		m_enableFriction = true;
	}

	// Run the simulation forward in time by dt (fixed timestep). Variable
	// timestep is not supported.
	public void Step()
	{
		if (m_newBox)
		{
			m_contactManager.m_broadphase.UpdatePairs();
			m_newBox = false;
		}

		m_contactManager.TestCollisions();

		for (Q3Body body = m_bodyList; body != null; body = body.m_next)
		{
			body.m_flags &= ~Q3Body.BodyStatus.eIsland.code;
		}

		Q3Island island = new Q3Island();
		island.bodyCapacity = m_bodyCount;
		island.contactCapacity = m_contactManager.m_contactCount;
		island.bodies = new Q3Body[m_bodyCount];
		island.velocities = new Q3VelocityState[m_bodyCount];

		for (int i = 0; i < island.velocities.length; i++)
			island.velocities[i] = new Q3VelocityState();

		island.contacts = new Q3ContactConstraint[island.contactCapacity];

		for (int i = 0; i < island.contacts.length; i++)
			island.contacts[i] = new Q3ContactConstraint();

		island.contactStates = new Q3ContactConstraintState[island.contactCapacity];

		for (int i = 0; i < island.contactStates.length; i++)
			island.contactStates[i] = new Q3ContactConstraintState();

		island.allowSleep = m_allowSleep;
		island.enableFriction = m_enableFriction;
		island.bodyCount = 0;
		island.contactCount = 0;
		island.dt = m_dt;
		island.gravity = m_gravity;
		island.iterations = m_iterations;

		// Build each active island and then solve each built island
		int stackSize = m_bodyCount;
		Q3Body[] stack = new Q3Body[stackSize];
		for (Q3Body seed = m_bodyList; seed != null; seed = seed.m_next)
		{
			// Seed cannot be apart of an island already
			if ((seed.m_flags & Q3Body.BodyStatus.eIsland.code) != 0)
				continue;

			// Seed must be awake
			if ((seed.m_flags & Q3Body.BodyStatus.eAwake.code) == 0)
				continue;

			// Seed cannot be a static body in order to keep islands
			// as small as possible
			if ((seed.m_flags & Q3Body.BodyStatus.StaticBody.code) != 0)
				continue;

			int stackCount = 0;
			stack[stackCount++] = seed;
			island.bodyCount = 0;
			island.contactCount = 0;

			// Mark seed as apart of island
			seed.m_flags |= Q3Body.BodyStatus.eIsland.code;

			// Perform DFS on constraint graph
			while (stackCount > 0)
			{
				// Decrement stack to implement iterative backtracking
				Q3Body body = stack[--stackCount];
				island.Add(body);

				// Awaken all bodies connected to the island
				body.SetToAwake();

				// Do not search across static bodies to keep island
				// formations as small as possible, however the static
				// body itself should be apart of the island in order
				// to properly represent a full contact
				if ((body.m_flags & Q3Body.BodyStatus.StaticBody.code) != 0)
					continue;

				// Search all contacts connected to this body
				Q3ContactEdge contacts = body.m_contactList;
				for (Q3ContactEdge edge = contacts; edge != null; edge = edge.next)
				{
					Q3ContactConstraint contact = edge.constraint;

					// Skip contacts that have been added to an island already
					if ((contact.m_flags & Q3ContactConstraint.Q3ContactConstraintTypes.eIsland.code) != 0)
						continue;

					// Can safely skip this contact if it didn't actually
					// collide with anything
					if ((contact.m_flags & Q3ContactConstraint.Q3ContactConstraintTypes.eColliding.code) == 0)
						continue;

					// Skip sensors
					if (contact.A.sensor || contact.B.sensor)
						continue;

					// Mark island flag and add to island
					contact.m_flags |= Q3ContactConstraint.Q3ContactConstraintTypes.eIsland.code;
					island.Add(contact);

					// Attempt to add the other body in the contact to the
					// island
					// to simulate contact awakening propogation
					Q3Body other = edge.other;
					if ((other.m_flags & Q3Body.BodyStatus.eIsland.code) != 0)
						continue;

					assert (stackCount < stackSize);

					stack[stackCount++] = other;
					other.m_flags |= Q3Body.BodyStatus.eIsland.code;
				}
			}

			assert (island.bodyCount != 0);

			island.Initialize();
			island.Solve();

			// Reset all static island flags
			// This allows static bodies to participate in other island
			// formations
			for (int i = 0; i < island.bodyCount; i++)
			{
				Q3Body body = island.bodies[i];

				if ((body.m_flags & Q3Body.BodyStatus.StaticBody.code) != 0)
					body.m_flags &= ~Q3Body.BodyStatus.eIsland.code;
			}
		}

//		m_stack.Free( stack );
//		m_stack.Free( island.m_contactStates );
//		m_stack.Free( island.m_contacts );
//		m_stack.Free( island.m_velocities );
//		m_stack.Free( island.m_bodies );

		// Update the broadphase AABBs
		for (Q3Body body = m_bodyList; body != null; body = body.m_next)
		{
			if ((body.m_flags & Q3Body.BodyStatus.StaticBody.code) != 0)
				continue;

			body.SynchronizeProxies();
		}

		// Look for new contacts
		m_contactManager.FindNewContacts();

		// Clear all forces
		for (Q3Body body = m_bodyList; body != null; body = body.m_next)
		{
			body.m_force.zero();
			body.m_torque.zero();
		}
	}

	// Construct a new rigid body. The BodyDef can be reused at the user's
	// discretion, as no reference to the BodyDef is kept.
	public Q3Body CreateBody(Q3BodyDef def)
	{
		Q3Body body = new Q3Body(def, this);

		// Add body to scene bodyList
		body.m_prev = null;
		body.m_next = m_bodyList;

		if (m_bodyList != null)
			m_bodyList.m_prev = body;

		m_bodyList = body;
		++m_bodyCount;

		return body;
	}

	// Frees a body, removes all shapes associated with the body and frees
	// all shapes and contacts associated and attached to this body.
	public void RemoveBody(Q3Body body)
	{
		assert (m_bodyCount > 0);

		m_contactManager.RemoveContactsFromBody(body);

		body.RemoveAllBoxes();

		// Remove body from scene bodyList
		if (body.m_next != null)
			body.m_next.m_prev = body.m_prev;

		if (body.m_prev != null)
			body.m_prev.m_next = body.m_next;

		if (body == m_bodyList)
			m_bodyList = body.m_next;

		--m_bodyCount;
	}

	public void RemoveAllBodies()
	{
		Q3Body body = m_bodyList;

		while (body != null)
		{
			Q3Body next = body.m_next;

			body.RemoveAllBoxes();

			body = next;
		}

		m_bodyList = null;
	}

	// Enables or disables rigid body sleeping. Sleeping is an effective CPU
	// optimization where bodies are put to sleep if they don't move much.
	// Sleeping bodies sit in memory without being updated, until the are
	// touched by something that wakes them up. The default is enabled.
	public void SetAllowSleep(boolean allowSleep)
	{
		m_allowSleep = allowSleep;

		if (!allowSleep)
		{
			for (Q3Body body = m_bodyList; body != null; body = body.m_next)
				body.SetToAwake();
		}
	}

	// Increasing the iteration count increases the CPU cost of simulating
	// Scene.Step(). Decreasing the iterations makes the simulation less
	// realistic (convergent). A good iteration number range is 5 to 20.
	// Only positive numbers are accepted. Non-positive and negative
	// inputs set the iteration count to 1.
	// 0 Sets it to the default value (20)
	public void SetIterations(int iterations)
	{
		if (iterations == 0)
			iterations = 20;

		m_iterations = Math.max(1, iterations);
	}

	// Friction occurs when two rigid bodies have shapes that slide along one
	// another. The friction force resists this sliding motion.
	public void SetEnableFriction(boolean enabled)
	{
		m_enableFriction = enabled;
	}

	// Render the scene with an interpolated time between the last frame and
	// the current simulation step.
//	void Render( q3Render* render ) const;

	// Gets and sets the global gravity vector used during integration
	public Vector3fc GetGravity()
	{
		return m_gravity;
	}

	public void SetGravity(Vector3fc gravity)
	{
		m_gravity.set(gravity);
	}

	// Removes all bodies from the scene.
	public void Shutdown()
	{
		RemoveAllBodies();
	}

	// Sets the listener to report collision start/end. Provides the user
	// with a pointer to an q3ContactConstraint. The q3ContactConstraint
	// holds pointers to the two shapes involved in a collision, and the
	// two bodies connected to each shape. The q3ContactListener will be
	// called very often, so it is recommended for the funciton to be very
	// efficient. Provide a NULL pointer to remove the previously set
	// listener.
	public void SetContactListener(Q3ContactListener listener)
	{
		m_contactManager.m_contactListener = listener;
	}

	// Query the world to find any shapes that can potentially intersect
	// the provided AABB. This works by querying the broadphase with an
	// AAABB -- only *potential* intersections are reported. Perhaps the
	// user might use lmDistance as fine-grained collision detection.
	public void QueryAABB(Q3QueryCallback cb, AABBf m_aabb)
	{
		m_contactManager.m_broadphase.m_tree.Query((id) ->
		{
			AABBf aabb = new AABBf();
			Q3Box box = (Q3Box) m_contactManager.m_broadphase.m_tree.GetUserData(id);

			box.ComputeAABB(box.body.GetTransform(), aabb);

			if (m_aabb.testAABB(aabb))
			{
				return cb.reportShape(box);
			}

			return true;
		}, m_aabb);
	}

	// Query the world to find any shapes intersecting a world space point.
	public void QueryPoint(Q3QueryCallback cb, Vector3fc point)
	{
		AABBf aabb = new AABBf(point.x() - 0.5f, point.y() - 0.5f, point.z() - 0.5f, point.x() + 0.5f, point.y() + 0.5f,
			point.z() + 0.5f);

		m_contactManager.m_broadphase.m_tree.Query((id) ->
		{
			Q3Box box = (Q3Box) m_contactManager.m_broadphase.m_tree.GetUserData(id);

			if (box.TestPoint(box.body.GetTransform(), point))
			{
				cb.reportShape(box);
			}

			return true;
		}, aabb);
	}

	// Query the world to find any shapes intersecting a ray.
	public void RayCast(Q3QueryCallback cb, Q3RaycastData rayCast)
	{
		m_contactManager.m_broadphase.m_tree.Query((id) ->
		{
			Q3Box box = (Q3Box) m_contactManager.m_broadphase.m_tree.GetUserData(id);

			if (box.Raycast(box.body.GetTransform(), rayCast))
			{
				return cb.reportShape(box);
			}

			return true;
		}, rayCast);
	}

	// Dump all rigid bodies and shapes into a log file. The log can be
	// used as C++ code to re-create an initial scene setup. Contacts
	// are *not* logged, meaning any cached resolution solutions will
	// not be saved to the log file. This means the log file will be most
	// accurate when dumped upon scene initialization, instead of mid-
	// simulation.
//	void Dump( FILE* file ) const;
}

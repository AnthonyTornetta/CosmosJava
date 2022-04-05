package com.cornchipss.cosmos.physx.qu3e.dynamics;

import org.joml.Matrix3f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import com.cornchipss.cosmos.physx.qu3e.dynamics.contact.Q3Contact;
import com.cornchipss.cosmos.physx.qu3e.dynamics.contact.Q3ContactConstraint;
import com.cornchipss.cosmos.physx.qu3e.dynamics.contact.solver.Q3ContactConstraintState;
import com.cornchipss.cosmos.physx.qu3e.dynamics.contact.solver.Q3ContactSolver;
import com.cornchipss.cosmos.physx.qu3e.dynamics.contact.solver.Q3ContactState;
import com.cornchipss.cosmos.physx.qu3e.geometry.Q3Math;
import com.cornchipss.cosmos.physx.qu3e.memory.Q3Settings;
import com.cornchipss.cosmos.utils.Utils;

public class Q3Island
{
	public Q3Body[] bodies;
	public Q3VelocityState[] velocities;
	public int bodyCapacity;
	public int bodyCount;

	public Q3ContactConstraint[] contacts;
	public Q3ContactConstraintState[] contactStates;

	public int contactCount;
	public int contactCapacity;

	public float dt;
	public Vector3f gravity;
	public int iterations;

	public boolean allowSleep;
	public boolean enableFriction;
	
	public void Solve()
	{
		// Apply gravity
		// Integrate velocities and create state buffers, calculate world
		// inertia
		for (int i = 0; i < bodyCount; ++i)
		{
			Q3Body body = bodies[i];
			Q3VelocityState v = velocities[i];
			
			if ((body.m_flags & Q3Body.BodyStatus.eDynamic.code) != 0)
			{
				body.ApplyLinearForce(
					gravity.mul(body.m_gravityScale, new Vector3f()));

				// Calculate world space intertia tensor
				Matrix3f r = body.transform().orientation().asMatrix3f();
				body.m_invInertiaWorld = r
					.mul(body.m_invInertiaModel, new Matrix3f())
					.mul(r.transpose(new Matrix3f()));

				// Integrate velocity
				body.rb.velocity(body.m_force.mul(body.m_invMass, new Vector3f().add(body.GetLinearVelocity()).mul(dt)));
				
				body.rb.angularVelocity(Q3Math.mul(body.m_invInertiaWorld, body.m_torque).add(body.GetAngularVelocity()).mul(dt));

				// From Box2D!
				// Apply damping.
				// ODE: dv/dt + c * v = 0
				// Solution: v(t) = v0 * exp(-c * t)
				// Time step: v(t + dt) = v0 * exp(-c * (t + dt)) = v0 * exp(-c
				// * t) * exp(-c * dt) = v * exp(-c * dt)
				// v2 = exp(-c * dt) * v1
				// Pade approximation:
				// v2 = v1 * 1 / (1 + c * dt)
				body.rb.velocity(body.GetLinearVelocity()
					.mul((1.0f) / ((1.0f) + dt * body.m_linearDamping), new Vector3f()));
				
				body.rb.velocity(body.GetAngularVelocity()
					.mul((1.0f) / ((1.0f) + dt * body.m_angularDamping), new Vector3f()));
			}

			v.v = new Vector3f(body.GetLinearVelocity());
			v.w = new Vector3f(body.GetAngularVelocity());
		}

		// Create contact solver, pass in state buffers, create buffers for
		// contacts
		// Initialize velocity constraint for normal + friction and warm start
		Q3ContactSolver contactSolver = new Q3ContactSolver();
		contactSolver.Initialize(this);
		contactSolver.PreSolve(dt);

		// Solve contacts
		for (int i = 0; i < iterations; ++i)
			contactSolver.Solve();

		contactSolver.ShutDown();

		// Copy back state buffers
		// Integrate positions
		for (int i = 0; i < bodyCount; ++i)
		{
			Q3Body body = bodies[i];
			Q3VelocityState v = velocities[i];

			if ((body.m_flags & Q3Body.BodyStatus.eStatic.code) != 0)
				continue;

			body.rb.velocity(v.v);
			body.rb.angularVelocity(v.w);

			// Integrate position
			body.GetTransform().position(body.GetLinearVelocity()
				.mul(dt, new Vector3f()).add(body.GetTransform().position()));
			Quaternionf res = body.GetTransform().orientation().quaternion()
				.integrate(body.GetAngularVelocity().x() * dt,
					body.GetAngularVelocity().y() * dt,
					body.GetAngularVelocity().z() * dt, 0.0f, new Quaternionf());
			body.GetTransform().orientation().quaternion(res.normalize());
		}

		if (allowSleep)
		{
			// Find minimum sleep time of the entire island
			float minSleepTime = Float.MAX_VALUE;
			for (int i = 0; i < bodyCount; ++i)
			{
				Q3Body body = bodies[i];

				if ((body.m_flags & Q3Body.BodyStatus.eStatic.code) != 0)
					continue;

				final float sqrLinVel = body.GetLinearVelocity()
					.dot(body.GetLinearVelocity());
				final float cbAngVel = body.GetAngularVelocity()
					.dot(body.GetAngularVelocity());
				final float linTol = Q3Settings.SLEEP_LINEAR;
				final float angTol = Q3Settings.SLEEP_ANGULAR;

				if (sqrLinVel > linTol || cbAngVel > angTol)
				{
					minSleepTime = (0.0f);
					body.m_sleepTime = (0.0f);
				}

				else
				{
					body.m_sleepTime += dt;
					minSleepTime = Math.min(minSleepTime, body.m_sleepTime);
				}
			}

			// Put entire island to sleep so long as the minimum found sleep
			// time
			// is below the threshold. If the minimum sleep time reaches below
			// the
			// sleeping threshold, the entire island will be reformed next step
			// and sleep test will be tried again.
			if (minSleepTime > Q3Settings.SLEEP_TIME)
			{
				for (int i = 0; i < bodyCount; ++i)
					bodies[i].SetToSleep();
			}
		}
	}

	public void Add(Q3Body body)
	{
		assert (bodyCount < bodyCapacity);

		body.m_islandIndex = bodyCount;

		bodies[bodyCount++] = body;
	}

	public void Add(Q3ContactConstraint contact)
	{
		assert (contactCount < contactCapacity);

		contacts[contactCount++] = contact;
	}

	public void Initialize()
	{
		for (int i = 0; i < contactCount; ++i)
		{
			Q3ContactConstraint cc = contacts[i];

			Q3ContactConstraintState c = contactStates[i];

			c.centerA = new Vector3f().set(cc.bodyA.GetTransform().position());
			c.centerB = new Vector3f().set(cc.bodyB.GetTransform().position());
			c.iA = cc.bodyA.m_invInertiaWorld;
			c.iB = cc.bodyB.m_invInertiaWorld;
			c.mA = cc.bodyA.m_invMass;
			c.mB = cc.bodyB.m_invMass;
			c.restitution = cc.restitution;
			c.friction = cc.friction;
			c.indexA = cc.bodyA.m_islandIndex;
			c.indexB = cc.bodyB.m_islandIndex;
			c.normal = cc.manifold.normal;
			c.tangentVectors[0] = cc.manifold.tangentVectors[0];
			c.tangentVectors[1] = cc.manifold.tangentVectors[1];
			c.contactCount = cc.manifold.contactCount;

			for (int j = 0; j < c.contactCount; ++j)
			{
				Q3ContactState s = c.contacts[j];
				Q3Contact cp = cc.manifold.contacts[j];
				s.ra = cp.position.sub(c.centerA, new Vector3f());
				s.rb = cp.position.sub(c.centerB, new Vector3f());
				s.penetration = cp.penetration;
				s.normalImpulse = cp.normalImpulse;
				s.tangentImpulse[0] = cp.tangentImpulse[0];
				s.tangentImpulse[1] = cp.tangentImpulse[1];
			}
		}
	}
}

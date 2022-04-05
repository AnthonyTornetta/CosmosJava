package com.cornchipss.cosmos.physx.qu3e.dynamics;

import org.joml.AABBf;
import org.joml.AxisAngle4f;
import org.joml.Matrix3f;
import org.joml.Quaternionf;
import org.joml.Quaternionfc;
import org.joml.Vector3f;
import org.joml.Vector3fc;

import com.cornchipss.cosmos.memory.MemoryPool;
import com.cornchipss.cosmos.physx.Orientation;
import com.cornchipss.cosmos.physx.RigidBody;
import com.cornchipss.cosmos.physx.Transform;
import com.cornchipss.cosmos.physx.qu3e.collision.Q3Box;
import com.cornchipss.cosmos.physx.qu3e.collision.Q3BoxDef;
import com.cornchipss.cosmos.physx.qu3e.dynamics.contact.Q3ContactConstraint;
import com.cornchipss.cosmos.physx.qu3e.dynamics.contact.Q3ContactEdge;
import com.cornchipss.cosmos.physx.qu3e.scene.Q3Scene;

public class Q3Body
{
	public static int nextBodyStatusCode = 1;

	// m_flags
	public static enum BodyStatus
	{
		eAwake(),  //0b1
		eActive(),
		eAllowSleep(),
		eIsland(),
		eStatic(),
		eDynamic(),
		eKinematic(),
		eLockAxisX(),
		eLockAxisY(),
		eLockAxisZ(),
		StaticBody(),
		DynamicBody(),
		KinematicBody();

		public final int code;

		BodyStatus()
		{
			this.code = nextBodyStatusCode;

			nextBodyStatusCode <<= 1;
		};
	};

	public Matrix3f m_invInertiaModel;
	public Matrix3f m_invInertiaWorld;
	public float m_mass;
	public float m_invMass;
//	public Vector3f m_linearVelocity;
//	public Vector3f m_angularVelocity;
	public Vector3f m_force;
	public Vector3f m_torque;
	
	public RigidBody rb;
	
//	public Transform transform();
	public Vector3f m_localCenter;
	public float m_sleepTime;
	public float m_gravityScale;
	public int m_layers;
	public int m_flags;

	public Q3Box m_boxes;
	public Object m_userData;
	public Q3Scene m_scene;
	public Q3Body m_next;
	public Q3Body m_prev;
	public int m_islandIndex;

	public float m_linearDamping;
	public float m_angularDamping;

	public Q3ContactEdge m_contactList;

	public Q3Body(Q3BodyDef def, Q3Scene scene)
	{
		rb = new RigidBody(new Transform(def.position, def.rotation));
		rb.velocity(def.linearVelocity);
		rb.angularVelocity(def.angularVelocity);
		m_force = new Vector3f();
		m_torque = new Vector3f();
		
		m_sleepTime = 0.0f;
		m_gravityScale = def.gravityScale;
		m_layers = def.layers;
		m_userData = def.userData;
		m_scene = scene;
		m_flags = 0;
		m_linearDamping = def.linearDamping;
		m_angularDamping = def.angularDamping;

		if (def.bodyType == BodyStatus.DynamicBody)
			m_flags |= BodyStatus.DynamicBody.code;

		else
		{
			if (def.bodyType == BodyStatus.StaticBody)
			{
				m_flags |= BodyStatus.StaticBody.code;
				rb.velocity(new Vector3f(0, 0, 0));
				rb.angularVelocity(new Vector3f(0, 0, 0));
				m_force.zero();
				m_torque.zero();
			}

			else if (def.bodyType == BodyStatus.KinematicBody)
				m_flags |= BodyStatus.KinematicBody.code;
			else
				throw new IllegalArgumentException(
					"NO BODY TYPE GIVEN OR INVALID ONE GIVEN");
		}

		if (def.allowSleep)
			m_flags |= BodyStatus.eAllowSleep.code;

		if (def.awake)
			m_flags |= BodyStatus.eAwake.code;

		if (def.active)
			m_flags |= BodyStatus.eActive.code;

		if (def.lockAxisX)
			m_flags |= BodyStatus.eLockAxisX.code;

		if (def.lockAxisY)
			m_flags |= BodyStatus.eLockAxisY.code;

		if (def.lockAxisZ)
			m_flags |= BodyStatus.eLockAxisZ.code;

		m_boxes = null;
		m_contactList = null;
	}
	
	public static Matrix3f mul(Matrix3f m, float s)
	{
		m.m00 *= s;
		m.m01 *= s;
		m.m02 *= s;

		m.m10 *= s;
		m.m11 *= s;
		m.m12 *= s;

		m.m20 *= s;
		m.m21 *= s;
		m.m22 *= s;

		return m;
	}

	public static Matrix3f q3OuterProduct(Vector3fc u, Vector3fc v)
	{
		Vector3f a = v.mul(u.x(), new Vector3f());
		Vector3f b = v.mul(u.y(), new Vector3f());
		Vector3f c = v.mul(u.z(), new Vector3f());

		return new Matrix3f(
			a.x, a.y, a.z,
			b.x, b.y, b.z,
			c.x, c.y, c.z);
	}
	
	public void CalculateMassData()
	{
		Matrix3f inertia = new Matrix3f();
		m_invInertiaModel = new Matrix3f();
		m_invInertiaWorld = new Matrix3f();
		
		m_invMass = 0.0f;
		m_mass = 0.0f;
		float mass = 0.0f;

		if ( (m_flags & BodyStatus.eStatic.code) != 0 || (m_flags & BodyStatus.eKinematic.code) != 0 )
		{
			m_localCenter.zero();
			return;
		}

		Vector3f lc = new Vector3f();

		for ( Q3Box box = m_boxes; box != null; box = box.next)
		{
			if ( box.density == 0.0f )
				continue;

			Q3MassData md = new Q3MassData();
			box.ComputeMass( md );
			mass += md.mass;
			inertia.add(md.inertia);
			lc.add(md.center.mul(md.mass, new Vector3f()));
		}

		if ( mass > 0.0f )
		{
			m_mass = mass;
			m_invMass = ( 1.0f ) / mass;
			lc.mul(m_invMass);
			Matrix3f identity = new Matrix3f().identity();
			
			inertia.sub(mul(identity, lc.dot(lc)).sub(mul(q3OuterProduct( lc, lc ), mass)));
			m_invInertiaModel = inertia.invert(new Matrix3f());

			if ( (m_flags & BodyStatus.eLockAxisX.code) != 0 )
			{
				m_invInertiaModel.m00 = 0;
				m_invInertiaModel.m01 = 0;
				m_invInertiaModel.m02 = 0;
			}

			if ( (m_flags & BodyStatus.eLockAxisY.code) != 0 )
			{
				m_invInertiaModel.m10 = 0;
				m_invInertiaModel.m11 = 0;
				m_invInertiaModel.m12 = 0;
			}

			if ( (m_flags & BodyStatus.eLockAxisZ.code) != 0 )
			{
				m_invInertiaModel.m20 = 0;
				m_invInertiaModel.m21 = 0;
				m_invInertiaModel.m22 = 0;
			}
		}
		else
		{
			// Force all dynamic bodies to have some mass
			m_invMass = 1.0f;
			m_invInertiaModel = new Matrix3f();
			m_invInertiaWorld = new Matrix3f();
		}

		m_localCenter = lc;
		transform().position(transform().q3Mul( lc ));
	}
	
	public Transform transform()
	{
		return rb.transform();
	}

	public void SynchronizeProxies()
	{

	}

	// public:
	// Adds a box to this body. Boxes are all defined in local space
	// of their owning body. Boxes cannot be defined relative to one
	// another. The body will recalculate its mass values. No contacts
	// will be created until the next q3Scene::Step( ) call.
	public Q3Box AddBox(Q3BoxDef def)
	{
		AABBf aabb = new AABBf();
		Q3Box box = new Q3Box();
		box.local = def.tx();
		box.extents = new Vector3f().set(def.extents());
		box.next = m_boxes;
		m_boxes = box;
		box.ComputeAABB(transform(), aabb);

		box.body = this;
		box.friction = def.friction();
		box.restitution = def.restitution();
		box.density = def.density();
		box.sensor = def.sensor();

		CalculateMassData();

		m_scene.m_contactManager.m_broadphase.InsertBox(box, aabb);
		m_scene.m_newBox = true;

		return box;
	}

	// Removes this box from the body and broadphase. Forces the body
	// to recompute its mass if the body is dynamic. Frees the memory
	// pointed to by the box pointer.
	public void RemoveBox(Q3Box box)
	{
		assert (box != null);
		assert (box.body == this);

		Q3Box node = m_boxes;

		boolean found = false;
		if (node == box)
		{
			m_boxes = node.next;
			found = true;
		}

		else
		{
			while (node != null)
			{
				if (node.next == box)
				{
					node.next = box.next;
					found = true;
					break;
				}

				node = node.next;
			}
		}

		// This shape was not connected to this body.
		assert (found);

		// Remove all contacts associated with this shape
		Q3ContactEdge edge = m_contactList;
		while (edge != null)
		{
			Q3ContactConstraint contact = edge.constraint;
			edge = edge.next;

			Q3Box A = contact.A;
			Q3Box B = contact.B;

			if (box == A || box == B)
				m_scene.m_contactManager.RemoveContact(contact);
		}

		m_scene.m_contactManager.m_broadphase.RemoveBox(box);

		CalculateMassData();
	}

	// Removes all boxes from this body and the broadphase.
	public void RemoveAllBoxes()
	{
		while (m_boxes != null)
		{
			Q3Box next = m_boxes.next;

			m_scene.m_contactManager.m_broadphase.RemoveBox(m_boxes);

			m_boxes = next;
		}

		m_scene.m_contactManager.RemoveContactsFromBody(this);
	}

	public void ApplyLinearForce(Vector3fc force)
	{
		m_force.x += force.x() * m_mass;
		m_force.y += force.y() * m_mass;
		m_force.z += force.z() * m_mass;

		SetToAwake();
	}

	public void ApplyForceAtWorldPoint(Vector3fc force, Vector3fc point)
	{
		m_force.x += force.x() * m_mass;
		m_force.y += force.y() * m_mass;
		m_force.z += force.z() * m_mass;

		Vector3f cross = MemoryPool.getInstanceOrCreate(Vector3f.class);

		point.sub(transform().position(), cross).cross(force, cross);

		m_torque.add(cross);

		MemoryPool.addToPool(cross);

		SetToAwake();
	}

	public void ApplyLinearImpulse(Vector3fc impulse)
	{
		Vector3f m_linearVelocity = new Vector3f(rb.velocity());
		m_linearVelocity.x += impulse.x() * m_invMass;
		m_linearVelocity.y += impulse.y() * m_invMass;
		m_linearVelocity.z += impulse.z() * m_invMass;

		rb.velocity(m_linearVelocity);
		
		SetToAwake();
	}

	public void ApplyLinearImpulseAtWorldPoint(Vector3fc impulse, Vector3fc point)
	{
		Vector3f m_linearVelocity = new Vector3f(rb.velocity());

		m_linearVelocity.x += impulse.x() * m_invMass;
		m_linearVelocity.y += impulse.y() * m_invMass;
		m_linearVelocity.z += impulse.z() * m_invMass;

		Vector3f cross = MemoryPool.getInstanceOrCreate(Vector3f.class);

		Vector3f m_angularVelocity = new Vector3f(rb.angularVelocity());

		m_angularVelocity.add(m_invInertiaWorld.transform(
			point.sub(transform().position(), cross).cross(impulse, cross), cross));

		rb.velocity(m_linearVelocity);
		rb.angularVelocity(m_angularVelocity);
		
		MemoryPool.addToPool(cross);

		SetToAwake();
	}

	public void ApplyTorque(Vector3fc torque)
	{
		m_torque.add(torque);
	}

	public void SetToAwake()
	{
		if ((m_flags & BodyStatus.eAwake.code) == 0)
		{
			m_flags |= BodyStatus.eAwake.code;
			m_sleepTime = 0.0f;
		}
	}

	public void SetToSleep()
	{
		m_flags &= ~BodyStatus.eAwake.code;
		m_sleepTime = 0.0f;
		rb.velocity(new Vector3f());
		rb.angularVelocity(new Vector3f());
//		m_linearVelocity.zero();
//		m_angularVelocity.zero();
		m_force.zero();
		m_torque.zero();
	}

	public boolean IsAwake()
	{
		return (m_flags & BodyStatus.eAwake.code) != 0;
	}

	public float GetGravityScale()
	{
		return m_gravityScale;
	}

	public void SetGravityScale(float scale)
	{
		this.m_gravityScale = scale;
	}

	public Vector3fc GetLocalPoint(Vector3fc p)
	{
		Vector3f ret = p.sub(transform().position(), new Vector3f());

		return transform().orientation().applyInverseRotation(ret, ret);
	}

	public Vector3fc GetLocalVector(Vector3fc v)
	{
		return transform().orientation().applyInverseRotation(v, new Vector3f());
	}

	public Vector3fc GetWorldPoint(Vector3fc p)
	{
		return transform().orientation().applyRotation(p, new Vector3f())
			.add(transform().position());
	}

	public Vector3fc GetWorldVector(Vector3fc v)
	{
		return transform().orientation().applyRotation(v, new Vector3f());
	}

	public Vector3fc GetLinearVelocity()
	{
		return rb.velocity();
	}

	public Vector3fc GetVelocityAtWorldPoint(Vector3fc p)
	{
		Vector3f dirToPoint = p.sub(transform().position(), new Vector3f());
		Vector3f relativeAngularVel = rb.angularVelocity().cross(dirToPoint,
			new Vector3f());

		return rb.velocity().add(relativeAngularVel, relativeAngularVel);
	}

	public void SetLinearVelocity(Vector3fc v)
	{
		assert ((m_flags & BodyStatus.StaticBody.code) == 0);

		if (v.dot(v) > 0)
		{
			SetToAwake();
		}

		rb.velocity(v);
	}

	public Vector3fc GetAngularVelocity()
	{
		return rb.angularVelocity();
	}

	public void SetAngularVelocity(Vector3fc v)
	{
		assert ((m_flags & BodyStatus.StaticBody.code) == 0);

		if (v.dot(v) > 0)
		{
			SetToAwake();
		}

		rb.angularVelocity(v);
	}

	public boolean CanCollide(Q3Body other)
	{
		if (this.equals(other))
			return false;

		if ((m_flags & BodyStatus.DynamicBody.code) != 0
			&& (other.m_flags & BodyStatus.DynamicBody.code) != 0)
			return false;

		if ((m_layers & other.m_layers) == 0)
			return false;

		return true;
	}

	public Transform GetTransform()
	{
		return this.transform();
	}

	public int GetFlags()
	{
		return m_flags;
	}

	public void SetLayers(int layers)
	{
		m_layers = layers;
	}

	public int GetLayers()
	{
		return m_layers;
	}

	public Quaternionfc GetQuaternion()
	{
		return transform().orientation().quaternion();
	}

	public Orientation GetOrientation()
	{
		return transform().orientation();
	}

	public Object GetUserData()
	{
		return m_userData;
	}

	public void SetLinearDamping(float damping)
	{
		m_linearDamping = damping;
	}

	public float GetLinearDamping(float damping)
	{
		return m_linearDamping;
	}

	public void SetAngularDamping(float damping)
	{
		m_angularDamping = damping;
	}

	public float GetAngularDamping(float damping)
	{
		return m_angularDamping;
	}

	// Manipulating the transformation of a body manually will result in
	// non-physical behavior. Contacts are updated upon the next call to
	// q3Scene::Step( ). Parameters are in world space. All body types
	// can be updated.
	public void SetTransform(Vector3fc position)
	{
		transform().position(position);
	}

	public void SetTransform(Vector3fc position, Vector3fc axis, float angle)
	{
		SetTransform(position);

		transform().orientation(
			new Orientation(new Quaternionf(new AxisAngle4f(angle, axis))));
		
		SynchronizeProxies();
	}

	// Used for debug rendering lines, triangles and basic lighting
//	void Render(q3Render render)
//	{
//	}
//
//	// Dump this rigid body and its shapes into a log file. The log can be
//	// used as C++ code to re-create an initial scene setup.
//	void Dump(FILE file, int index)
//	{
//	}

	public float GetMass()
	{
		return m_mass;
	}

	public float GetInvMass()
	{
		return m_invMass;
	}
}

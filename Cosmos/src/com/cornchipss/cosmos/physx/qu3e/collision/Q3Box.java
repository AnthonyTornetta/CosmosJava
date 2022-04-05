package com.cornchipss.cosmos.physx.qu3e.collision;

import org.joml.AABBf;
import org.joml.Matrix3f;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.joml.Vector3fc;

import com.cornchipss.cosmos.physx.Transform;
import com.cornchipss.cosmos.physx.qu3e.dynamics.Q3Body;
import com.cornchipss.cosmos.physx.qu3e.dynamics.Q3MassData;
import com.cornchipss.cosmos.physx.qu3e.geometry.Q3RaycastData;

public class Q3Box
{
	public static class MassData
	{
		Matrix3f inertia;
		Vector3f center;
		float mass;
	}

	public Transform local;
	public Vector3f extents;

	public Q3Box next;

	public Q3Body body;

	public float friction, restitution, density;
	public int broadPhaseIndex;

	public float friction()
	{
		return friction;
	}

	public float restitution()
	{
		return restitution;
	}

	public float density()
	{
		return density;
	}

	// MUTABLE //
	public Object userData;
	public boolean sensor;

	// --------------------------------------------------------------------------------------------------
	public Object userData()
	{
		return userData;
	}

	public void userData(Object o)
	{
		this.userData = o;
	}

	private Vector3f q3Min(Vector3f a, Vector3f b)
	{
		return a.set(Math.min(a.x, b.x),
			Math.min(a.y, b.y),
			Math.min(a.z, b.z));
	}

	private Vector3f q3Max(Vector3f a, Vector3f b)
	{
		return a.set(Math.max(a.x, b.x),
			Math.max(a.y, b.y),
			Math.max(a.z, b.z));
	}

	public void ComputeAABB(Transform tx, AABBf aabb)
	{
		Transform world = new Transform(
			tx.orientation().quaternion()
				.transform(local.position(), new Vector3f()).add(tx.position()),
			tx.orientation().quaternion().mul(local.orientation().quaternion(),
				new Quaternionf()));

		Vector3f[] v = {
			new Vector3f(-extents.x, -extents.y, -extents.z),
			new Vector3f(-extents.x, -extents.y, extents.z),
			new Vector3f(-extents.x, extents.y, -extents.z),
			new Vector3f(-extents.x, extents.y, extents.z),
			new Vector3f(extents.x, -extents.y, -extents.z),
			new Vector3f(extents.x, -extents.y, extents.z),
			new Vector3f(extents.x, extents.y, -extents.z),
			new Vector3f(extents.x, extents.y, extents.z)
		};

		for (int i = 0; i < 8; ++i)
			v[i] = world.q3Mul(v[i]);

		Vector3f min = new Vector3f(Float.MAX_VALUE, Float.MAX_VALUE,
			Float.MAX_VALUE);
		Vector3f max = new Vector3f(Float.MIN_VALUE, Float.MIN_VALUE,
			Float.MIN_VALUE);

		for (int i = 0; i < 8; ++i)
		{
			min = q3Min(min, v[i]);
			max = q3Max(max, v[i]);
		}

		aabb.setMin(min);
		aabb.setMax(max);
	}

	private static Matrix3f q3OuterProduct(Vector3fc u, Vector3fc v)
	{
		Vector3f a = v.mul(u.x(), new Vector3f());
		Vector3f b = v.mul(u.y(), new Vector3f());
		Vector3f c = v.mul(u.z(), new Vector3f());

		return new Matrix3f(
			a.x, a.y, a.z,
			b.x, b.y, b.z,
			c.x, c.y, c.z);
	}

	private static Matrix3f mul(Matrix3f m, float s)
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

	/**
	 * Maybe this works???????????????????
	 * 
	 * @param md
	 */
	public void ComputeMass(Q3MassData md)
	{
		// Calculate inertia tensor
		float ex2 = 4.0f * extents.x * extents.x;
		float ey2 = 4.0f * extents.y * extents.y;
		float ez2 = 4.0f * extents.z * extents.z;
		float mass = 8.0f * extents.x * extents.y * extents.z * density;
		float x = 1.0f / 12.0f * mass * (ey2 + ez2);
		float y = 1.0f / 12.0f * mass * (ex2 + ez2);
		float z = 1.0f / 12.0f * mass * (ex2 + ey2);
		Matrix3f I = new Matrix3f(
			x, 0, 0,
			0, y, 0,
			0, 0, z);

		Matrix3f matRot = local.orientation().asMatrix3f();

		// Transform tensor to local space
		I = matRot.mul(I).mul(matRot.transpose(new Matrix3f()));
		Matrix3f identity = new Matrix3f().identity();

		I.add(mul(identity, local.position().dot(local.position())).sub(
			mul(q3OuterProduct(local.position(), local.position()), mass)));

		md.center = new Vector3f(local.position());
		md.inertia = I;
		md.mass = mass;
	}

	public boolean TestPoint(Transform tx, Vector3fc p)
	{
		Transform world = tx.q3Mul(local);
		Vector3f p0 = world.q3MulT(p);

		for (int i = 0; i < 3; ++i)
		{
			float d = p0.get(i);
			float ei = extents.get(i);

			if (d > ei || d < -ei)
			{
				return false;
			}
		}

		return true;
	}

	public boolean Raycast(Transform tx, Q3RaycastData raycast)
	{
		Transform world = tx.q3Mul( local );
		Vector3f d = world.orientation().applyInverseRotation(raycast.dir, new Vector3f());
		Vector3f p = world.orientation().applyInverseRotation(raycast.start, new Vector3f());
		
		float epsilon = (float)( 1.0e-8 );
		float tmin = 0;
		float tmax = raycast.t;

		// t = (e[ i ] - p.[ i ]) / d[ i ]
		float t0;
		float t1;
		Vector3f n0 = new Vector3f();

		for ( int i = 0; i < 3; ++i )
		{
			// Check for ray parallel to and outside of AABB
			if ( Math.abs( d.get(i) ) < epsilon )
			{
				// Detect separating axes
				if ( p.get(i) < -extents.get(i) || p.get(i) > extents.get(i) )
				{
					return false;
				}
			}

			else
			{
				float d0 = 1.0f / d.get(i);
				float s = Math.signum( d.get(i));
				float ei = extents.get(i) * s;
				Vector3f n = new Vector3f();
				switch(i)
				{
					case 0:
						n.x = -s;
						break;
					case 1:
						n.y = -s;
						break;
					default:
						n.z = -s;
						break;
				}

				t0 = -(ei + p.get(i)) * d0;
				t1 = (ei - p.get(i)) * d0;

				if ( t0 > tmin )
				{
					n0 = n;
					tmin = t0;
				}

				tmax = Math.min( tmax, t1 );

				if ( tmin > tmax )
				{
					return false;
				}
			}
		}

		raycast.normal = world.orientation().applyRotation(n0, new Vector3f());
		raycast.toi = tmin;

		return true;
	}
}

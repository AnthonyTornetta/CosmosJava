package com.cornchipss.cosmos.physx.qu3e.geometry;

import org.joml.Matrix3f;
import org.joml.Vector3f;

import com.cornchipss.cosmos.physx.qu3e.collision.Q3Box;

public class Q3Math
{

	// Restitution mixing. The idea is to use the maximum bounciness, so bouncy
	// objects will never not bounce during collisions.

	public static float q3MixRestitution(Q3Box A, Q3Box B)
	{
		return Math.max(A.restitution(), B.restitution());
	}

	// Friction mixing. The idea is to allow a very low friction value to
	// drive down the mixing result. Example: anything slides on ice.

	public static float q3MixFriction(Q3Box A, Q3Box B)
	{
		return (float) Math.sqrt(A.friction() * B.friction());
	}

	public static void q3ComputeBasis(Vector3f a, Vector3f b, Vector3f c)
	{
		// Suppose vector a has all equal components and is a unit vector: a =
		// (s, s, s)
		// Then 3*s*s = 1, s = sqrt(1/3) = 0.57735027. This means that at least
		// one component of a
		// unit vector must be greater or equal to 0.57735027. Can use SIMD
		// select operation.

		if (Math.abs(a.x) >= 0.57735027f)
			b.set(a.y, -a.x, 0.0f);
		else
			b.set(0.0f, a.z, -a.y);

		b.normalize();
		a.cross(b, c);
	}

	public static Vector3f mul(Matrix3f m, Vector3f v)
	{
		return new Vector3f(m.m00 * v.x + m.m01 * v.y + m.m02 * v.z,
			m.m10 * v.x + m.m11 * v.y + m.m12 * v.z,
			m.m20 * v.x + m.m11 * v.y + m.m12 * v.z);
	}
}

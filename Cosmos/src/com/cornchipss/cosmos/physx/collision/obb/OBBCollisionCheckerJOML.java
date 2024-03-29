package com.cornchipss.cosmos.physx.collision.obb;

import org.joml.Intersectionf;
import org.joml.Vector3f;
import org.joml.Vector3fc;

import com.cornchipss.cosmos.memory.MemoryPool;
import com.cornchipss.cosmos.physx.collision.CollisionInfo;

public class OBBCollisionCheckerJOML implements IOBBCollisionChecker
{
	@Override
	public boolean testOBBOBB(OBBCollider a, OBBCollider b)
	{
		return (Intersectionf.testObOb((Vector3f) a.center(),
			(Vector3f) a.localAxis()[0], (Vector3f) a.localAxis()[1],
			(Vector3f) a.localAxis()[2], (Vector3f) a.halfwidths(),
			(Vector3f) b.center(), (Vector3f) b.localAxis()[0],
			(Vector3f) b.localAxis()[1], (Vector3f) b.localAxis()[2],
			(Vector3f) b.halfwidths()));
	}

	private boolean testMovingOBBOBB(Vector3fc aDeltaPos, Vector3fc deltaStart,
		OBBCollider a, OBBCollider b, CollisionInfo info, CollisionInfo actual)
	{
		boolean hit = false;

		Vector3f point = MemoryPool.getInstanceOrCreate(Vector3f.class);

		for (Vector3fc pt : a)
		{
			point.set(pt);
			point.add(deltaStart);

			if (testLineOBB(point, aDeltaPos, b, info))
			{
				if (info == null)
					return true;

				info.distanceSquared = 0;

				return true;
			}
		}

		MemoryPool.addToPool(point);

		return hit;
	}

	@Override
	public boolean testMovingOBBOBB(Vector3fc aDeltaPos, OBBCollider a,
		OBBCollider b, CollisionInfo info)
	{
		CollisionInfo actual = info;

		boolean madeInfo = false;
		if (info == null)
		{
			// It's still needed for JOML functions
			info = MemoryPool.getInstanceOrCreate(CollisionInfo.class);
			info.distanceSquared = Float.MAX_VALUE;
			info.normal.set(0);
			info.collisionPoint.set(0);
			
			madeInfo = true;
		}

		try
		{
			boolean hit = false;

			for (Vector3fc point : a.cornerIterator())
			{
				if (testLineOBB(point, aDeltaPos, b, info))
				{
					if (actual == null)
						return true;

					hit = true;
				}
			}

			if (!hit && testOBBOBB(a, b))
			{
				// Last resort collision
				if (actual != null)
				{
					float max = Math.max(
						Math.max(b.halfwidths().x(), b.halfwidths().y()),
						b.halfwidths().z());
					Vector3f deltaStart = MemoryPool
						.getInstanceOrCreate(Vector3f.class).set(aDeltaPos);

					if (deltaStart.x != 0 || deltaStart.y != 0
						|| deltaStart.z != 0)
					{
						deltaStart.normalize();
						Vector3f newDelta = MemoryPool
							.getInstanceOrCreate(Vector3f.class)
							.set(deltaStart);
						deltaStart.mul(-max);
						newDelta.mul(2 * max);

						MemoryPool.addToPool(newDelta);
						MemoryPool.addToPool(deltaStart);

						return testMovingOBBOBB(newDelta, deltaStart, a, b,
							info, actual);
					}
					else
					{
						MemoryPool.addToPool(deltaStart);

						// If the OBB isn't actually moving just take a guess
						info.collisionPoint.set(a.center());
						info.distanceSquared = 0;
						info.normal.set(0, 0, 0);

						return true;
					}
				}
				else
					return true;
			}

			return hit;

		}
		finally
		{
			if (madeInfo)
			{
				MemoryPool.addToPool(info);
			}
		}
	}

	@Override
	public boolean testLineOBB(Vector3fc point, Vector3fc aDeltaPos,
		OBBCollider b, CollisionInfo info)
	{
		Vector3f end = MemoryPool.getInstanceOrCreate(Vector3f.class).set(
			point.x() + aDeltaPos.x(), point.y() + aDeltaPos.y(),
			point.z() + aDeltaPos.z());

		Vector3f v0 = MemoryPool.getInstanceOrCreate(Vector3f.class),
			v1 = MemoryPool.getInstanceOrCreate(Vector3f.class),
			v2 = MemoryPool.getInstanceOrCreate(Vector3f.class);

		Vector3f temp = MemoryPool.getInstanceOrCreate(Vector3f.class);

		Vector3f dx = b.localAxis()[0].mul(b.halfwidths().x(),
			MemoryPool.getInstanceOrCreate(Vector3f.class));
		Vector3f dy = b.localAxis()[1].mul(b.halfwidths().y(),
			MemoryPool.getInstanceOrCreate(Vector3f.class));
		Vector3f dz = b.localAxis()[2].mul(b.halfwidths().z(),
			MemoryPool.getInstanceOrCreate(Vector3f.class));

		Vector3f dx2 = MemoryPool.getInstanceOrCreate(Vector3f.class).set(dx)
			.mul(2);
		Vector3f dy2 = MemoryPool.getInstanceOrCreate(Vector3f.class).set(dy)
			.mul(2);
		Vector3f dz2 = MemoryPool.getInstanceOrCreate(Vector3f.class).set(dz)
			.mul(2);

		try
		{
			final float EPSILON = 1e-5f;
			boolean hit = false;

			// LEFT + RIGHT SIDE CHECKS
			for (int signX = -1; signX <= 1; signX += 2)
			{
				// 1st Triangle

				v0.set(b.center());

				if (signX == -1)
					v0.sub(dx);
				else
					v0.add(dx);

				v0.add(dy);
				v0.add(dz);
				// v0 = +x, +y, +z

				v1.set(v0);
				v1.sub(dz2);
				// v1 = +x, +y, -z

				v2.set(v1);
				v2.sub(dy2);
				// v2 = +x, -y, -z

				if (Intersectionf.intersectLineSegmentTriangle(point, end, v0,
					v1, v2, EPSILON, temp))
				{
					if (info == null)
						return true;

					hit = true;
					float distSqrd = temp.distanceSquared(point);
					if (distSqrd < info.distanceSquared)
					{
						info.collisionPoint.set(temp);
						info.distanceSquared = distSqrd;

						info.normal.set(b.localAxis()[0]);
						info.normal.mul(signX);
					}
				}

				// 2nd Triangle

				// v0 = +x, +y, +z from last check

				v1.set(v0);
				v1.sub(dy2);
				// v1 = +x, -y, +z

				// v2 = +x, -y, -z from last check

				if (Intersectionf.intersectLineSegmentTriangle(point, end, v0,
					v1, v2, EPSILON, temp))
				{
					if (info == null)
						return true;

					hit = true;
					float distSqrd = temp.distanceSquared(point);
					if (distSqrd < info.distanceSquared)
					{
						info.collisionPoint.set(temp);
						info.distanceSquared = distSqrd;

						info.normal.set(b.localAxis()[0]);
						info.normal.mul(signX);
					}
				}
			}

			// TOP + BOTTOM SIDE CHECKS
			for (int sign = -1; sign <= 1; sign += 2)
			{
				// 1st Triangle

				v0.set(b.center());

				v0.add(dx);

				if (sign == -1)
					v0.sub(dy);
				else
					v0.add(dy);

				v0.add(dz);
				// v0 = +x, +y, +z

				v1.set(v0);
				v1.sub(dz2);
				// v1 = +x, +y, -z

				v2.set(v1);
				v2.sub(dx2);
				// v2 = -x, +y, -z

				if (Intersectionf.intersectLineSegmentTriangle(point, end, v0,
					v1, v2, EPSILON, temp))
				{
					if (info == null)
						return true;

					hit = true;
					float distSqrd = temp.distanceSquared(point);
					if (distSqrd < info.distanceSquared)
					{
						info.collisionPoint.set(temp);
						info.distanceSquared = distSqrd;

						info.normal.set(b.localAxis()[1]);
						info.normal.mul(sign);
					}
				}

				// 2nd Triangle

				// v0 = +x, +y, +z from last check

				v1.set(v0);
				v1.sub(dx2);
				// v1 = -x, +y, +z

				// v2 = -x, +y, -z from last check

				if (Intersectionf.intersectLineSegmentTriangle(point, end, v0,
					v1, v2, EPSILON, temp))
				{
					if (info == null)
						return true;

					hit = true;
					float distSqrd = temp.distanceSquared(point);
					if (distSqrd < info.distanceSquared)
					{
						info.collisionPoint.set(temp);
						info.distanceSquared = distSqrd;

						info.normal.set(b.localAxis()[1]);
						info.normal.mul(sign);
					}
				}
			}

			// FRONT + BACK SIDE CHECKS
			for (int sign = -1; sign <= 1; sign += 2)
			{
				// 1st Triangle

				v0.set(b.center());

				v0.add(dx);
				v0.add(dy);
				if (sign == -1)
					v0.sub(dz);
				else
					v0.add(dz);

				// v0 = +x, +y, +z

				v1.set(v0);
				v1.sub(dy2);
				// v1 = +x, -y, +z

				v2.set(v1);
				v2.sub(dx2);
				// v2 = -x, -y, +z

				if (Intersectionf.intersectLineSegmentTriangle(point, end, v0,
					v1, v2, EPSILON, temp))
				{
					if (info == null)
						return true;

					hit = true;
					float distSqrd = temp.distanceSquared(point);
					if (distSqrd < info.distanceSquared)
					{
						info.collisionPoint.set(temp);
						info.distanceSquared = distSqrd;

						info.normal.set(b.localAxis()[2]);
						info.normal.mul(sign);
					}
				}

				// 2nd Triangle

				// v0 = +x, +y, +z from last check

				v1.set(v0);
				v1.sub(dx2);
				// v1 = -x, +y, +z

				// v2 = -x, -y, +z from last check

				if (Intersectionf.intersectLineSegmentTriangle(point, end, v0,
					v1, v2, EPSILON, temp))
				{
					if (info == null)
						return true;

					hit = true;
					float distSqrd = temp.distanceSquared(point);
					if (distSqrd < info.distanceSquared)
					{
						info.collisionPoint.set(temp);
						info.distanceSquared = distSqrd;

						info.normal.set(b.localAxis()[2]);
						info.normal.mul(sign);
					}
				}
			}
			return hit;

		}
		finally
		{
			MemoryPool.addToPool(end);
			MemoryPool.addToPool(v0);
			MemoryPool.addToPool(v1);
			MemoryPool.addToPool(v2);

			MemoryPool.addToPool(dx);
			MemoryPool.addToPool(dy);
			MemoryPool.addToPool(dz);

			MemoryPool.addToPool(dx2);
			MemoryPool.addToPool(dy2);
			MemoryPool.addToPool(dz2);

			MemoryPool.addToPool(temp);
		}
	}

//	/**
//	 * Assumes the closest point is the one that's inside (this isn't always true but works well enough 99% of the time)
//	 * @param a The OBB hitting B
//	 * @param b The OBB being hit by A
//	 * @param collisionNormal The normal vector result of the collision - unchanged if no collision.  Leave null if no normal is needed
//	 */
//	private static float calculateNormal(OBBCollider a, OBBCollider b, Vector3f collisionNormal)
//	{
//		float closest = Float.MAX_VALUE;
//		Vector3f closestVec = new Vector3f();
//		
//		for(Vector3fc vertex : a)
//		{
//			float dist = vertex.distanceSquared(b.center());
//			if(dist < closest)
//			{
//				closest = dist;
//				closestVec.set(vertex);
//			}
//		}
//		
//		closestVec.sub(b.center());
//		
//		Vector3f bestAxis = new Vector3f();
//		bestAxis.set(b.localAxis()[0]);
//		float bestDot = closestVec.dot(bestAxis);
//		
//		for(int i = 1; i < b.localAxis().length * 2; i++)
//		{
//			Vector3fc v = b.localAxis()[i / 2];
//			
//			float sign = i % 2 == 0 ? 1 : -1;
//			
//			float dotHere = closestVec.dot(v.x() * sign, v.y() * sign, v.z() * sign);
//			if(dotHere > bestDot)
//			{
//				bestDot = dotHere;
//				bestAxis.set(v.x() * sign, v.y() * sign, v.z() * sign);
//			}
//		}
//		
//		collisionNormal.set(bestAxis);
//		
//		return closest;
//	}
}

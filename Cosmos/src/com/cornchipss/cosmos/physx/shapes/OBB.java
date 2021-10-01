package com.cornchipss.cosmos.physx.shapes;

import org.joml.Intersectionf;
import org.joml.Vector3f;
import org.joml.Vector3fc;

import com.cornchipss.cosmos.physx.Orientation;

public class OBB
{
	private Vector3f center; // c
	private Vector3f[] localAxis = new Vector3f[3]; // u
	private Vector3f halfwidths; // e
	private Orientation or;
	
	public OBB(Vector3fc center, Orientation orientation, Vector3fc halfwidths)
	{
		this.center = new Vector3f(center);
		localAxis[0] = new Vector3f(orientation.right());
		localAxis[1] = new Vector3f(orientation.up());
		localAxis[2] = new Vector3f(orientation.forward());
		this.halfwidths = new Vector3f(halfwidths);
		this.or = orientation;
	}
	
	/**
	 * In respect to a hitting b
	 * @param a The OBB hitting B
	 * @param b The OBB being hit by A
	 * @param collisionNormal The normal vector result of the collision - unchanged if no collision.  Leave null if no normal is needed
	 * @return If the two OBBs are hitting each other
	 */
	public static boolean testOBBOBB(OBB a, OBB b, Vector3f collisionNormal)
	{
		if(Intersectionf.testObOb(
				a.center, a.localAxis[0], a.localAxis[1], a.localAxis[2], a.halfwidths, 
				b.center, b.localAxis[0], b.localAxis[1], b.localAxis[2], b.halfwidths))
		{
			
			if(collisionNormal != null)
				calculateNormal(a, b, collisionNormal);
			
			return true;
		}
		return false;
	}
	
	/**
	 * Assumes the closest point is the one that's inside (this isn't always true but works well enough 99% of the time)
	 * @param a The OBB hitting B
	 * @param b The OBB being hit by A
	 * @param collisionNormal The normal vector result of the collision - unchanged if no collision.  Leave null if no normal is needed
	 */
	private static void calculateNormal(OBB a, OBB b, Vector3f collisionNormal)
	{
		float closest = Float.MAX_VALUE;
		Vector3f closestVec = new Vector3f();
		
		for(int dz = -1; dz <= 1; dz += 2)
		{
			for(int dy = -1; dy <= 1; dy += 2)
			{
				for(int dx = -1; dx <= 1; dx += 2)
				{
					Vector3f vertex = new Vector3f(a.halfwidths);
					vertex.mul(dx, dy, dz);
					a.or.applyRotation(vertex, vertex);
					vertex.add(a.center);
					
					float dist = vertex.distanceSquared(b.center);
					if(dist < closest)
					{
						closest = dist;
						closestVec.set(vertex);
					}
				}
			}
		}
		
		closestVec.sub(b.center);
		
		Vector3f bestAxis = new Vector3f();
		bestAxis.set(b.localAxis[0]);
		float bestDot = closestVec.dot(b.localAxis[0]);
		
		for(int i = 1; i < b.localAxis.length * 2; i++)
		{
			Vector3f v = b.localAxis[i % 2];
			float sign = 0 - 2 * (i % 2) + 1; // -1 if odd, 1 if even
			float dotHere = closestVec.dot(v.x * sign, v.y * sign, v.z * sign);
			if(dotHere > bestDot)
			{
				bestDot = dotHere;
				bestAxis.set(v.x * sign, v.y * sign, v.z * sign);
			}
		}
		
		collisionNormal.set(bestAxis);
	}
	
	public Vector3fc center() { return center; }
	public Vector3fc[] localAxis() { return localAxis; }
	public Vector3fc halfWidths() { return halfwidths; }
}

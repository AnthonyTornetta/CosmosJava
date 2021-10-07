package com.cornchipss.cosmos.physx.collision.obb;

import org.joml.Intersectionf;
import org.joml.Vector3f;
import org.joml.Vector3fc;

import com.cornchipss.cosmos.utils.Utils;

public class OBBCollisionCheckerJOML implements IOBBCollisionChecker
{
	@Override
	public boolean testOBBOBB(OBBCollider a, OBBCollider b, Vector3f collisionNormal)
	{
		if(Intersectionf.testObOb(
				(Vector3f)a.center(), (Vector3f)a.localAxis()[0], (Vector3f)a.localAxis()[1], 
				(Vector3f)a.localAxis()[2], (Vector3f)a.halfwidths(), 
				(Vector3f)b.center(), (Vector3f)b.localAxis()[0], (Vector3f)b.localAxis()[1], 
				(Vector3f)b.localAxis()[2], (Vector3f)b.halfwidths()))
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
	private static void calculateNormal(OBBCollider a, OBBCollider b, Vector3f collisionNormal)
	{
		float closest = Float.MAX_VALUE;
		Vector3f closestVec = new Vector3f();
		
		for(Vector3fc vertex : a)
		{
			float dist = vertex.distanceSquared(b.center());
			if(dist < closest)
			{
				closest = dist;
				closestVec.set(vertex);
			}
		}
		
		closestVec.sub(b.center());
		
		Vector3f bestAxis = new Vector3f();
		bestAxis.set(b.localAxis()[0]);
		bestAxis.mul(-1);
		float bestDot = closestVec.dot(bestAxis);
				
		Utils.println(bestAxis);
		
		for(int i = 1; i < b.localAxis().length * 2; i++)
		{
			Vector3fc v = b.localAxis()[i / 2];
			
			float sign =  i % 2 == 0 ? 1 : -1;
			sign = -1;
			Utils.println(v.mul(sign, new Vector3f()));
			
			float dotHere = closestVec.dot(v.x() * sign, v.y() * sign, v.z() * sign);
			if(dotHere > bestDot)
			{
				bestDot = dotHere;
				bestAxis.set(v.x() * sign, v.y() * sign, v.z() * sign);
			}
		}
		// asdf
		collisionNormal.set(bestAxis);
	}
}

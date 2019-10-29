package com.cornchipss.physics.collision.hitbox;

import org.joml.Vector3f;
import org.joml.Vector3fc;

import com.cornchipss.utils.Utils;
import com.cornchipss.utils.datatypes.Tuple;
import com.cornchipss.world.blocks.BlockFace;

/**
 * TODO: read up: https://www.toptal.com/game/video-game-physics-part-ii-collision-detection-for-solid-objects
 */
public abstract class Hitbox
{
	/**
	 * Returns true if two hitboxes are colliding based on the two positions
	 * @param a Hitbox A
	 * @param b Hitbox B
	 * @param positionA Position of hitbox A
	 * @param positionB Position of hitbox B
	 * @return true if two hitboxes are colliding based on the two positions
	 */
	public static boolean isColliding(Hitbox a, Hitbox b, Vector3fc positionA, Vector3fc positionB)
	{
		if(a == null || b == null)
			return false;
		
		Vector3fc[] cornersA = a.getCorners();
		Vector3fc[] cornersB = b.getCorners();
		
		for(int j = 0; j < cornersA.length; j+=2)
		{
			Vector3f va = Utils.add(cornersA[j], positionA);
			Vector3f va2 = Utils.add(cornersA[j + 1], positionA);
			
			Vector3f dimsA = Utils.sub(va2, va);
			
			for(int i = 0; i < cornersB.length; i+=2)
			{
				Vector3f vb = Utils.add(cornersB[i], positionB);
				Vector3f vb2 = Utils.add(cornersB[i + 1], positionB);
				
				Vector3f dimsB = Utils.sub(vb2, vb);
				
				if(vb.x + dimsB.x >= va.x && vb.x <= va.x + dimsA.x)
				{
					if(vb.y + dimsB.y >= va.y && vb.y <= va.y + dimsA.y)
					{
						if(vb.z + dimsB.z >= va.z && vb.z <= va.z + dimsA.z)
						{
							return true;
						}
					}
				}
			}
		}
		
		return false;
	}
	
	/**
	 * Gets a general rectangle that can be used for broadphase collision detection
	 * @return a general rectangle that can be used for broadphase collision detection
	 */
	public abstract Vector3fc getBoundingBox();
	
	/**
	 * Gets the 2 corners of the hitbox as if it were one rectangle in order of -x, -y, -z then x, y, z
	 * @return the 2 corners of the hitbox as if it were one rectangle in order of -x, -y, -z then x, y, z
	 */
	public abstract Vector3fc[] getCorners();

	/**
	 * Gets every verticie present in the hitbox - there may be duplicates in this
	 * @return every verticie present in the hitbox - there may be duplicates in this
	 */
	public Vector3fc[] getVerticies()
	{
		Vector3fc[] corners = getCorners();
		
		Vector3fc[] verts = new Vector3f[8 * (corners.length / 2)];
		
		int vertIndex = 0;
		
		for(int i = 0; i < corners.length; i += 2)
		{ 
			Vector3fc c1 = corners[i];
			Vector3fc c2 = corners[i + 1];
			
			verts[vertIndex++] = new Vector3f(c1);
			verts[vertIndex++] = new Vector3f(c1.x(), c1.y(), c2.z());
			verts[vertIndex++] = new Vector3f(c1.x(), c2.y(), c1.z());
			verts[vertIndex++] = new Vector3f(c2.x(), c1.y(), c1.z());
			verts[vertIndex++] = new Vector3f(c1.x(), c2.y(), c2.z());
			verts[vertIndex++] = new Vector3f(c2.x(), c2.y(), c1.z());
			verts[vertIndex++] = new Vector3f(c2.x(), c1.y(), c2.z());
			verts[vertIndex++] = new Vector3f(c2);
		}
		
		return verts;
	}
	
	/**
	 * Gets the two closest verticies in two hitboxes
	 * @param x Hitbox A
	 * @param y Hitbox B
	 * @return the two closest verticies in two hitboxes
	 */
	public static Tuple<Vector3fc> getClosestVerticies(Hitbox x, Hitbox y)
	{
		float shortestDist = -1;
		Tuple<Vector3fc> closest = new Tuple<>(2);
		
		for(Vector3fc v1 : x.getVerticies())
		{
			for(Vector3fc v2 : y.getVerticies())
			{
				float dist = v1.distanceSquared(v2);
				
				if(shortestDist == -1 || dist < shortestDist)
				{
					shortestDist = dist;
					closest.set(0, v1);
					closest.set(1, v2);
				}
			}
		}
		
		return closest;
	}

	public static Vector3fc getClosestVerticie(Hitbox h, Vector3f vert)
	{
		float shortestDist = -1;
		
		Vector3fc closest = null;
		
		for(Vector3fc v : h.getVerticies())
		{
			float dist = v.distanceSquared(vert);
			
			if(shortestDist == -1 || dist < shortestDist)
			{
				shortestDist = dist;
				closest = v;
			}
		}
		
		return closest;
	}

	public Vector3fc getExtremeNeg()
	{
		Vector3fc[] corners = getCorners();
		
		Vector3f extreme = null;
		
		for(Vector3fc v : corners)
		{
			if(extreme == null)
				extreme = new Vector3f(v);
			else
			{
				if(extreme.x < v.x())
					extreme.x = v.x();
				if(extreme.y < v.y())
					extreme.y = v.y();
				if(extreme.z < v.z())
					extreme.z = v.z();
			}
		}
		
		return extreme;
	}
	
	public Vector3fc getExtremePos()
	{
		Vector3fc[] corners = getCorners();
		
		Vector3f extreme = null;
		
		for(Vector3fc v : corners)
		{
			if(extreme == null)
				extreme = new Vector3f(v);
			else
			{
				if(extreme.x > v.x())
					extreme.x = v.x();
				if(extreme.y > v.y())
					extreme.y = v.y();
				if(extreme.z > v.z())
					extreme.z = v.z();
			}
		}
		
		return extreme;
	}

	public abstract Vector3fc[] getTriangles();
	public abstract BlockFace[] getFaces();
}

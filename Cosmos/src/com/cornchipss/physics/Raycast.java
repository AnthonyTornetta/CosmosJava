package com.cornchipss.physics;

import java.util.List;

import org.joml.AABBf;
import org.joml.Intersectionf;
import org.joml.LineSegmentf;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector3fc;

import com.cornchipss.physics.collision.hitbox.Hitbox;
import com.cornchipss.utils.Utils;
import com.cornchipss.utils.datatypes.LinkedArrayList;
import com.cornchipss.world.Location;
import com.cornchipss.world.Universe;
import com.cornchipss.world.blocks.BlockFace;

public class Raycast
{
	private List<Location> blocksHit;
	private List<BlockFace> facesHit;
	
	private Vector3fc start, end;
	
	private Raycast(List<Location> locs, List<BlockFace> faces, Vector3fc start, Vector3fc end)
	{
		this.blocksHit = locs;
		this.facesHit = faces;
		this.start = start;
		this.end = end;
	}
	
	public static Raycast fire(Vector3fc position, Universe universe, float rx, float ry, float maxDist)
	{
//		position = Utils.add(position, new Vector3f(0, 2, 0));
		
		Vector3f endPoint = pointAt(position, rx, ry, maxDist);
		
		Utils.println(position);
		Utils.println(endPoint);
		
//		float maxDistSqrd = maxDist * maxDist;
//		
//		Vector3f slopes = new Vector3f((endPoint.x - position.x) / maxDistSqrd, (endPoint.y - position.y) / maxDistSqrd, (endPoint.z - position.z) / maxDistSqrd);
		
		// Used for finding the slope if a 3d line; https://math.stackexchange.com/questions/799783/slope-of-a-line-in-3d-coordinate-system
		
		//line @ t
		
//		Intersectionf.intersectRayAab(position, new Vector3f(rx, ry, 0), 0, maxDist, )
		
//		RayAabIntersection ray = new RayAabIntersection(position.x, position.y, position.z, rx, ry, 0);
		
		
//		ry = -(ry - pi / 2) % (2 * pi);
		

		float pi = (float)Math.PI;
		
		float useRy = Math.abs(ry) % (2 * pi);
		
		if(useRy >= pi)
		{
			useRy = -2 * pi + useRy;
		}
		
		Utils.println(rx);
		Utils.println(useRy);
		
		LineSegmentf seg = new LineSegmentf(position, endPoint);
		
//		Rayf ray = new Rayf(position, new Vector3f(
//				useRy * (float)Math.cos(rx), // idk
//				-rx,  // correct
//				2)); // idk * idk
		
		Utils.println(useRy);
		
		Location[][][] locs = universe.getBlocksBetween(new Vector3f(-10, -20, -10), new Vector3f(10, 0, 10));
		
		LinkedArrayList<Location> hits = new LinkedArrayList<Location>();
		
		for(int z = 0; z < locs.length; z++)
		{
			for(int y = 0; y < locs[z].length; y++)
			{
				for(int x = 0; x < locs[z][y].length; x++)
				{
					if(locs[z][y][x] != null)
					{
						Location loc = locs[z][y][x];
						
						Hitbox box = loc.getBlock().getHitbox();
						Vector3f[] corners = box.getCorners();
						
//						Utils.println(corners);
						
						Vector3f leftCornerLoc = Utils.add(loc.getPosition(), corners[0]);
						Vector3f rightCornerLoc = Utils.add(loc.getPosition(), corners[1]);
						
						AABBf aabb = new AABBf(leftCornerLoc, rightCornerLoc);
						
						Vector2f result = new Vector2f();
						
						int res = Intersectionf.intersectLineSegmentAab(seg, aabb, result);
						
						if(res != Intersectionf.OUTSIDE)
						{
							hits.add(loc);
						}
						
//						{							
//							float distStart = leftCornerLoc.distanceSquared(position);
//							float distEnd = rightCornerLoc.distanceSquared(position);
//							
//							if(distStart <= maxDist || distEnd <= maxDist)
//							{
//								Vector3f lineAtStart = Utils.add(position, Utils.mul(slopes, distStart));
//								Vector3f lineAtEnd = Utils.add(position, Utils.mul(slopes, distEnd));
//								
//								if(isColliding(lineAtStart, lineAtEnd, leftCornerLoc, rightCornerLoc))
//								{
//									hits.add(loc);
//								}
//							}
//						}
					}
				}
			}
		}
		
		hits.finalize();
		return new Raycast(hits, null, position, endPoint);
	}
	
	private static boolean isColliding(Vector3f lineAtStart, Vector3f lineAtEnd, Vector3f leftCornerLoc,
			Vector3f rightCornerLoc)
	{
		return isColliding(lineAtStart.x, lineAtEnd.x, leftCornerLoc.x, rightCornerLoc.x) && 
				isColliding(lineAtStart.x, lineAtEnd.x, leftCornerLoc.x, rightCornerLoc.x) &&
				isColliding(lineAtStart.x, lineAtEnd.x, leftCornerLoc.x, rightCornerLoc.x);
	}

	private static boolean isColliding(float start, float end, float x1, float x2)
	{
		// start/end inside
		if(start >= x1 && start <= x2)
			return true;
		if(end >= x1 && end <= x2)
			return true;
		
		// on different sides
		if(start <= x1 && end >= x1)
			return true;
		if(start >= x1 && end <= x1)
			return true;
		if(start <= x2 && end >= x2)
			return true;
		if(start >= x2 && end <= x2)
			return true;
		
		return false;
	}
	
	private static boolean isVectorColliding(Vector3f rayAtStart, Vector3f rayAtEnd, Vector3f extremeNeg, Vector3f extremePos)
	{
//		for(int i = 1; i < 2; i++)
//		{
//			
//			Utils.println(i + ": ");
//			Utils.println(rayAtStart.get(i) <= extremeNeg.get(i) && rayAtEnd.get(i) >= extremeNeg.get(i));
//			Utils.println(rayAtStart.get(i) <= extremePos.get(i) && rayAtEnd.get(i) >= extremePos.get(i));
//			Utils.println(rayAtStart.get(i) >= extremeNeg.get(i) && rayAtStart.get(i) <= extremePos.get(i));
//			Utils.println(rayAtEnd.get(i) >= extremeNeg.get(i) && rayAtEnd.get(i) <= extremePos.get(i));
//			
//			if(!((rayAtStart.get(i) <= extremeNeg.get(i) && rayAtEnd.get(i) >= extremeNeg.get(i)
//					|| rayAtStart.get(i) <= extremePos.get(i) && rayAtEnd.get(i) >= extremePos.get(i))
//					|| (rayAtStart.get(i) >= extremeNeg.get(i) && rayAtStart.get(i) <= extremePos.get(i) 
//					|| rayAtEnd.get(i) >= extremeNeg.get(i) && rayAtEnd.get(i) <= extremePos.get(i))))
//			{
//				return false;
//			}
//		}
		
		return true;
	}
	
	private static Vector3f pointAt(Vector3fc position, float rx, float ry, float dist)
	{
		Vector3f endPoint = new Vector3f();
		
		final double j = dist * Math.cos(rx);
		
		endPoint.x = (float) (position.x() + j * Math.sin(ry));
		endPoint.y = (float) (position.y() - dist * Math.sin(rx));
		endPoint.z = (float) (position.z() - j * Math.cos(ry));
		
		return endPoint;
	}
	
	public Vector3fc getStartPoint() { return start; }
	public Vector3fc getEndPoint() { return end; }
	
	public Location getNthHit(int n) { return blocksHit.get(n); }
	public Location getFirstHit() { return size() > 0 ? getNthHit(0) : null; }
	public Location getLastHit() { return size() > 0 ? getNthHit(size() - 1) : null; }
	
	public int size() { return blocksHit.size(); }
	
	public List<Location> getBlocksHit() { return blocksHit; }
	public void setBlocksHit(List<Location> blocksHit) { this.blocksHit = blocksHit; }
	
	public List<BlockFace> getFacesHit() { return facesHit; }
	public void setFacesHit(List<BlockFace> facesHit) { this.facesHit = facesHit; }
}

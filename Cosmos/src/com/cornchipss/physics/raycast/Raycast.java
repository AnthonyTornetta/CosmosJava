package com.cornchipss.physics.raycast;

import java.util.List;

import org.joml.Intersectionf;
import org.joml.Vector3f;
import org.joml.Vector3fc;

import com.cornchipss.physics.collision.hitbox.Hitbox;
import com.cornchipss.utils.Utils;
import com.cornchipss.utils.datatypes.LinkedArrayList;
import com.cornchipss.world.Location;
import com.cornchipss.world.Universe;
import com.cornchipss.world.blocks.Block;
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
	
	/**
	 * Runs a line through the world starting at a given position with a given length at a given angle. It then returns a list of all blocks that were hit and the face they were hit on
	 * @param position The position to start the line
	 * @param universe The universe to check it in
	 * @param rx The rotation on the x axis
	 * @param ry The rotation on the y axis
	 * @param maxDist The length of the line to test
	 * @return A Raycast object storing the parrallel lists of the blocks hit + block faces.
	 */
	public static Raycast fire(Vector3fc position, Universe universe, float rx, float ry, float maxDist)
	{
		return fire(position, universe, rx, ry, maxDist, new RaycastOptions());
	}
	
	/**
	 * Runs a line through the world starting at a given position with a given length at a given angle. It then returns a list of all blocks that were hit and the face they were hit on
	 * @param position The position to start the line
	 * @param universe The universe to check it in
	 * @param rx The rotation on the x axis
	 * @param ry The rotation on the y axis
	 * @param maxDist The length of the line to test
	 * @param options Raycast options for a more specific search
	 * @return A Raycast object storing the parrallel lists of the blocks hit + block faces.
	 */
	public static Raycast fire(Vector3fc position, Universe universe, float rx, float ry, float maxDist, RaycastOptions options)
	{
		Vector3f endPoint = pointAt(position, rx, ry, maxDist);
		
		Location[][][] locs = universe.getBlocksBetween(position, endPoint);
		
		LinkedArrayList<Location> hits = new LinkedArrayList<>();
		LinkedArrayList<BlockFace> faces = new LinkedArrayList<>();
		
		for(int z = 0; z < locs.length; z++)
		{
			for(int y = 0; y < locs[z].length; y++)
			{
				for(int x = 0; x < locs[z][y].length; x++)
				{
					Location loc = locs[z][y][x];
					
					if(loc != null)
					{
						Block b = loc.getBlock();
						if(b == null)
							continue;
						
						Block[] blacklist = options.getBlacklist();
						Block[] whitelist = options.getWhitelist();
						
						if(blacklist != null && Utils.contains(blacklist, b))
							continue;
						if(whitelist != null && !Utils.contains(whitelist, b))
							continue;
						
						Hitbox hitbox = loc.getBlock().getHitbox();
						
						if(hitbox != null)
						{
							Vector3f intPoint = new Vector3f();
							
							Vector3fc[] tris = hitbox.getTriangles();
							BlockFace[] boxFaces = hitbox.getFaces();
							
							BlockFace bestFace = null;
							float distSqrdBest = 0;
							Location bestLoc = null;
							
							for(int i = 0; i < tris.length; i += 3)
							{								
								if(Intersectionf.intersectLineSegmentTriangle(
										position, endPoint, 
										Utils.add(loc.getPosition(), tris[i]), 
										Utils.add(loc.getPosition(), tris[i + 1]), 
										Utils.add(loc.getPosition(), tris[i + 2]),
										(float) 1E-9, intPoint))
								{
									BlockFace face = boxFaces[i / 3];
									
									float distSqrdTemp = Utils.add(loc.getPosition(), face.getDirection()).distanceSquared(position);
									
									if(bestLoc == null || distSqrdTemp < distSqrdBest)
									{
										bestLoc = loc;
										bestFace = face;
										distSqrdBest = distSqrdTemp;
									}
								}
							}
							
							if(bestLoc != null)
							{
								hits.add(bestLoc);
								faces.add(bestFace);
							}
						}
					}
				}
			}
		}
		
		hits.finalize();
		faces.finalize();
		
		return new Raycast(hits, faces, position, endPoint);
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

	public BlockFace getNthFace(int i) { return facesHit.get(i); }
}

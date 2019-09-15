package com.cornchipss.physics;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Deque;
import java.util.List;

import org.joml.Vector3f;

import com.cornchipss.physics.collision.hitbox.Hitbox;
import com.cornchipss.registry.Blocks;
import com.cornchipss.utils.Utils;
import com.cornchipss.world.Location;
import com.cornchipss.world.Universe;
import com.cornchipss.world.blocks.BlockFace;

public class Raycast
{
	private List<Location> blocksHit;
	private List<BlockFace> facesHit;
	
	private Vector3f start, end;
	
	private Raycast(List<Location> locs, List<BlockFace> faces, Vector3f start, Vector3f end)
	{
		this.blocksHit = locs;
		this.facesHit = faces;
		this.start = start;
		this.end = end;
	}
	
	public static Raycast fire(Vector3f position, Universe universe, float rx, float ry, float maxDist)
	{
		Vector3f endPoint = pointAt(position, rx, ry, maxDist);
		
		// Used for finding the slope if a 3d line; https://math.stackexchange.com/questions/799783/slope-of-a-line-in-3d-coordinate-system
		Vector3f direction = Utils.sub(endPoint, position);
		
		// Broad phase
		Location[][][] locs = universe.getBlocksBetween(position, endPoint);
		
		// Narrow phase
		List<Location> hits = new ArrayList<Location>();
		
		for(int z = 0; z < locs.length; z++)
		{
			for(int y = 0; y < locs[z].length; y++)
			{
				for(int x = 0; x < locs[z][y].length; x++)
				{
					Location loc = locs[z][y][x];
					
					if(loc != null && !loc.getBlock().equals(Blocks.air))
					{
						Vector3f pos = loc.getPosition();
						Hitbox hb = loc.getBlock().getHitbox();
						Vector3f box = hb.getBoundingBox(); // getting too specific may make it too hard to place blocks on it
						
						float posDist = pos.distance(position);
						float posBoxDist = Utils.add(pos, box).distance(position);
						
						if(posDist <= maxDist)
						{
							Vector3f rayAtStart = Utils.add(position, Utils.mul(direction, posDist / maxDist));
							Vector3f rayAtEnd = Utils.add(position, Utils.mul(direction,  posBoxDist / maxDist));
							
							if(Math.signum(pos.x + box.x - rayAtStart.x) != Math.signum(pos.x - rayAtEnd.x) || 
									rayAtStart.x >= pos.x && rayAtStart.x <= box.x + pos.x || rayAtEnd.x >= pos.x && rayAtEnd.x <= box.x + pos.x)
							{
								if(Math.signum(pos.y + box.y - rayAtStart.y) != Math.signum(pos.y - rayAtEnd.y) || 
										rayAtStart.y >= pos.y && rayAtStart.y <= box.y + pos.y || rayAtEnd.y >= pos.y && rayAtEnd.y <= box.y + pos.y)
								{
									if(Math.signum(pos.z + box.z - rayAtStart.z) != Math.signum(pos.z - rayAtEnd.z) || 
											rayAtStart.z >= pos.z && rayAtStart.z <= box.z + pos.z || rayAtEnd.z >= pos.z && rayAtEnd.z <= box.z + pos.z)
									{
										float distSqrd = loc.getPosition().distanceSquared(position);
										
										boolean placed = false;
										
										for(int i = 0; i < hits.size(); i++)
										{
											if(distSqrd < hits.get(0).getPosition().distanceSquared(position))
											{
												hits.add(i, loc);
												placed = true;
												break;
											}
										}
										
										if(!placed)
											hits.add(loc);
									}
								}
							}
						}
					}
				}
			}
		}
		
//		Collections.sort(hits, new Comparator<Location>() 
//		{
//			@Override
//			public int compare(Location o1, Location o2)
//			{
//				return (int) (o1.getPosition().distanceSquared(position) - o2.getPosition().distanceSquared(position));
//			}
//		});
		
		return new Raycast(hits, null, position, endPoint);
	}
	
	private static Vector3f pointAt(Vector3f start, float rx, float ry, float dist)
	{
		Vector3f endPoint = new Vector3f();
		
		final double j = dist * Math.cos(rx);
		
		endPoint.x = (float) (start.x + j * Math.sin(ry));
		endPoint.y = (float) (start.y - dist * Math.sin(rx));
		endPoint.z = (float) (start.z - j * Math.cos(ry));
		
		return endPoint;
	}
	
	public Vector3f getStartPoint() { return start; }
	public Vector3f getEndPoint() { return end; }
	
	public Location getNthHit(int n) { return blocksHit.get(n); }
	public Location getFirstHit() { return size() > 0 ? getNthHit(0) : null; }
	public Location getLastHit() { return size() > 0 ? getNthHit(size() - 1) : null; }
	
	public int size() { return blocksHit.size(); }
	
	public List<Location> getBlocksHit() { return blocksHit; }
	public void setBlocksHit(List<Location> blocksHit) { this.blocksHit = blocksHit; }
	
	public List<BlockFace> getFacesHit() { return facesHit; }
	public void setFacesHit(List<BlockFace> facesHit) { this.facesHit = facesHit; }
}

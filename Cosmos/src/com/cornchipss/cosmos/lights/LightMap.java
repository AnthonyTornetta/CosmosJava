package com.cornchipss.cosmos.lights;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.joml.Vector3f;
import org.joml.Vector3fc;
import org.joml.Vector3i;
import org.joml.Vector3ic;

import com.cornchipss.cosmos.utils.Maths;
import com.cornchipss.cosmos.utils.Utils;

public class LightMap
{
	private Map<Vector3ic, Integer> changes = new HashMap<>();
	
	private int w, h, l;
	
	public int width() { return w; }
	public int height() { return h; }
	public int length() { return l; }
	
	private Map<Vector3i, LightSource> lightsToAdd = new HashMap<>();
	private List<Vector3i> lightsToRemove = new LinkedList<>();
	private List<Vector3i> blockingsToCreate = new LinkedList<>();
	private List<Vector3i> blockingsToRemove = new LinkedList<>();
	
	private static class LightResult
	{
		LightSource src;
		int strength;
		Vector3ic origin;
		
		@Override
		public boolean equals(Object o)
		{
			if(o instanceof LightResult)
			{
				return ((LightResult)o).strength == strength && ((LightResult)o).origin.equals(origin);
			}
			return false;
		}
	}
	
	private static class LitBlock
	{
		Map<Vector3ic, LightResult> lightResults = new HashMap<>();
		Vector3f totalLight;
		
		public LitBlock(Vector3f v)
		{
			totalLight = v;
		}
		
		void recalcLight()
		{
			totalLight.set(0, 0, 0);
			for(LightResult rs : lightResults.values())
			{
				float ratio = rs.strength / (float)rs.src.strength();
				totalLight.add(rs.src.r() * ratio * rs.src.r() * ratio, 
						rs.src.g() * ratio * rs.src.g() * ratio, 
						rs.src.b() * ratio * rs.src.b() * ratio);
			}
			
			totalLight.x = Maths.sqrt(totalLight.x);
			totalLight.y = Maths.sqrt(totalLight.y);
			totalLight.z = Maths.sqrt(totalLight.z);
			
			if(totalLight.x >= 1 && totalLight.x >= totalLight.y && totalLight.x >= totalLight.z)
			{
				float ratioY = totalLight.y / totalLight.x;
				float ratioZ = totalLight.z / totalLight.x;
				
				totalLight.x = 1;
				totalLight.y = ratioY;
				totalLight.z = ratioZ;
			}
			
			if(totalLight.y >= 1 && totalLight.y >= totalLight.x && totalLight.y >= totalLight.z)
			{
				float ratioX = totalLight.x / totalLight.y;
				float ratioZ = totalLight.z / totalLight.y;
				
				totalLight.x = ratioX;
				totalLight.y = 1;
				totalLight.z = ratioZ;
			}
			
			if(totalLight.z >= 1 && totalLight.z >= totalLight.x && totalLight.z >= totalLight.y)
			{
				float ratioX = totalLight.x / totalLight.z;
				float ratioY = totalLight.y / totalLight.z;
				
				totalLight.x = ratioX;
				totalLight.y = ratioY;
				totalLight.z = 1;
			}
		}
		
		void addLightResult(LightResult r)
		{
			LightResult lr = lightResults.get(r.origin);
			if(lr != null)
			{
				lr.strength = r.strength;
			}
			else
				lightResults.put(r.origin, r);
			
			recalcLight();
		}
		
		void removeLightResult(LightResult r)
		{
			lightResults.remove(r.origin);
			
			recalcLight();
		}
		
		public boolean containsBetterLightResult(LightResult res)
		{
			LightResult lr = lightResults.get(res.origin);
			if(lr != null)
				return lr.strength > res.strength;
			return false;
		}

		public LightResult lightResultFromSource(LightSource src)
		{
			for(LightResult lr : lightResults.values())
			{
				if(lr.src.equals(src))
					return lr;
			}
			
			return null;
		}
		
		public LightResult largestLightResult()
		{
			int max = 0;
			LightResult best = null;
			
			for(LightResult lr : lightResults.values())
			{
				if(lr.strength > max)
				{
					max = lr.strength;
					best = lr;
				}
			}
			
			return best;
		}
	}
	
	private Map<Vector3ic, LightSource> lights = new HashMap<>();
	
	private LitBlock[][][] lightMap;
	private Vector3f[][][] calculatedMap;
	private boolean[][][]  blocked;
	
	public LightMap(int w, int h, int l)
	{
		this.w = w;
		this.h = h;
		this.l = l;
		
		lightMap = new LitBlock[l][h][w];
		calculatedMap = new Vector3f[l][h][w];
		blocked = new boolean[l][h][w];
		
		for(int z = 0; z < l; z++)
		{
			for(int y = 0; y < h; y++)
			{
				for(int x = 0; x < w; x++)
				{
					calculatedMap[z][y][x] = new Vector3f();
					lightMap[z][y][x] = new LitBlock(calculatedMap[z][y][x]);
				}
			}
		}
	}
	
	public boolean hasLightSource(int x, int y, int z)
	{
		return lights.containsKey(new Vector3i(x, y, z));
	}
	
	public boolean hasLight(int x, int y, int z)
	{
		return lightMap[z][y][x].lightResults.size() != 0;
	}
	
	public Vector3fc lightAt(int x, int y, int z)
	{
		return calculatedMap[z][y][x];
	}
	
	public boolean isBlocked(int x, int y, int z)
	{
		return blocked[z][y][x];
	}
	
	private void propagateAll(int x, int y, int z)
	{
		List<LightResult> toHandle = new LinkedList<>();
		
		for(LightResult lr : lightMap[z][y][x].lightResults.values())
		{
			toHandle.add(lr);
		}
		
		for(LightResult lr : toHandle)
		{
			propagateAt(x, y, z, lr.strength, lr.src, lr.origin);
		}
	}
	
	private void propagateSourceAt(int x, int y, int z, LightSource src, Vector3ic origin)
	{
		if(within(x, y, z))
		{
			LightResult lr = lightMap[z][y][x].lightResults.get(origin);
			if(lr != null)
				propagateAt(x, y, z, lr.strength, src, origin);
		}
	}
	
	private void calculateNextPoints(int x, int y, int z, int strength, 
			Vector3ic origin, Set<Vector3i> nextPoints, Set<Vector3i> onReserve)
	{		
		if(within(x, y, z))
		{
			if(strength < 0 || !blocked[z][y][x])
			{
				LightResult lr = lightMap[z][y][x].lightResults.get(origin);
				
				if(strength > 0)
				{
					if(lr == null || lr.strength < strength - 1)
						nextPoints.add(new Vector3i(x, y, z));
				}
				else if(lr != null)
				{
					if(strength <= -lr.strength)
					{
						nextPoints.add(new Vector3i(x, y, z));
					}
					else
					{
						onReserve.add(new Vector3i(x, y, z));
					}
				}
			}
		}
	}
	
	/**
	 * Propagates a light source at a given location with a given strength.  Does not have to be called from a block with an actual light
	 * @param x The x position of the light
	 * @param y The y position of the light
	 * @param z The z position of the light
	 * @param strength How many blocks the light can travel (+ = add light - = remove light)
	 * @param src The light source to spread
	 * @param origin The origin of the light source you are spreading
	 */
	private void propagateAt(int x, int y, int z, int strength, 
			LightSource src, final Vector3ic origin)
	{
		Set<Vector3i> points = new HashSet<>();
		Set<Vector3i> nextPoints = new HashSet<>();
		Set<Vector3i> onReserve = new HashSet<>();
		
		points.add(new Vector3i(x, y, z));
		
		while(points.size() != 0)
		{		
			for(Vector3i pt : points)
			{
				if(strength > 0)
				{
					LightResult res = new LightResult();
					res.origin = origin;
					res.src = src;
					res.strength = strength;
					
					LitBlock lb = lightMap[pt.z][pt.y][pt.x];
					
					if(!lb.containsBetterLightResult(res))
					{
						lb.addLightResult(res);
						
						if(strength - 1 != 0)
						{
							calculateNextPoints(pt.x - 1, pt.y, pt.z, strength, origin, nextPoints, onReserve);
							calculateNextPoints(pt.x + 1, pt.y, pt.z, strength, origin, nextPoints, onReserve);
							calculateNextPoints(pt.x, pt.y - 1, pt.z, strength, origin, nextPoints, onReserve);
							calculateNextPoints(pt.x, pt.y + 1, pt.z, strength, origin, nextPoints, onReserve);
							calculateNextPoints(pt.x, pt.y, pt.z - 1, strength, origin, nextPoints, onReserve);
							calculateNextPoints(pt.x, pt.y, pt.z + 1, strength, origin, nextPoints, onReserve);
						}
					}
				}
				else
				{
					LightResult res = new LightResult();
					res.origin = origin;
					res.src = src;
					res.strength = -strength + 1;
					
					LitBlock lb = lightMap[pt.z][pt.y][pt.x];
					
					if(!lb.containsBetterLightResult(res))
					{
						lb.removeLightResult(res);

						if(strength + 1 != 0)
						{
							calculateNextPoints(pt.x - 1, pt.y, pt.z, strength, origin, nextPoints, onReserve);
							calculateNextPoints(pt.x + 1, pt.y, pt.z, strength, origin, nextPoints, onReserve);
							calculateNextPoints(pt.x, pt.y - 1, pt.z, strength, origin, nextPoints, onReserve);
							calculateNextPoints(pt.x, pt.y + 1, pt.z, strength, origin, nextPoints, onReserve);
							calculateNextPoints(pt.x, pt.y, pt.z - 1, strength, origin, nextPoints, onReserve);
							calculateNextPoints(pt.x, pt.y, pt.z + 1, strength, origin, nextPoints, onReserve);
						}
						else
						{
							propagateSourceAt(pt.x - 1, pt.y, pt.z, src, origin);
							propagateSourceAt(pt.x + 1, pt.y, pt.z, src, origin);
							propagateSourceAt(pt.x, pt.y - 1, pt.z, src, origin);
							propagateSourceAt(pt.x, pt.y + 1, pt.z, src, origin);
							propagateSourceAt(pt.x, pt.y, pt.z - 1, src, origin);
							propagateSourceAt(pt.x, pt.y, pt.z + 1, src, origin);
							
							for(Vector3i nextPt : onReserve)
							{
								propagateSourceAt(nextPt.x, nextPt.y, nextPt.z, src, origin);
							}
							
							onReserve.clear();
						}
					}
				}
			}
			
			strength -= Math.signum(strength);
			
			points = nextPoints;
			nextPoints = new HashSet<>();
		}
	}
	
	private void removeAllLightsAt(int x, int y, int z)
	{
		if(hasLightSource(x, y, z))
		{
			removeLight(x, y, z);
		}
		
		List<LightResult> toHandle = new LinkedList<>();
		 
		for(LightResult lr : lightMap[z][y][x].lightResults.values())
		{
			toHandle.add(lr);
		}
		
		Vector3ic origin = new Vector3i(x, y, z);
		
		for(LightResult lr : toHandle)
		{
			changes.put(origin, Math.max(changes.getOrDefault(origin, 0), lr.strength));
			propagateAt(x, y, z, -lr.strength, lr.src, lr.origin);
		}
		
		lightMap[z][y][x].lightResults.clear();
		lightMap[z][y][x].totalLight.set(0,0,0);
	}
	
	private void setBlockingFin(int x, int y, int z)
	{		
		blocked[z][y][x] = true;

		if(hasLight(x, y, z))
		{
			removeAllLightsAt(x, y, z);
		}
	}
	
	public void setBlocking(int x, int y, int z)
	{
		blockingsToCreate.add(new Vector3i(x, y, z));
	}
	
	private void removeBlockingFin(int x, int y, int z)
	{
		Vector3i here = new Vector3i(x, y, z);
		
		if(isBlocked(x, y, z))
		{
			blocked[z][y][x] = false;
			
			if(within(x - 1, y, z))
			{
				LightResult l = lightMap[z][y][x - 1].largestLightResult();
				
				if(l != null)
					changes.put(here, Math.max(changes.getOrDefault(here, 0), l.strength - 1));
				
				propagateAll(x - 1, y, z);
			}
			if(within(x + 1, y, z))
			{
				LightResult l = lightMap[z][y][x + 1].largestLightResult();
				
				if(l != null)
					changes.put(here, Math.max(changes.getOrDefault(here, 0), l.strength - 1));
				
				propagateAll(x + 1, y, z);
			}
			if(within(x, y - 1, z))
			{
				LightResult l = lightMap[z][y - 1][x].largestLightResult();
				
				if(l != null)
					changes.put(here, Math.max(changes.getOrDefault(here, 0), l.strength - 1));
				
				propagateAll(x, y - 1, z);
			}
			if(within(x, y + 1, z))
			{
				LightResult l = lightMap[z][y + 1][x].largestLightResult();
				
				if(l != null)
					changes.put(here, Math.max(changes.getOrDefault(here, 0), l.strength - 1));
				
				propagateAll(x, y + 1, z);
			}
			if(within(x, y, z - 1))
			{
				LightResult l = lightMap[z - 1][y][x].largestLightResult();
				
				if(l != null)
					changes.put(here, Math.max(changes.getOrDefault(here, 0), l.strength - 1));
				
				propagateAll(x, y, z - 1);
			}
			if(within(x, y, z + 1))
			{
				LightResult l = lightMap[z + 1][y][x].largestLightResult();
				
				if(l != null)
					changes.put(here, Math.max(changes.getOrDefault(here, 0), l.strength - 1));
				
				propagateAll(x, y, z + 1);
			}
		}
	}
	
	public void removeBlocking(int x, int y, int z)
	{
		blockingsToRemove.add(new Vector3i(x, y, z));
	}
	
	private void addLightFin(LightSource l, int x, int y, int z)
	{
		Vector3i key = new Vector3i(x, y, z);
		changes.put(key, Math.max(changes.getOrDefault(key, 0), l.strength()));
		
		Vector3ic origin = new Vector3i(x, y, z);
		
		lights.put(origin, l);
		propagateAt(x, y, z, l.strength(), l, origin);
	}
	
	public void addLight(LightSource l, int x, int y, int z)
	{
		lightsToAdd.put(new Vector3i(x, y, z), l);
	}
	
	private void removeLightFin(int x, int y, int z)
	{
		Vector3i key = new Vector3i(x, y, z);
		
		LightSource src = lights.remove(key);
		
		if(src != null)
		{
			LightResult lr = lightMap[z][y][x].lightResultFromSource(src);
			
			changes.put(key, Math.max(changes.getOrDefault(key, 0), src.strength()));
			
			propagateAt(x, y, z, -lr.strength, lr.src, lr.origin);
		}
	}
	
	public void removeLight(int x, int y, int z)
	{
		Vector3i pt = new Vector3i(x, y, z);
		
		if(lights.containsKey(pt))
			lightsToRemove.add(pt);
		else if(lightsToAdd.containsKey(pt))
			lightsToAdd.remove(pt);
	}
	
	public boolean needsUpdating()
	{
		return blockingsToCreate.size() != 0 || blockingsToRemove.size() != 0 ||
				lightsToRemove.size() != 0 || lightsToAdd.size() != 0;
	}
	
	public Map<Vector3ic, Integer> updateMap()
	{
		for(Vector3i b : lightsToRemove)
		{
			removeLightFin(b.x, b.y, b.z);
		}
		
		for(Vector3i b : blockingsToRemove)
		{
			removeBlockingFin(b.x, b.y, b.z);
		}
		
		for(Vector3i b : blockingsToCreate)
		{
			setBlockingFin(b.x, b.y, b.z);
		}
		
		for(Vector3i b : lightsToAdd.keySet())
		{
			addLightFin(lightsToAdd.get(b), b.x, b.y, b.z);
		}
		
		blockingsToCreate.clear();
		blockingsToRemove.clear();
		lightsToRemove.clear();
		lightsToAdd.clear();
		
		Map<Vector3ic, Integer> ret = new HashMap<>(changes);
		changes.clear();
		return ret;
	}
	
	public void printMap()
	{
		for(int z = 0; z < length(); z++)
		{
			for(int y = 0; y < height(); y++)
			{
				for(int x = 0; x < width(); x++)
				{
					System.out.print(Utils.toEasyString(lightAt(x, y, z).x()) + " ");
				}
				System.out.println();
			}
			
			System.out.println();
		}
	}
	

	public void printDBG()
	{
		printMap();
		
		System.out.println("BLOCKED");
		for(int z = 0; z < length(); z++)
		{
			for(int y = 0; y < height(); y++)
			{
				for(int x = 0; x < width(); x++)
				{
					System.out.print(blocked[z][y][x] ? 1 : 0);
				}
				System.out.println();
			}
			
			System.out.println();
		}
		
		System.out.println("STRENGTHS");
		
		DecimalFormat df = new DecimalFormat("00");
		
		for(int z = 0; z < length(); z++)
		{
			for(int y = 0; y < height(); y++)
			{
				for(int x = 0; x < width(); x++)
				{
					if(lightMap[z][y][x].lightResults.size() == 0)
						System.out.print("00 ");
					else
						System.out.print(df.format(lightMap[z][y][x].lightResults.values().iterator().next().strength) + " ");
				}
				System.out.println();
			}
			
			System.out.println();
		}
		
		System.out.println("COMBO");
		
		printDBGCombo();
	}
	
	public void printDBGCombo()
	{
		DecimalFormat df = new DecimalFormat("00");

		for(int z = 0; z < length(); z++)
		{
			for(int y = 0; y < height(); y++)
			{
				for(int x = 0; x < width(); x++)
				{
					if(blocked[z][y][x])
					{
						System.out.print("-1 ");
					}
					else
					{
						if(lightMap[z][y][x].lightResults.size() == 0)
							System.out.print("00 ");
						else
							System.out.print(df.format(lightMap[z][y][x].lightResults.values().iterator().next().strength) + " ");
					}
				}
				System.out.println();
			}
			
			System.out.println();
		}
	}
	
	public boolean within(int x, int y, int z)
	{
		return z >= 0 && z < lightMap.length &&
				y >= 0 && y < lightMap[z].length &&
				x >= 0 && x < lightMap[z][y].length;
	}
	
	public Map<Vector3ic, Integer> changes()
	{
		return changes;
	}
}

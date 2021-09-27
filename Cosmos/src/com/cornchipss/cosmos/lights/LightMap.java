package com.cornchipss.cosmos.lights;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.joml.Vector3f;
import org.joml.Vector3fc;
import org.joml.Vector3i;
import org.joml.Vector3ic;

public class LightMap
{
	private Map<Vector3ic, Integer> changes = new HashMap<>();
	
	private int w, h, l;
	
	public int width() { return w; }
	public int height() { return h; }
	public int length() { return l; }
	
	private Map<Vector3i, LightSource> lightsToAdd;
	private List<Vector3i> lightsToRemove;
	private List<Vector3i> blockingsToCreate;
	private List<Vector3i> blockingsToRemove;
	
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
				totalLight.add(rs.src.r() * ratio, rs.src.g() * ratio, rs.src.b() * ratio);
			}
			
			if(totalLight.lengthSquared() > 1)
				totalLight.normalize();
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
	
	private void dewIt(int x, int y, int z, LightSource src, Vector3ic origin)
	{
		if(within(x, y, z))
		{
			LightResult lr = lightMap[z][y][x].lightResults.get(origin);
			if(lr != null)
				propagateAt(x, y, z, lr.strength, src, origin);
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
			LightSource src, Vector3ic origin)
	{
		List<Vector3i> points = new LinkedList<>();
		List<Vector3i> nextPoints = new LinkedList<>();
		
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
							if(within(pt.x - 1, pt.y, pt.z))
								nextPoints.add(new Vector3i(pt.x - 1, pt.y, pt.z));
							if(within(pt.x + 1, pt.y, pt.z))
								nextPoints.add(new Vector3i(pt.x + 1, pt.y, pt.z));
							if(within(pt.x, pt.y - 1, pt.z))
								nextPoints.add(new Vector3i(pt.x, pt.y - 1, pt.z));
							if(within(pt.x, pt.y + 1, pt.z))
								nextPoints.add(new Vector3i(pt.x, pt.y + 1, pt.z));
							if(within(pt.x, pt.y, pt.z - 1))
								nextPoints.add(new Vector3i(pt.x, pt.y, pt.z - 1));
							if(within(pt.x, pt.y, pt.z + 1))
								nextPoints.add(new Vector3i(pt.x, pt.y, pt.z + 1));
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
							if(within(pt.x - 1, pt.y, pt.z))
								nextPoints.add(new Vector3i(pt.x - 1, pt.y, pt.z));
							if(within(pt.x + 1, pt.y, pt.z))
								nextPoints.add(new Vector3i(pt.x + 1, pt.y, pt.z));
							if(within(pt.x, pt.y - 1, pt.z))
								nextPoints.add(new Vector3i(pt.x, pt.y - 1, pt.z));
							if(within(pt.x, pt.y + 1, pt.z))
								nextPoints.add(new Vector3i(pt.x, pt.y + 1, pt.z));
							if(within(pt.x, pt.y, pt.z - 1))
								nextPoints.add(new Vector3i(pt.x, pt.y, pt.z - 1));
							if(within(pt.x, pt.y, pt.z + 1))
								nextPoints.add(new Vector3i(pt.x, pt.y, pt.z + 1));
						}
						else
						{
							dewIt(pt.x - 1, pt.y, pt.z, src, origin);
							dewIt(pt.x + 1, pt.y, pt.z, src, origin);
							dewIt(pt.x, pt.y - 1, pt.z, src, origin);
							dewIt(pt.x, pt.y + 1, pt.z, src, origin);
							dewIt(pt.x, pt.y, pt.z - 1, src, origin);
							dewIt(pt.x, pt.y, pt.z + 1, src, origin);
						}
					}
				}
			}
			
			strength -= Math.signum(strength);
			
			points = nextPoints;
			nextPoints = new LinkedList<>();
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
	
	public void setBlocking(int x, int y, int z)
	{
		blocked[z][y][x] = true;
		
		if(hasLight(x, y, z))
		{
			removeAllLightsAt(x, y, z);
		}
	}
	
	public boolean removeBlocking(int x, int y, int z)
	{
		boolean b = isBlocked(x, y, z);
		
		Vector3i here = new Vector3i(x, y, z);
		
		if(b)
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
		
		return b;
	}
	
	public void addLight(LightSource l, int x, int y, int z)
	{
		Vector3i key = new Vector3i(x, y, z);
		changes.put(key, Math.max(changes.getOrDefault(key, 0), l.strength()));
		
		Vector3ic origin = new Vector3i(x, y, z);
		
		lights.put(origin, l);
		propagateAt(x, y, z, l.strength(), l, origin);
	}
	
	public void removeLight(int x, int y, int z)
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
	
	public boolean hasChanges()
	{
		return changes.size() != 0;
	}
	
	public void clearChanges()
	{
		changes.clear();
	}
	
	public void printMap()
	{
		for(int z = 0; z < length(); z++)
		{
			for(int y = 0; y < height(); y++)
			{
				for(int x = 0; x < width(); x++)
				{
					System.out.print(lightAt(x, y, z).x() + " ");
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

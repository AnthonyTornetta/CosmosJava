package com.cornchipss.cosmos.lights;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.joml.Vector3f;
import org.joml.Vector3i;
import org.joml.Vector3ic;

import com.cornchipss.cosmos.utils.Maths;

public class LightMap
{
	private static class LightResult
	{
		LightSource src;
		int strength;
		
		@Override
		public boolean equals(Object o)
		{
			if(o instanceof LightResult)
			{
				return ((LightResult)o).strength == strength && ((LightResult)o).src.equals(src);
			}
			return false;
		}
	}
	
	private static class LitBlock
	{
		List<LightResult> lightResults = new LinkedList<>();
		Vector3f totalLight;
		
		public LitBlock(Vector3f v)
		{
			totalLight = v;
		}
		
		void recalcLight()
		{
			totalLight.set(0, 0, 0);
			for(LightResult rs : lightResults)
			{
				float ratio = rs.strength / (float)rs.src.strength();
				totalLight.add(rs.src.r() * ratio, rs.src.g() * ratio, rs.src.b() * ratio);
			}
			
			Maths.normalizeIfNotZero(totalLight, 1, totalLight);
		}
		
		void addLightResult(LightResult r)
		{
			lightResults.add(r);
			
			recalcLight();
		}
		
		void removeLightResult(LightResult r)
		{
			lightResults.remove(r);
			
			recalcLight();
		}

		public boolean containsBetterLightResult(LightResult res)
		{
			for(LightResult lr : lightResults)
			{
				if(lr.src.equals(res.src))
				{
					return lr.strength > res.strength;
				}
			}
			return false;
		}
	}
	
	private Map<Vector3ic, LightSource> lights = new HashMap<>();
	
	private LitBlock[][][] lightMap;
	private Vector3f[][][] calculatedMap;
	private boolean[][][]  blocked;
	
	List<Vector3ic> recalcLights = new LinkedList<>();
	
	public LightMap(int w, int h, int l)
	{
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
	
	private boolean isBlocked(int x, int y, int z)
	{
		return blocked[z][y][x];
	}
	
	private void propagateAll(int x, int y, int z)
	{
		for(LightResult lr : lightMap[z][y][x].lightResults)
		{
			propagateAt(x, y, z, lr.strength, lr.src);
		}
	}
	
	/**
	 * Propagates a light source at a given location with a given strength.  Does not have to be called from a block with an actual light
	 * @param x The x position of the light
	 * @param y The y position of the light
	 * @param z The z position of the light
	 * @param strength How many blocks the light can travel (+ = add light - = remove light)
	 * @param src The light source to spread
	 */
	private void propagateAt(int x, int y, int z, int strength, LightSource src)
	{
		List<Vector3ic> pts = new LinkedList<>();
		List<Vector3ic> nextPts = new LinkedList<>();
		
		pts.add(new Vector3i(x, y, z));
		
		while(pts.size() != 0) // strength is checked when adding points
		{
			LightResult res = new LightResult();
			res.src = src;
			res.strength = Math.abs(strength);
			
			for(Vector3ic pt : pts)
			{
				LitBlock lb = lightMap[pt.z()][pt.y()][pt.x()];
				
				if(!lb.containsBetterLightResult(res))
				{
					if(strength < 0)
						lb.removeLightResult(res);
					else
						lb.addLightResult(res);
					
					if(strength > 1 || strength < -1)
					{
						if(within(x + 1, y, z) && !isBlocked(x + 1, y, z))
							nextPts.add(new Vector3i(x + 1, y, z));
						if(within(x - 1, y, z) && !isBlocked(x - 1, y, z))
							nextPts.add(new Vector3i(x - 1, y, z));
						if(within(x, y + 1, z) && !isBlocked(x, y + 1, z))
							nextPts.add(new Vector3i(x, y + 1, z));
						if(within(x, y - 1, z) && !isBlocked(x, y - 1, z))
							nextPts.add(new Vector3i(x, y - 1, z));
						if(within(x, y, z + 1) && !isBlocked(x, y, z + 1))
							nextPts.add(new Vector3i(x, y, z + 1));
						if(within(x, y, z - 1) && !isBlocked(x, y, z - 1))
							nextPts.add(new Vector3i(x, y, z - 1));
					}
				}
			}
			
			strength -= Math.signum(strength);
			pts = nextPts;
			nextPts = new LinkedList<>();
		}
	}
	
	public void removeLights(int x, int y, int z)
	{
		if(hasLightSource(x, y, z))
		{
			lights.remove(new Vector3i(x, y, z));
		}
				
		for(LightResult lr : lightMap[z][y][x].lightResults)
		{
			propagateAt(x, y, z, lr.strength, lr.src);
		}
		
		lightMap[z][y][x].lightResults.clear();
		lightMap[z][y][x].totalLight.set(0,0,0);
	}
	
	public void setBlocking(int x, int y, int z)
	{
		blocked[z][y][x] = true;
		
		if(hasLight(x, y, z))
		{
			removeLights(x, y, z);
		}
	}
	
	public boolean removeBlocking(int x, int y, int z)
	{
		boolean b = isBlocked(x, y, z);;
		
		if(b)
		{
			blocked[z][y][x] = false;
			
			if(within(x - 1, y, z))
				propagateAll(x - 1, y, z);
		}
		
		return b;
	}
	
	public void updateLightmap()
	{
		
	}
	
//	/**
//	 * Calculates the light map from scratch - overrides any previous light values
//	 * Keeps anything in the light map marked as {@linkplain LightMap#BLOCKED}
//	 * @return If this is calculated before, 
//	 * returns the bounds of the parts of it that changed [ leftmost corner, rightmost corner ] - 
//	 * otherwise the return result isn't of note, but it still has a size of 2 non-null values.  
//	 * If no changes were made, they will both be (-1, -1, -1)
//	 */
//	public Vector3i[] calculateLightMap()
//	{
//		
//	}
//	
	public boolean within(int x, int y, int z)
	{
		return z >= 0 && z < lightMap.length &&
				y >= 0 && y < lightMap[z].length &&
				x >= 0 && x < lightMap[z][y].length;
	}
}

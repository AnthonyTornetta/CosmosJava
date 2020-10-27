package test;

import java.util.LinkedList;
import java.util.List;

import com.cornchipss.rendering.ModelCreator;
import com.cornchipss.utils.Maths;
import com.cornchipss.utils.Utils;

public class BulkModel
{
	CubeModel[][][] cubes;
	
	List<Mesh> meshes;
	int calls = 0;
	public BulkModel(int w, int h, int l)
	{
		cubes = new CubeModel[l][h][w];
	}
	
	boolean within(int x, int y, int z)
	{
		return z >= 0 && z < cubes.length
				&& y >= 0 && y < cubes[z].length
				&& x >= 0 && x < cubes[z][y].length;
	}
	
	void setModel(int x, int y, int z, CubeModel model)
	{
		if(!within(x, y, z))
			return;
		
		if(!Utils.equals(cubes[z][y][x], model))
		{
			cubes[z][y][x] = model;
		}
	}
	
	private static class Plane
	{
		float x, y, z, width, height;
		
		Plane(float x, float y, float z)
		{
			this.x = x;
			this.y = y;
			this.z = z;
		}
		
		public String toString()
		{
			return "[" + x + "," + y + "," + z + "|" + width + "x" + height + "]";
		}
	}
	
	/**
	 * Greedy meshing algorithm
	 */
	void render()
	{
		// step 1: generate verticle cross section
		// step 2: generate horizontal cross section (https://0fps.files.wordpress.com/2012/06/slices.png?w=595&h=242)
		// step 3: turn those all into meshes
		
		int ZLEN = cubes.length;
		int YLEN = cubes[0].length;
		int XLEN = cubes[0][0].length;
		
		List<Plane> horizontalPlanes = new LinkedList<>();
		
		// assume a null block is a transparent block
		
		int widthBot = 0;
		int lenBot = 0;
		
		int widthTop = 0;
		int lenTop = 0;
		
		Plane bottomPlane = null;
		Plane topPlane = null;
		
		int recomputingTop = 0;
		int recomputingBot = 0;
		
		for(int y = 0; y < YLEN; y++)
		{
			if(bottomPlane != null)
			{
				bottomPlane.width = widthBot;
				bottomPlane.height = lenBot;
				horizontalPlanes.add(bottomPlane);
				bottomPlane = null;
			}
			
			if(topPlane != null)
			{
				topPlane.width = widthTop;
				topPlane.height = lenTop;
				horizontalPlanes.add(topPlane);
				topPlane = null;
			}
			
			for(int z = 0; z < ZLEN; z++)
			{
				lenBot = Math.min(lenBot + 1, ZLEN);
				lenTop = Math.min(lenTop + 1, ZLEN);
				
				for(int x = 0; x < XLEN; x++)
				{
					if(recomputingTop == 0)
					{
						recomputingBot = Math.max(0, recomputingBot - 1);
						
						if(cubes[z][y][x] != null && (!within(x, y - 1, z) || cubes[z][y - 1][x] == null))
						{
							if(bottomPlane == null)
							{
								lenBot = 1;
								widthBot = 0;
								
								bottomPlane = new Plane(x, y - 0.5f, z);
							}
							widthBot = Math.min(widthBot + 1, XLEN);
						}
						else if(bottomPlane != null)
						{
							if(lenBot == 1)
							{
								if(widthBot == 0)
									continue;
								
								bottomPlane.width = widthBot;
								bottomPlane.height = 1;
								
								horizontalPlanes.add(bottomPlane);
								
								bottomPlane = null;
							}
							else
							{
								bottomPlane.width = widthBot;
								bottomPlane.height = lenBot - 1;
								
								horizontalPlanes.add(bottomPlane);
								
								bottomPlane = null;
								
								recomputingBot = x + 1;
								x = -1;
							}
						}
					}
					
					if(recomputingBot == 0)
					{
						recomputingTop = Math.max(0, recomputingTop - 1);
						
						if(cubes[z][y][x] != null && (!within(x, y + 1, z) || cubes[z][y + 1][x] == null))
						{
							if(topPlane == null)
							{
								lenTop = 1;
								widthTop = 0;
								
								topPlane = new Plane(x, y + 0.5f, z);
							}
							
							widthTop = Math.min(widthTop + 1, XLEN);
						}
						else if(topPlane != null)
						{
							if(lenTop == 1)
							{
								if(widthTop == 0)
									continue;
								
								topPlane.width = widthTop;
								topPlane.height = 1;
								
								horizontalPlanes.add(topPlane);
								
								topPlane = null;
							}
							else
							{
								topPlane.width = widthTop;
								topPlane.height = lenTop - 1;
								
								horizontalPlanes.add(topPlane);
								
								topPlane = null;
								
								recomputingTop = x + 1;
								x = -1;
							}
						}
					}
				}
				
				if(bottomPlane != null && bottomPlane.x != 0)
				{
					bottomPlane.width = widthBot;
					bottomPlane.height = 1;
					horizontalPlanes.add(bottomPlane);
					bottomPlane = null;
				}
				
				if(topPlane != null && topPlane.x != 0)
				{
					topPlane.width = widthTop;
					topPlane.height = 1;
					horizontalPlanes.add(topPlane);
					topPlane = null;
				}
			}
			
		}
		
		if(bottomPlane != null)
		{
			bottomPlane.width = widthBot;
			bottomPlane.height = lenBot;
			horizontalPlanes.add(bottomPlane);
			bottomPlane = null;
		}
		
		if(topPlane != null)
		{
			topPlane.width = widthTop;
			topPlane.height = lenTop;
			horizontalPlanes.add(topPlane);
			topPlane = null;
		}
		
		meshes = new LinkedList<>();
		
		for(Plane p : horizontalPlanes)
		{
			meshes.add(Mesh.createMesh(new float[]
					{
						p.x, p.y, p.z,
						p.x, p.y, p.z + p.height,
						p.x + p.width, p.y, p.z + p.height,
						p.x + p.width, p.y, p.z
					},
					new int[]
					{
						0, 1, 2,
						2, 3, 0
					}));
		}
	}

	public void parse(String string, int yLevel)
	{
		String[] splt = string.split(("\n"));
		for(int y = 0; y < splt.length; y++)
		{
			for(int x = 0; x < splt[y].length(); x++)
			{
				if(splt[y].charAt(x) == 'X')
					setModel(x, yLevel, y, new CubeModel());
			}
		}
	}
}

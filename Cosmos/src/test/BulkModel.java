package test;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.cornchipss.utils.Utils;

public class BulkModel
{
	CubeModel[][][] cubes;
	
	Map<CubeModel, Mesh> meshses;
	Mesh beeg;
	
	private boolean debug;
	
	List<Integer> indicies = new LinkedList<>();
	List<Float> verticies = new LinkedList<>();
	List<Float> uvs = new LinkedList<>();
	int maxIndex = 0;
	
	public BulkModel(int w, int h, int l)
	{
		cubes = new CubeModel[l][h][w];
		
		meshses = new HashMap<>();
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
		
		CubeModel model;
		
		Plane(float x, float y, float z, CubeModel model)
		{
			this.x = x;
			this.y = y;
			this.z = z;
			this.model = model;
		}
		
		public String toString()
		{
			return "[" + x + "," + y + "," + z + "|" + width + "x" + height + "]";
		}
	}
	
	private void renderLeftsAndRights()
	{
		// step 1: generate verticle cross section
		// step 2: generate horizontal cross section (https://0fps.files.wordpress.com/2012/06/slices.png?w=595&h=242)
		// step 3: turn those all into meshes
		
		int ZLEN = cubes.length;
		int YLEN = cubes[0].length;
		int XLEN = cubes[0][0].length;
		
		List<Plane> horizontalPlanes = new LinkedList<>();
		
		// assume a null block is a transparent block
		
		int widthLeft = 0;
		int lenLeft = 0;
		
		int widthRight = 0;
		int lenRight = 0;
		
		Plane leftPlane = null;
		Plane rightPlane = null;
		
		int recomputingRight = 0;
		int recomputingLeft = 0;
		
		for(int x = 0; x < XLEN; x++)
		{
			if(leftPlane != null)
			{
				leftPlane.width = widthLeft;
				leftPlane.height = lenLeft;
				horizontalPlanes.add(leftPlane);
				leftPlane = null;
			}
			
			if(rightPlane != null)
			{
				rightPlane.width = widthRight;
				rightPlane.height = lenRight;
				horizontalPlanes.add(rightPlane);
				rightPlane = null;
			}
			
			for(int y = 0; y < YLEN; y++)
			{
				lenLeft = Math.min(lenLeft + 1, YLEN);
				lenRight = Math.min(lenRight + 1, YLEN);
				
				for(int z = 0; z < ZLEN; z++)
				{
					if(recomputingRight == 0)
					{
						recomputingLeft = Math.max(0, recomputingLeft - 1);
						
						if(cubes[z][y][x] != null && (!within(x - 1, y, z) || cubes[z][y][x - 1] == null))
						{
							if(leftPlane == null)
							{
								lenLeft = 1;
								widthLeft = 0;
								
								leftPlane = new Plane(x, y - 0.5f, z - 0.5f, cubes[z][y][x]);
							}
							widthLeft = Math.min(widthLeft + 1, ZLEN);
						}
						else if(leftPlane != null)
						{
							if(lenLeft == 1)
							{
								if(widthLeft == 0)
									continue;
								
								leftPlane.width = widthLeft;
								leftPlane.height = 1;
								
								horizontalPlanes.add(leftPlane);
								
								leftPlane = null;
							}
							else
							{
								leftPlane.width = widthLeft;
								leftPlane.height = lenLeft - 1;
								
								horizontalPlanes.add(leftPlane);
								
								leftPlane = null;
								
								recomputingLeft = z + 1;
								z = -1;
							}
						}
					}
					
					if(recomputingLeft == 0)
					{
						recomputingRight = Math.max(0, recomputingRight - 1);
						
						if(cubes[z][y][x] != null && (!within(x + 1, y, z) || cubes[z][y][x + 1] == null))
						{
							if(rightPlane == null)
							{
								lenRight = 1;
								widthRight = 0;
								
								rightPlane = new Plane(x + 1, y - 0.5f, z - 0.5f, cubes[z][y][x]);
							}
							widthRight = Math.min(widthRight + 1, ZLEN);
						}
						else if(rightPlane != null)
						{
							if(lenRight == 1)
							{
								if(widthRight == 0)
									continue;
								
								rightPlane.width = widthRight;
								rightPlane.height = 1;
								
								horizontalPlanes.add(rightPlane);
								
								rightPlane = null;
							}
							else
							{
								rightPlane.width = widthRight;
								rightPlane.height = lenRight - 1;
								
								horizontalPlanes.add(rightPlane);
								
								rightPlane = null;
								
								recomputingRight = z + 1;
								z = -1;
							}
						}
					}
				}
				
				if(leftPlane != null && leftPlane.z != -0.5f)
				{
					leftPlane.width = widthLeft;
					leftPlane.height = 1;
					horizontalPlanes.add(leftPlane);
					leftPlane = null;
				}
				
				if(rightPlane != null && rightPlane.z != -0.5f)
				{
					rightPlane.width = widthRight;
					rightPlane.height = 1;
					horizontalPlanes.add(rightPlane);
					rightPlane = null;
				}
			}
			
		}
		
		if(leftPlane != null)
		{
			leftPlane.width = widthLeft;
			leftPlane.height = lenLeft;
			horizontalPlanes.add(leftPlane);
			leftPlane = null;
		}
		
		if(rightPlane != null)
		{
			rightPlane.width = widthRight;
			rightPlane.height = lenRight;
			horizontalPlanes.add(rightPlane);
			rightPlane = null;
		}
		
		for(Plane p : horizontalPlanes)
		{
			processPlane(p);
			
			if(debug)
				Utils.println(p);
			
			maxIndex = indiciesAndUvs(p, maxIndex);
			
			verticies.add(p.x);
			verticies.add(p.y);
			verticies.add(p.z);
			
			verticies.add(p.x);
			verticies.add(p.y + p.height);
			verticies.add(p.z);
			
			verticies.add(p.x);
			verticies.add(p.y + p.height);
			verticies.add(p.z + p.width);
			
			verticies.add(p.x);
			verticies.add(p.y);
			verticies.add(p.z + p.width);
		}
	}
	
	private int indiciesAndUvs(Plane p, int maxIndex)
	{
		indicies.add(maxIndex);
		indicies.add(maxIndex + 1);
		indicies.add(maxIndex + 2);
		indicies.add(maxIndex + 2);
		indicies.add(maxIndex + 3);
		indicies.add(maxIndex);	
		  
		float uEnd = p.model.U + CubeModel.TEXTURE_DIMENSIONS * p.width;
		float vEnd = p.model.V + CubeModel.TEXTURE_DIMENSIONS * p.height;
		
		uvs.add(uEnd);
		uvs.add(vEnd);
		uvs.add(p.model.U);
		uvs.add(p.model.V);

		uvs.add(uEnd);
		uvs.add(p.model.V);
		uvs.add(p.model.U);
		uvs.add(p.model.V);


		uvs.add(p.model.U);
		uvs.add(p.model.V);
		uvs.add(p.model.U);
		uvs.add(p.model.V);

		uvs.add(p.model.U);
		uvs.add(vEnd);
		uvs.add(p.model.U);
		uvs.add(p.model.V);

		return maxIndex + 4;
	}
	
	private void renderBacksAndForwards()
	{
		// step 1: generate verticle cross section
		// step 2: generate horizontal cross section (https://0fps.files.wordpress.com/2012/06/slices.png?w=595&h=242)
		// step 3: turn those all into meshes
		
		int ZLEN = cubes.length;
		int YLEN = cubes[0].length;
		int XLEN = cubes[0][0].length;
		
		List<Plane> horizontalPlanes = new LinkedList<>();
		
		// assume a null block is a transparent block
		
		int widthBack = 0;
		int lenBack = 0;
		
		int widthForward = 0;
		int lenForward = 0;
		
		Plane backPlane = null;
		Plane forwardPlane = null;
		
		int recomputingForward = 0;
		int recomputingBack = 0;
		
		for(int z = 0; z < ZLEN; z++)
		{
			if(backPlane != null)
			{
				backPlane.width = widthBack;
				backPlane.height = lenBack;
				horizontalPlanes.add(backPlane);
				backPlane = null;
			}
			
			if(forwardPlane != null)
			{
				forwardPlane.width = widthForward;
				forwardPlane.height = lenForward;
				horizontalPlanes.add(forwardPlane);
				forwardPlane = null;
			}
			
			for(int y = 0; y < YLEN; y++)
			{
				lenBack = Math.min(lenBack + 1, YLEN);
				lenForward = Math.min(lenForward + 1, YLEN);
				
				for(int x = 0; x < XLEN; x++)
				{
					if(recomputingForward == 0)
					{
						recomputingBack = Math.max(0, recomputingBack - 1);
						
						if(cubes[z][y][x] != null && (!within(x, y, z - 1) || cubes[z - 1][y][x] == null))
						{
							if(backPlane == null)
							{
								lenBack = 1;
								widthBack = 0;
								
								backPlane = new Plane(x, y - 0.5f, z - 0.5f, cubes[z][y][x]);
							}
							widthBack = Math.min(widthBack + 1, XLEN);
						}
						else if(backPlane != null)
						{
							if(lenBack == 1)
							{
								if(widthBack == 0)
									continue;
								
								backPlane.width = widthBack;
								backPlane.height = 1;
								
								horizontalPlanes.add(backPlane);
								
								backPlane = null;
							}
							else
							{
								backPlane.width = widthBack;
								backPlane.height = lenBack - 1;
								
								horizontalPlanes.add(backPlane);
								
								backPlane = null;
								
								recomputingBack = x + 1;
								x = -1;
							}
						}
					}
					
					if(recomputingBack == 0)
					{
						recomputingForward = Math.max(0, recomputingForward - 1);
						
						if(cubes[z][y][x] != null && (!within(x, y, z + 1) || cubes[z + 1][y][x] == null))
						{
							if(forwardPlane == null)
							{
								lenForward = 1;
								widthForward = 0;
								
								forwardPlane = new Plane(x, y - 0.5f, z + 0.5f, cubes[z][y][x]);
							}
							
							widthForward = Math.min(widthForward + 1, XLEN);
						}
						else if(forwardPlane != null)
						{
							if(lenForward == 1)
							{
								if(widthForward == 0)
									continue;
								
								forwardPlane.width = widthForward;
								forwardPlane.height = 1;
								
								horizontalPlanes.add(forwardPlane);
								
								forwardPlane = null;
							}
							else
							{
								forwardPlane.width = widthForward;
								forwardPlane.height = lenForward - 1;
								
								horizontalPlanes.add(forwardPlane);
								
								forwardPlane = null;
								
								recomputingForward = x + 1;
								x = -1;
							}
						}
					}
				}
				
				if(backPlane != null && backPlane.x != 0)
				{
					backPlane.width = widthBack;
					backPlane.height = 1;
					horizontalPlanes.add(backPlane);
					backPlane = null;
				}
				
				if(forwardPlane != null && forwardPlane.x != 0)
				{
					forwardPlane.width = widthForward;
					forwardPlane.height = 1;
					horizontalPlanes.add(forwardPlane);
					forwardPlane = null;
				}
			}
			
		}
		
		if(backPlane != null)
		{
			backPlane.width = widthBack;
			backPlane.height = lenBack;
			horizontalPlanes.add(backPlane);
			backPlane = null;
		}
		
		if(forwardPlane != null)
		{
			forwardPlane.width = widthForward;
			forwardPlane.height = lenForward;
			horizontalPlanes.add(forwardPlane);
			forwardPlane = null;
		}
		
		for(Plane p : horizontalPlanes)
		{
			processPlane(p);
			
			if(debug)
				Utils.println(p);
			
			maxIndex = indiciesAndUvs(p, maxIndex);
			
			verticies.add(p.x);
			verticies.add(p.y);
			verticies.add(p.z);
			
			verticies.add(p.x);
			verticies.add(p.y + p.height);
			verticies.add(p.z);
			
			verticies.add(p.x + p.width);
			verticies.add(p.y + p.height);
			verticies.add(p.z);
			
			verticies.add(p.x + p.width);
			verticies.add(p.y);
			verticies.add(p.z);
		}
	}
	
	/**
	 * why? idk
	 * @param p
	 */
	private void processPlane(Plane p)
	{
		p.z -= 0.5f;
		p.x -= 0.5f;
	}
	
	private void renderBottomsAndTops()
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
								
								bottomPlane = new Plane(x, y - 0.5f, z - 0.5f, cubes[z][y][x]);
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
								
								topPlane = new Plane(x, y + 0.5f, z - 0.5f, cubes[z][y][x]);
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
		
		for(Plane p : horizontalPlanes)
		{
			processPlane(p);
			
			if(debug)
				Utils.println(p);
			
			maxIndex = indiciesAndUvs(p, maxIndex);
			
			verticies.add(p.x);
			verticies.add(p.y);
			verticies.add(p.z);
			
			verticies.add(p.x);
			verticies.add(p.y);
			verticies.add(p.z + p.height);
			
			verticies.add(p.x + p.width);
			verticies.add(p.y);
			verticies.add(p.z + p.height);
			
			verticies.add(p.x + p.width);
			verticies.add(p.y);
			verticies.add(p.z);
		}
	}
	
	/**
	 * Greedy meshing algorithm
	 */
	void render()
	{
		verticies.clear();
		indicies.clear();
		uvs.clear();
		
		maxIndex = 0;
		meshses.clear();
		
		renderBottomsAndTops();
		renderBacksAndForwards();
		renderLeftsAndRights();
		
		int i = 0;
		int[] indiciesArr = new int[indicies.size()];
		for(int index : indicies)
			indiciesArr[i++] = index;
		
		i = 0;
		float[] verticiesArr = new float[verticies.size()];
		for(float vertex : verticies)
			verticiesArr[i++] = vertex;
		
		i = 0;
		float[] uvsArr = new float[uvs.size()];
		for(float uv : uvs)
			uvsArr[i++] = uv;
		
		beeg = Mesh.createMesh(verticiesArr, indiciesArr, uvsArr);
	}

	public void parse(String string, int yLevel)
	{
		String[] splt = string.split(("\n"));
		for(int y = 0; y < splt.length; y++)
		{
			for(int x = 0; x < splt[y].length(); x++)
			{
				if(splt[y].charAt(x) == 'X')
					setModel(x, yLevel, y, new CubeModel(0, 0));
			}
		}
	}

	public void debug(boolean b)
	{
		debug = b;
	}
}

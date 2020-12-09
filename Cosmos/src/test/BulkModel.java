package test;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.cornchipss.utils.Utils;

import test.models.BlockSide;
import test.models.CubeModel;
import test.models.GrassModel;

public class BulkModel
{
	CubeModel[][][] cubes;
	
	Map<CubeModel, Mesh> meshses;
	Mesh beeg;
	
	private static Axis botTop = new Axis() 
	{
		@Override
		public float zOff(int delta)
		{
			return -0.5f;
		}
		
		@Override
		public float yOff(int delta)
		{
			return delta * 0.5f;
		}
		
		@Override
		public float xOff(int delta)
		{
			return 0;
		}
		
		@Override
		public BlockSide side(int delta)
		{
			return delta < 0 ? BlockSide.BOTTOM : BlockSide.TOP;
		}
		
		@Override
		public CubeModel nextModel(BulkModel m, int x, int y, int z, int delta)
		{
			return getModelAt(m, x, y, z + delta);
		}
		
		@Override
		public CubeModel getModelAt(BulkModel m, int x, int y, int z)
		{
			return m.within(x, z, y) ? m.cubes[y][z][x] : null;
		}
		
		@Override
		public int ZLEN(BulkModel m)
		{
			return m.cubes[0].length;
		}
		
		@Override
		public int YLEN(BulkModel m)
		{
			return m.cubes.length;
		}
		
		@Override
		public int XLEN(BulkModel m)
		{
			return m.cubes[0][0].length;
		}
		
		@Override
		public void addPlane(List<Float> verticies, Plane p)
		{
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
	},
	
	backFront = new Axis() 
	{
		@Override
		public float zOff(int delta)
		{
			return delta * 0.5f;
		}
		
		@Override
		public float yOff(int delta)
		{
			return -0.5f;
		}
		
		@Override
		public float xOff(int delta)
		{
			return 0;
		}
		
		@Override
		public BlockSide side(int delta)
		{
			return delta < 0 ? BlockSide.BACK : BlockSide.FRONT;
		}
		
		@Override
		public CubeModel nextModel(BulkModel m, int x, int y, int z, int delta)
		{
			return getModelAt(m, x, y, z + delta);
		}
		
		@Override
		public CubeModel getModelAt(BulkModel m, int x, int y, int z)
		{
			return m.within(z, y, x) ? m.cubes[x][y][z] : null;
		}
		
		@Override
		public int ZLEN(BulkModel m)
		{
			return m.cubes[0][0].length;
		}
		
		@Override
		public int YLEN(BulkModel m)
		{
			return m.cubes[0].length;
		}
		
		@Override
		public int XLEN(BulkModel m)
		{
			return m.cubes.length;
		}
		
		@Override
		public void addPlane(List<Float> verticies, Plane p)
		{
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
	},
	
	leftRight = new Axis() 
	{
		@Override
		public float zOff(int delta)
		{
			return delta < 0 ? 0 : 1;
		}
		
		@Override
		public float yOff(int delta)
		{
			return -0.5f;
		}
		
		@Override
		public float xOff(int delta)
		{
			return -0.5f;
		}
		
		@Override
		public BlockSide side(int delta)
		{
			return delta < 0 ? BlockSide.LEFT : BlockSide.RIGHT;
		}
		
		@Override
		public CubeModel nextModel(BulkModel m, int x, int y, int z, int delta)
		{
			return getModelAt(m, x, y, z + delta);
		}
		
		@Override
		public CubeModel getModelAt(BulkModel m, int x, int y, int z)
		{
			return m.within(z, y, x) ? m.cubes[x][y][z] : null;
		}
		
		@Override
		public int ZLEN(BulkModel m)
		{
			return m.cubes[0][0].length;
		}
		
		@Override
		public int YLEN(BulkModel m)
		{
			return m.cubes[0].length;
		}
		
		@Override
		public int XLEN(BulkModel m)
		{
			return m.cubes.length;
		}
		
		@Override
		public void addPlane(List<Float> verticies, Plane p)
		{
			verticies.add(p.z);
			verticies.add(p.y);
			verticies.add(p.x);
			
			verticies.add(p.z);
			verticies.add(p.y + p.height);
			verticies.add(p.x);
			
			verticies.add(p.z);
			verticies.add(p.y + p.height);
			verticies.add(p.x + p.width);
			
			verticies.add(p.z);
			verticies.add(p.y);
			verticies.add(p.x + p.width);
		}
	};
		
	
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
		
		BlockSide side;
		CubeModel model;
		
		Plane(float x, float y, float z, CubeModel model, BlockSide side)
		{
			this.x = x;
			this.y = y;
			this.z = z;
			this.model = model;
			this.side = side;
		}
		
		public String toString()
		{
			return "[" + x + "," + y + "," + z + "|" + width + "x" + height + "]";
		}
	}
	
	private static abstract interface Axis
	{
		int ZLEN(BulkModel m);
		int YLEN(BulkModel m);
		int XLEN(BulkModel m);
		CubeModel getModelAt(BulkModel m, int x, int y, int z);
		CubeModel nextModel(BulkModel m, int x, int y, int z, int delta);
		BlockSide side(int delta);
		
		float xOff(int delta);
		float yOff(int delta);
		float zOff(int delta);
		
		void addPlane(List<Float> verticies, Plane p);
	}
	
	public void renderAxis(Axis axis)
	{
		// step 1: generate verticle cross section
		// step 2: generate horizontal cross section (https://0fps.files.wordpress.com/2012/06/slices.png?w=595&h=242)
		// step 3: turn those all into meshes
		
		int widthNeg = 0, widthPos = 0;
		int lenNeg = 0, lenPos = 0;
		
		int recomputingNeg = 0;
		int recomputingPos = 0;
		
		Plane negPlane = null, posPlane = null;
		
		List<Plane> planes = new LinkedList<>();
		
		for(int z = 0; z < axis.ZLEN(this); z++) // y
		{
			for(int y = 0; y < axis.YLEN(this); y++) // z
			{
				lenNeg = Math.min(lenNeg + 1, axis.YLEN(this));
				lenPos = Math.min(lenPos + 1, axis.YLEN(this));
				
				for(int x = 0; x < axis.XLEN(this); x++) // x
				{
					if(recomputingPos == 0)
					{
						recomputingPos = Math.max(0, recomputingPos - 1);
						
						CubeModel modelHere = axis.getModelAt(this, x, y, z);
						
						if(modelHere != null && axis.nextModel(this, x, y, z, -1) == null 
								&& (negPlane == null || Utils.equals(negPlane.model, modelHere)))
						{
							if(negPlane == null)
							{
								lenNeg = 1;
								widthNeg = 0;
								
								negPlane = new Plane(x + axis.xOff(-1), y + axis.yOff(-1), z + axis.zOff(-1), modelHere, axis.side(-1));
							}
							
							widthNeg = Math.min(widthNeg + 1, axis.XLEN(this));
						}
						else if(negPlane != null)
						{
							if(lenNeg == 1)
							{
								if(widthNeg == 0)
									continue;
								else
								{
									negPlane.width = widthNeg;
									negPlane.height = 1;
									
									planes.add(negPlane);
									negPlane = null;
								}
							}
							else
							{
								negPlane.width = widthNeg;
								negPlane.height = lenNeg - 1;
								
								planes.add(negPlane);
								negPlane = null;
								
								recomputingNeg = x + 1;
								x = -1;
							}
						}
					}
					
					if(recomputingNeg == 0)
					{
						recomputingNeg = Math.max(0, recomputingNeg - 1);
						
						CubeModel modelHere = axis.getModelAt(this, x, y, z);

						if(modelHere != null && axis.nextModel(this, x, y, z, 1) == null 
								&& (posPlane == null || Utils.equals(posPlane.model, modelHere)))
						{
							if(posPlane == null)
							{
								lenPos = 1;
								widthPos = 0;
								
								posPlane = new Plane(x + axis.xOff(1), y + axis.yOff(1), z + axis.zOff(1), modelHere, axis.side(1));
							}
							
							widthPos = Math.min(widthPos + 1, axis.XLEN(this));
						}
						else if(posPlane != null)
						{
							if(lenPos == 1)
							{
								if(widthPos == 0)
									continue;
								else
								{
									posPlane.width = widthPos;
									posPlane.height = 1;
									
									planes.add(posPlane);
									posPlane = null;
								}
							}
							else
							{
								posPlane.width = widthPos;
								posPlane.height = lenPos - 1;
								
								planes.add(posPlane);
								posPlane = null;
								
								recomputingPos = x + 1;
								x = -1;
							}
						}
					}
				}
			}
		}
		
		if(negPlane != null)
		{
			negPlane.width = widthNeg;
			negPlane.height = lenNeg;
			planes.add(negPlane);
			negPlane = null;
		}
		
		if(posPlane != null)
		{
			posPlane.width = widthPos;
			posPlane.height = lenPos;
			planes.add(posPlane);
			posPlane = null;
		}
		
		for(Plane p : planes)
		{
			p.z -= 0.5f;
			p.x -= 0.5f;
			
			maxIndex = indiciesAndUvs(p, maxIndex);
			
			axis.addPlane(verticies, p);
		}
	}
	
	private int indiciesAndUvs(Plane p, int maxIndex)
	{
		int[] indiciesArr = p.model.indicies(p.side);
		int max = -1;
		
		for(int index : indiciesArr)
		{
			indicies.add(index + maxIndex);
			if(max < index)
				max = index;
		}
		  
		float u = p.model.u(p.side);
		float v = p.model.v(p.side);
		
		float uEnd = u + CubeModel.TEXTURE_DIMENSIONS * p.width;
		float vEnd = v + CubeModel.TEXTURE_DIMENSIONS * p.height;
		
		uvs.add(uEnd);
		uvs.add(vEnd);
		uvs.add(u);
		uvs.add(v);
		
		uvs.add(uEnd);
		uvs.add(v);
		uvs.add(u);
		uvs.add(v);


		uvs.add(u);
		uvs.add(v);
		uvs.add(u);
		uvs.add(v);

		uvs.add(u);
		uvs.add(vEnd);
		uvs.add(u);
		uvs.add(v);

		return maxIndex + max + 1;
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
		
		renderAxis(botTop);
		renderAxis(backFront);
		renderAxis(leftRight);
		
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
					setModel(x, yLevel, y, new GrassModel());
			}
		}
	}
}

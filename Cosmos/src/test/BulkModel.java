package test;

import java.util.LinkedList;
import java.util.List;

import com.cornchipss.utils.Utils;

import test.models.BlockSide;
import test.models.CubeModel;
import test.models.IHasModel;

public class BulkModel
{
	private IHasModel[][][] cubes;

	public void setModels(IHasModel[][][] blocks)
	{
		this.cubes = blocks;
	}
	
	private Mesh combinedModel;
	
	private static Axis botTop = new Axis() 
	{
		@Override
		public float zOff(int delta)
		{
			return delta == 1 ? 1 : 0;
		}
		
		@Override
		public float yOff(int delta)
		{
			return 0;
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
		public IHasModel nextModel(BulkModel m, int x, int y, int z, int delta,
				BulkModel left, BulkModel right, BulkModel top, 
				BulkModel bottom, BulkModel front, BulkModel back)
		{
			return getModelAt(m, x, y, z + delta);
		}
		
		@Override
		public IHasModel getModelAt(BulkModel m, int x, int y, int z)
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
			float x = p.x, z = p.y, y = p.z;
			
			verticies.add(x);
			verticies.add(y);
			verticies.add(z);
			
			verticies.add(x);
			verticies.add(y);
			verticies.add(z + p.height);
			
			verticies.add(x + p.width);
			verticies.add(y);
			verticies.add(z + p.height);
			
			verticies.add(x + p.width);
			verticies.add(y);
			verticies.add(z);
		}
	},
	
	backFront = new Axis() 
	{
		@Override
		public float zOff(int delta)
		{
			return 0;
		}
		
		@Override
		public float yOff(int delta)
		{
			return 0;
		}
		
		@Override
		public float xOff(int delta)
		{
			return 0.5f + (delta * 0.5f);
		}
		
		@Override
		public BlockSide side(int delta)
		{
			return delta < 0 ? BlockSide.BACK : BlockSide.FRONT;
		}
		
		@Override
		public IHasModel nextModel(BulkModel m, int x, int y, int z, int delta,
				BulkModel left, BulkModel right, BulkModel top, 
				BulkModel bottom, BulkModel front, BulkModel back)
		{
			return getModelAt(m, x + delta, y, z);
		}
		
		@Override
		public IHasModel getModelAt(BulkModel m, int x, int y, int z)
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
			
			verticies.add(p.z + p.width);
			verticies.add(p.y + p.height);
			verticies.add(p.x);
			
			verticies.add(p.z + p.width);
			verticies.add(p.y);
			verticies.add(p.x);
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
			return 0;
		}
		
		@Override
		public float xOff(int delta)
		{
			return 0;
		}
		
		@Override
		public BlockSide side(int delta)
		{
			return delta < 0 ? BlockSide.LEFT : BlockSide.RIGHT;
		}
		
		@Override
		public IHasModel nextModel(BulkModel m, int x, int y, int z, int delta,
				BulkModel left, BulkModel right, BulkModel top, 
				BulkModel bottom, BulkModel front, BulkModel back)
		{
			if(!m.within(z + delta, y, x))
			{
				if(delta == -1)
				{
					if(left != null)
					{
						return left.cubes[x][y][left.cubes[0][0].length - 1];
					}
					else
						return null;
				}
				else if(delta == 1)
				{
					if(right != null)
					{
						return right.cubes[x][y][0];
					}
					else
						return null;
				}
				else
					throw new IllegalArgumentException("Delta must be -1 or 1!");
			}
			else
				return getModelAt(m, x, y, z + delta);
		}
		
		@Override
		public IHasModel getModelAt(BulkModel m, int x, int y, int z)
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
	
	public BulkModel(IHasModel[][][] models)
	{
		cubes = models;
	}
	
	boolean within(int x, int y, int z)
	{
		return z >= 0 && z < cubes.length
				&& y >= 0 && y < cubes[z].length
				&& x >= 0 && x < cubes[z][y].length;
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
		IHasModel getModelAt(BulkModel m, int x, int y, int z);
		IHasModel nextModel(BulkModel m, int x, int y, int z, int delta,
				BulkModel left, BulkModel right, BulkModel top, 
				BulkModel bottom, BulkModel front, BulkModel back);
		BlockSide side(int delta);
		
		float xOff(int delta);
		float yOff(int delta);
		float zOff(int delta);
		
		void addPlane(List<Float> verticies, Plane p);
	}
	
	private Plane handlePlane(int x, int y, int z, Axis axis, int delta, Plane plane,
			BulkModel left, BulkModel right, BulkModel top, 
			BulkModel bottom, BulkModel front, BulkModel back)
	{
		IHasModel model = axis.getModelAt(this, x, y, z);
		
		if(model != null)
		{
			IHasModel nextModel = axis.nextModel(this, x, y, z, delta, left, right, top, bottom, front, back);
			
			if(plane == null && nextModel == null)
			{
				plane = new Plane(
						x + axis.xOff(delta), 
						y + axis.yOff(delta), 
						z + axis.zOff(delta), 
						model.model(), axis.side(delta));
				plane.width = 0;
				plane.height = 1;
			}
			else if(plane != null && nextModel != null)
			{
				axis.addPlane(verticies, plane);
				maxIndex = indiciesAndUvs(plane, maxIndex);
				
				plane = null;
			}
			else if(plane != null && !Utils.equals(plane.model, model))
			{
				axis.addPlane(verticies, plane);
				maxIndex = indiciesAndUvs(plane, maxIndex);
				
				plane = new Plane(
						x + axis.xOff(delta), 
						y + axis.yOff(delta), 
						z + axis.zOff(delta), 
						model.model(), axis.side(delta));
				plane.width = 0;
				plane.height = 1;
			}
		}
		else if(plane != null)
		{
			axis.addPlane(verticies, plane);
			maxIndex = indiciesAndUvs(plane, maxIndex);
			plane = null;
		}
		
		if(plane != null)
			plane.width++;
		
		return plane;
	}
	
	public void renderAxis(Axis axis,
			BulkModel left, BulkModel right, BulkModel top, 
			BulkModel bottom, BulkModel front, BulkModel back)
	{
		for(int z = 0; z < axis.ZLEN(this); z++)
		{
			for(int y = 0; y < axis.YLEN(this); y++)
			{
				Plane positivePlane = null, negativePlane = null;
				
				for(int x = 0; x < axis.XLEN(this); x++)
				{
					positivePlane = handlePlane(x, y, z, axis, 1, positivePlane, left, right, top, bottom, front, back);
					negativePlane = handlePlane(x, y, z, axis, -1, negativePlane, left, right, top, bottom, front, back);
				}
				
				if(positivePlane != null)
				{
					axis.addPlane(verticies, positivePlane);
					maxIndex = indiciesAndUvs(positivePlane, maxIndex);
					positivePlane = null;
				}
				
				if(negativePlane != null)
				{
					axis.addPlane(verticies, negativePlane);
					maxIndex = indiciesAndUvs(negativePlane, maxIndex);
					negativePlane = null;
				}
			}
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
	 * "Greedy meshing algorithm" kinda
	 */
	void render()
	{
		render(null, null, null, null, null, null);
	}
	
	/**
	 * "Greedy meshing algorithm" kinda
	 */
	void render(BulkModel left, BulkModel right, BulkModel top, 
			BulkModel bottom, BulkModel front, BulkModel back)
	{
		verticies.clear();
		indicies.clear();
		uvs.clear();
		
		maxIndex = 0;
		
		renderAxis(botTop, left, right, top, bottom, front, back);
		renderAxis(backFront, left, right, top, bottom, front, back);
		renderAxis(leftRight, left, right, top, bottom, front, back);
		
		int i = 0;
		int[] indiciesArr = new int[indicies.size()];
		for(int index : indicies)
			indiciesArr[i++] = index;

		i = 0;
		float[] verticiesArr = new float[verticies.size()];
		
//		float dz = cubes.length / 2.0f;
//		float dy = cubes[(int)dz].length / 2.0f;
//		float dx = cubes[(int)dz][(int)dy].length / 2.0f;
		
		// verticies must be in the order of x,y,z
		for(float vertex : verticies)
			verticiesArr[i++] = vertex;// - (i % 3 == 0 ? dx : (i % 3 == 1 ? dy : dz)); // centers everything around the center of the bulk model's 0,0
		
		i = 0;
		float[] uvsArr = new float[uvs.size()];
		for(float uv : uvs)
			uvsArr[i++] = uv;
		
		combinedModel = Mesh.createMesh(verticiesArr, indiciesArr, uvsArr);
	}
	
	public Mesh mesh()
	{
		return combinedModel;
	}
}

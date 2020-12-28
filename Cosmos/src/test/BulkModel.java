package test;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.joml.Vector3fc;
import org.joml.Vector3i;
import org.joml.Vector3ic;

import com.cornchipss.utils.Maths;
import com.cornchipss.utils.Utils;

import test.lights.LightSource;
import test.models.BlockSide;
import test.models.CubeModel;
import test.models.IHasModel;

public class BulkModel
{
	private boolean renderedOnce = false;
	
	private IHasModel[][][] cubes;
	
	private Map<Vector3i, LightSource> lightSources = new HashMap<>();

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
		public Vector3ic nextCoord(Plane p, Axis axis, int delta)
		{
			return new Vector3i((int)p.x, (int)p.z * delta, (int)p.y);
//			return new Vector3i(Maths.floor(p.x), Maths.floor(p.z) + delta, Maths.floor(p.y));
		}
		
		@Override
		public IHasModel nextModel(BulkModel m, int x, int y, int z, int delta,
				BulkModel left, BulkModel right, BulkModel top, 
				BulkModel bottom, BulkModel front, BulkModel back)
		{
			if(!m.within(x, z + delta, y))
			{
				if(delta == -1)
				{
					if(bottom != null)
						return bottom.cubes[y][bottom.cubes[0].length - 1][x];
					else
						return null;
				}
				else if(delta == 1)
				{
					if(top != null)
						return top.cubes[y][0][x];
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
			if(!m.within(z, y, x + delta))
			{
				if(delta == -1)
				{
					if(back != null)
						return back.cubes[back.cubes.length - 1][y][z];
					else
						return null;
				}
				else if(delta == 1)
				{
					if(front != null)
						return front.cubes[0][y][z];
					else
						return null;
				}
				else
					throw new IllegalArgumentException("Delta must be -1 or 1!");
			}
			else
				return getModelAt(m, x + delta, y, z);
		}

		@Override
		public Vector3ic nextCoord(Plane p, Axis axis, int delta)
		{
			return new Vector3i(Maths.floor(p.z), Maths.floor(p.y), Maths.floor(p.x) + delta);
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
						return left.cubes[x][y][left.cubes[0][0].length - 1];
					else
						return null;
				}
				else if(delta == 1)
				{
					if(right != null)
						return right.cubes[x][y][0];
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
		public Vector3ic nextCoord(Plane p, Axis axis, int delta)
		{
			return new Vector3i(Maths.floor(p.z) + delta, Maths.floor(p.y), Maths.floor(p.x) + delta);
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
	List<Float> lights = new LinkedList<>();
	
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
		Vector3ic nextCoord(Plane p, Axis axis, int delta);

		BlockSide side(int delta);
		
		float xOff(int delta);
		float yOff(int delta);
		float zOff(int delta);
		
		void addPlane(List<Float> verticies, Plane p);
	}
	
	private Plane handlePlane(int x, int y, int z, Axis axis, int delta, Plane plane,
			BulkModel left, BulkModel right, BulkModel top, 
			BulkModel bottom, BulkModel front, BulkModel back,
			int offX, int offY, int offZ, float[][][] lightMap)
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
				lighting(plane, axis, delta, offX, offY, offZ, lightMap);
				
				plane = null;
			}
			else if(plane != null && !Utils.equals(plane.model, model))
			{
				axis.addPlane(verticies, plane);
				maxIndex = indiciesAndUvs(plane, maxIndex);
				lighting(plane, axis, delta, offX, offY, offZ, lightMap);
				
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
			lighting(plane, axis, delta, offX, offY, offZ, lightMap);
			plane = null;
		}
		
		if(plane != null)
			plane.width++;
		
		return plane;
	}
	
	public void renderAxis(Axis axis,
			BulkModel left, BulkModel right, BulkModel top, 
			BulkModel bottom, BulkModel front, BulkModel back,
			int offX, int offY, int offZ, float[][][] lightMap)
	{
		for(int z = 0; z < axis.ZLEN(this); z++)
		{
			for(int y = 0; y < axis.YLEN(this); y++)
			{
				Plane positivePlane = null, negativePlane = null;
				
				for(int x = 0; x < axis.XLEN(this); x++)
				{
					positivePlane = handlePlane(x, y, z, axis, 1, positivePlane, left, right, top, bottom, front, back, offX, offY, offZ, lightMap);
					negativePlane = handlePlane(x, y, z, axis, -1, negativePlane, left, right, top, bottom, front, back, offX, offY, offZ, lightMap);
				}
				
				if(positivePlane != null)
				{
					axis.addPlane(verticies, positivePlane);
					maxIndex = indiciesAndUvs(positivePlane, maxIndex);
					lighting(positivePlane, axis, 1, offX, offY, offZ, lightMap);
					positivePlane = null;
				}
				
				if(negativePlane != null)
				{
					axis.addPlane(verticies, negativePlane);
					maxIndex = indiciesAndUvs(negativePlane, maxIndex);
					lighting(negativePlane, axis, -1, offX, offY, offZ, lightMap);
					negativePlane = null;
				}
			}
		}
	}
	
	private void lighting(Plane p, Axis axis, int delta, int offX, int offY, int offZ, float[][][] lightMap)
	{
		Vector3ic coord = axis.nextCoord(p, axis, delta);
		
		float col = -1;
		
		if(withinLightmap(offX, offY, offZ, lightMap, coord.x(), coord.y(), coord.z()))
		{
			col = lightMap[offZ + coord.z()][offY + coord.y()][offX + coord.x()];
		}
		
		lights.add(col);
		lights.add(col);
		lights.add(col);
		
		lights.add(col);
		lights.add(col);
		lights.add(col);
		
		lights.add(col);
		lights.add(col);
		lights.add(col);
		
		lights.add(col);
		lights.add(col);
		lights.add(col);
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
	
	public void calculateLightMap(int offX, int offY, int offZ, float[][][] lightMap)
	{
		
		
		for(int z = 0; z < length(); z++)
		{
			for(int y = 0; y < height(); y++)
			{
				for(int x = 0; x < width(); x++)
				{
					lightMap[z + offZ][y + offY][x + offX] = 1.0f;
				}
			}
		}
		
		for(int z = 0; z < length(); z++)
		{
			for(int y = 0; y < height(); y++)
			{
				for(int x = 0; x < width(); x++)
				{
					if(cubes[z][y][x] != null)
						lightMap[z + offZ][y + offY][x + offX] = -1; // marks where the blocks are in the light map
				}
			}
		}
		
//		for(Vector3i pos : lightSources.keySet())
//		{
//			LightSource src = lightSources.get(pos);
//			
//			lightMap[pos.z() + offZ][pos.y() + offY][pos.x() + offX] = 1;
//			
//			doTheThing(src, pos, lightMap, offX, offY, offZ, 1);
//			doTheThing(src, pos, lightMap, offX, offY, offZ, -1);
//			
////			light(offX, offY, offZ, lightMap, pos.x(), pos.y(), pos.z(), src);
//		}
	}
	
	private void doTheThing(LightSource src, Vector3ic pos, float[][][] lightMap, int offX, int offY, int offZ, int dir)
	{
		for(int dz = 0; dz <= src.strength(); dz++)
		{
			for(int dy = 0; dy <= src.strength(); dy++)
			{
				for(int dx = 0; dx <= src.strength(); dx++)
				{
					if(dz == 0 && dy == 0 && dx == 0)
						continue;
					
					int xx = pos.x() + dir * dx + offX;
					int yy = pos.y() + dir * dy + offY;
					int zz = pos.z() + dir * dz + offZ;
					
//					if(withinLightmap(offX, offY, offZ, lightMap, pos.x() + dir * dx, pos.y() + dir * yy, pos.z() + dir * zz))
					{
//						float here = lightMap[zz][yy][xx];
						
						try
						{
							lightMap[zz][yy][xx] = 1;
						}
						catch(ArrayIndexOutOfBoundsException ex)
						{
							
						}
//						if(here != -1)
//						{
//							float max = 0;
//							
//							if(withinLightmap(xx + 1, yy, zz, lightMap))
//								max = Maths.max(lightMap[zz][yy][xx + 1], max);
//							if(withinLightmap(xx - 1, yy, zz, lightMap))
//								max = Maths.max(lightMap[zz][yy][xx - 1], max);
//							
//							if(withinLightmap(xx, yy + 1, zz, lightMap))
//								max = Maths.max(lightMap[zz][yy + 1][xx], max);
//							if(withinLightmap( xx, yy - 1, zz, lightMap))
//								max = Maths.max(lightMap[zz][yy - 1][xx], max);
//							
//							if(withinLightmap(xx, yy, zz + 1, lightMap))
//								max = Maths.max(lightMap[zz + 1][yy][xx], max);
//							if(withinLightmap(xx, yy, zz - 1, lightMap))
//								max = Maths.max(lightMap[zz - 1][yy][xx], max);
//							
//							max -= 1.0f / src.strength();
//							
//							lightMap[pos.z() + offZ][yy][pos.x() + offX] = 100;//Maths.max(max, here);
//						}
					}
				}
			}
		}
	}
	
	private boolean withinLightmap(int offX, int offY, int offZ, float[][][] lightMap, int x, int y, int z)
	{
		return withinLightmap(x + offX, y + offY, z + offZ, lightMap);			
	}
	
	private boolean withinLightmap(int x, int y, int z, float[][][] lightMap)
	{
		return z >= 0 && z < lightMap.length &&
				y >= 0 && y < lightMap[z].length &&
				x >= 0 && x < lightMap[z][y].length;
				
	}
	
	private void light(int offX, int offY, int offZ, float[][][] lightMap, int startX, int startY, int startZ, LightSource src)
	{
		float startingStren = lightMap[startZ + offZ][startY + offY][startX + offX];
		
		if(startingStren <= 1)
			return;
		
		for(int dz = -src.strength(); dz <= src.strength(); dz++)
		{
			for(int dy = -src.strength(); dy <= src.strength(); dy++)
			{
				for(int dx = -src.strength(); dx <= src.strength(); dx++)
				{
					if(!withinLightmap(offX, offY, offZ, lightMap, startX + dx, dy + startY, dz + startZ))
						break;
					
					lightMap[offZ + dz + startZ]
							[offY + dy + startY]
							[offX + dx + startX] = 1.0f;//1.0f - Math.max(Math.max(Math.abs(dx), Math.abs(dy)), Math.abs(dz)) / (float)src.strength();
				}
			}
		}
		
//		for(int y = 1; y < startingStren; y++)
//		{			
//			if(!withinLightmap(offX, offY, offZ, lightMap, startX, y + startY, startZ))
//				break;
//			
//			float here = lightMap[offX + startX][offY + y + startY][offZ + startZ];
//			float stren = (startingStren - y) / (float)src.strength();
//			
//			if(here == -1 || here >= stren)
//				break;
//			
//			lightMap[offZ + startZ][offY + y + startY][offX + startX] = stren;
//		}
//		
//		for(int y = 1; y < startingStren; y++)
//		{
//			if(!withinLightmap(offX, offY, offZ, lightMap, startX, -y + startY, startZ))
//				break;
//			
//			float here = lightMap[offX + startX][offY + -y + startY][offZ + startZ];
//			float stren = (startingStren - y) / (float)src.strength();
//			
//			if(here == -1 || here >= stren)
//				break;
//			
//			lightMap[offZ + startZ][offY + -y + startY][offX + startX] = stren;
//		}
	}
	
	/**
	 * "<s>Greedy meshing</s> algorithm" kinda
	 */
	void render(BulkModel left, BulkModel right, BulkModel top, 
			BulkModel bottom, BulkModel front, BulkModel back,
			int offX, int offY, int offZ, float[][][] lightMap)
	{
		Utils.println(offX + " " + offY + " " + offZ);
		
		verticies.clear();
		indicies.clear();
		uvs.clear();
		
		maxIndex = 0;
		
		renderAxis(botTop, left, right, top, bottom, front, back, offX, offY, offZ, lightMap);
		renderAxis(backFront, left, right, top, bottom, front, back, offX, offY, offZ, lightMap);
		renderAxis(leftRight, left, right, top, bottom, front, back, offX, offY, offZ, lightMap);
		
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
		
		i = 0;
		float[] lightsArr = new float[lights.size()];
		for(float l : lights)
			lightsArr[i++] = l;
		
		combinedModel = Mesh.createMesh(verticiesArr, indiciesArr, uvsArr, lightsArr);
		
		renderedOnce = true;
	}
	
	public void addLight(LightSource light, Vector3fc pos) // relative to chunk's 0,0,0
	{
		int x = Maths.floor(pos.x());
		int y = Maths.floor(pos.y());
		int z = Maths.floor(pos.z());
		
		lightSources.put(new Vector3i(x, y, z), light);
	}
	
	public Mesh mesh()
	{
		return combinedModel;
	}
	
	public int width()
	{
		return cubes[0][0].length;
	}
	
	public int height()
	{
		return cubes[0].length;
	}
	
	public int length()
	{
		return cubes.length;
	}
}

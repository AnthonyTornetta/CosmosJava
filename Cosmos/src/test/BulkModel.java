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
	
	private void computeEverything(BulkModel left, BulkModel right, BulkModel top, 
			BulkModel bottom, BulkModel front, BulkModel back,
			int offX, int offY, int offZ, float[][][] lightMap)
	{
		for(int z = 0; z < length(); z++)
		{
			for(int y = 0; y < height(); y++)
			{
				for(int x = 0; x < width(); x++)
				{
					if(cubes[z][y][x] != null)
					{
						boolean withinB;
						
						if((!(withinB = within(x, y + 1, z)) &&
							(top == null || top.cubes[z][0][x] == null)) 
								|| withinB && cubes[z][y + 1][x] == null)
						{
							float xx = (float)x;
							float yy = (float)y + 1;
							float zz = (float)z;
							
							verticies.add(xx);
							verticies.add(yy);
							verticies.add(zz);
							
							verticies.add(xx);
							verticies.add(yy);
							verticies.add(zz + 1);
							
							verticies.add(xx + 1);
							verticies.add(yy);
							verticies.add(zz + 1);
							
							verticies.add(xx + 1);
							verticies.add(yy);
							verticies.add(zz);
							
							maxIndex = indiciesAndUvs(BlockSide.TOP, cubes[z][y][x].model());
							
							lighting(offX, offY, offZ, x, y + 1, z, lightMap);
						}
						if((!(withinB = within(x, y - 1, z)) &&
								(bottom == null || bottom.cubes[z][bottom.height() - 1][x] == null)) 
									|| withinB && cubes[z][y - 1][x] == null)
						{
							float xx = (float)x;
							float yy = (float)y;
							float zz = (float)z;
							
							verticies.add(xx);
							verticies.add(yy);
							verticies.add(zz);
							
							verticies.add(xx);
							verticies.add(yy);
							verticies.add(zz + 1);
							
							verticies.add(xx + 1);
							verticies.add(yy);
							verticies.add(zz + 1);
							
							verticies.add(xx + 1);
							verticies.add(yy);
							verticies.add(zz);
							
							maxIndex = indiciesAndUvs(BlockSide.BOTTOM, cubes[z][y][x].model());
							
							lighting(offX, offY, offZ, x, y - 1, z, lightMap);
						}
						
						if((!(withinB = within(x, y, z + 1)) &&
								(front == null || front.cubes[0][y][x] == null)) 
									|| withinB && cubes[z + 1][y][x] == null)
						{
							float xx = (float)x;
							float yy = (float)y;
							float zz = (float)z + 1;
							
							verticies.add(xx);
							verticies.add(yy);
							verticies.add(zz);
							
							verticies.add(xx);
							verticies.add(yy + 1);
							verticies.add(zz);
							
							verticies.add(xx + 1);
							verticies.add(yy + 1);
							verticies.add(zz);
							
							verticies.add(xx + 1);
							verticies.add(yy);
							verticies.add(zz);
							
							maxIndex = indiciesAndUvs(BlockSide.FRONT, cubes[z][y][x].model());
							
							lighting(offX, offY, offZ, x, y, z + 1, lightMap);
						}
						if((!(withinB = within(x, y, z - 1)) &&
								(back == null || back.cubes[back.length() - 1][y][x] == null)) 
									|| withinB && cubes[z - 1][y][x] == null)
						{
							float xx = (float)x;
							float yy = (float)y;
							float zz = (float)z;
							
							verticies.add(xx);
							verticies.add(yy);
							verticies.add(zz);
							
							verticies.add(xx);
							verticies.add(yy + 1);
							verticies.add(zz);
							
							verticies.add(xx + 1);
							verticies.add(yy + 1);
							verticies.add(zz);
							
							verticies.add(xx + 1);
							verticies.add(yy);
							verticies.add(zz);
							
							maxIndex = indiciesAndUvs(BlockSide.BACK, cubes[z][y][x].model());
							
							lighting(offX, offY, offZ, x, y, z - 1, lightMap);
						}
						

						if((!(withinB = within(x + 1, y, z)) &&
								(right == null || right.cubes[z][y][0] == null)) 
									|| withinB && cubes[z][y][x + 1] == null)
						{
							float xx = (float)x + 1;
							float yy = (float)y;
							float zz = (float)z;
							
							verticies.add(xx);
							verticies.add(yy);
							verticies.add(zz);
							
							verticies.add(xx);
							verticies.add(yy + 1);
							verticies.add(zz);
							
							verticies.add(xx);
							verticies.add(yy + 1);
							verticies.add(zz + 1);
							
							verticies.add(xx);
							verticies.add(yy);
							verticies.add(zz + 1);
							
							maxIndex = indiciesAndUvs(BlockSide.RIGHT, cubes[z][y][x].model());
							
							lighting(offX, offY, offZ, x + 1, y, z, lightMap);
						}
						if((!(withinB = within(x - 1, y, z)) &&
								(left == null || left.cubes[z][y][left.width() - 1] == null)) 
									|| withinB && cubes[z][y][x - 1] == null)
						{
							float xx = (float)x;
							float yy = (float)y;
							float zz = (float)z;
							
							verticies.add(xx);
							verticies.add(yy);
							verticies.add(zz);
							
							verticies.add(xx);
							verticies.add(yy + 1);
							verticies.add(zz);
							
							verticies.add(xx);
							verticies.add(yy + 1);
							verticies.add(zz + 1);
							
							verticies.add(xx);
							verticies.add(yy);
							verticies.add(zz + 1);
							
							maxIndex = indiciesAndUvs(BlockSide.LEFT, cubes[z][y][x].model());
							
							lighting(offX, offY, offZ, x - 1, y, z, lightMap);
						}
					}
				}
			}
		}
	}
	
	private void lighting(int offX, int offY, int offZ, int x, int y, int z, float[][][] lightMap)
	{
		float col = 0;
		if(withinLightmap(offX, offY, offZ, lightMap, x, y, z))
			col = lightMap[z + offZ][y + offY][x + offX];
		
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
	
	private int indiciesAndUvs(BlockSide side, CubeModel model)
	{
		int[] indiciesArr = model.indicies(side);
		int max = -1;
		
		for(int index : indiciesArr)
		{
			indicies.add(index + maxIndex);
			if(max < index)
				max = index;
		}
		  
		float u = model.u(side);
		float v = model.v(side);
		
		float uEnd = u + CubeModel.TEXTURE_DIMENSIONS;
		float vEnd = v + CubeModel.TEXTURE_DIMENSIONS;
		
		uvs.add(uEnd);
		uvs.add(vEnd);
		
		uvs.add(uEnd);
		uvs.add(v);
		
		uvs.add(u);
		uvs.add(v);

		uvs.add(u);
		uvs.add(vEnd);
		
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
					if(cubes[z][y][x] != null)
						lightMap[z + offZ][y + offY][x + offX] = -1; // marks where the blocks are in the light map
				}
			}
		}
		
		for(Vector3i pos : lightSources.keySet())
		{
			LightSource src = lightSources.get(pos);
			
			lightMap[pos.z() + offZ][pos.y() + offY][pos.x() + offX] = 1.0f;
			
			for(int delta = -1; delta <= 1; delta += 2)
			{
				for(int z = -src.strength(); z <= src.strength(); z++)
				{
					for(int y = -src.strength(); y <= src.strength(); y++)
					{
						for(int x = -src.strength(); x <= src.strength(); x++)
						{
							if(x == 0 && y == 0 && z == 0)
								continue;
							
							int xx = pos.x() + offX + x * delta;
							int yy = pos.y() + offY + y * delta;
							int zz = pos.z() + offZ + z * delta;
							
							if(!withinLightmap(xx, yy, zz, lightMap))
								continue;
							
							if(lightMap[zz][yy][xx] == -1)
								continue;
							
							float highest = 0;
							
							if(withinLightmap(xx - 1, yy, zz, lightMap))
								highest = Maths.max(highest, lightMap[zz][yy][xx - 1]);
							if(withinLightmap(xx + 1, yy, zz, lightMap))
								highest = Maths.max(highest, lightMap[zz][yy][xx + 1]);
							if(withinLightmap(xx, yy - 1, zz, lightMap))
								highest = Maths.max(highest, lightMap[zz][yy - 1][xx]);
							if(withinLightmap(xx, yy + 1, zz, lightMap))
								highest = Maths.max(highest, lightMap[zz][yy + 1][xx]);
							if(withinLightmap(xx, yy, zz - 1, lightMap))
								highest = Maths.max(highest, lightMap[zz - 1][yy][xx]);
							if(withinLightmap(xx, yy, zz + 1, lightMap))
								highest = Maths.max(highest, lightMap[zz + 1][yy][xx]);
							
							if(highest > 0)
								lightMap[zz][yy][xx] = Math.max(lightMap[zz][yy][xx], highest - 1.0f / src.strength());
						}
					}
				}
			}
			
//			doTheThing(src, pos, lightMap, offX, offY, offZ, 1);
//			doTheThing(src, pos, lightMap, offX, offY, offZ, -1);
//			
//			light(offX, offY, offZ, lightMap, pos.x(), pos.y(), pos.z(), src);
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
		
		computeEverything(left, right, top, bottom, front, back, offX, offY, offZ, lightMap);
		
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

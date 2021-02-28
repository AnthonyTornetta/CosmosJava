package com.cornchipss.cosmos;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.cornchipss.cosmos.blocks.BlockFace;
import com.cornchipss.cosmos.lights.LightMap;
import com.cornchipss.cosmos.material.Material;
import com.cornchipss.cosmos.models.CubeModel;
import com.cornchipss.cosmos.models.IHasModel;
import com.cornchipss.cosmos.rendering.MaterialMesh;

public class BulkModel
{
	private IHasModel[][][] cubes;
	
	public void setModels(IHasModel[][][] blocks)
	{
		this.cubes = blocks;
	}	
	
	private static class MaterialMeshGenerator
	{
		List<Integer> indicies = new LinkedList<>();
		List<Float> verticies = new LinkedList<>();
		List<Float> uvs = new LinkedList<>();
		List<Float> lights = new LinkedList<>();
		int maxIndex = 0;
	}
	
	private List<MaterialMesh> meshes;
	
	private Map<Material, MaterialMeshGenerator> indevMeshes;
	
	public BulkModel(IHasModel[][][] models)
	{
		cubes = models;
		
		meshes = new LinkedList<>();
		
		indevMeshes = new HashMap<>();
	}
	
	boolean within(int x, int y, int z)
	{
		return z >= 0 && z < cubes.length
				&& y >= 0 && y < cubes[z].length
				&& x >= 0 && x < cubes[z][y].length;
	}
	
	private void computeEverything(BulkModel left, BulkModel right, BulkModel top, 
			BulkModel bottom, BulkModel front, BulkModel back,
			int offX, int offY, int offZ, LightMap lightMap)
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
						
						Material mat = cubes[z][y][x].model().material();
						
						if(!indevMeshes.containsKey(mat))
						{
							indevMeshes.put(mat, new MaterialMeshGenerator());
						}
						
						MaterialMeshGenerator matMesh = indevMeshes.get(mat);
						
						if((!(withinB = within(x, y + 1, z)) &&
							(top == null || top.cubes[z][0][x] == null)) 
								|| withinB && cubes[z][y + 1][x] == null)
						{
							for(float f : cubes[z][y][x].model().verticies(BlockFace.TOP, x, y, z))
								matMesh.verticies.add(f);
							
							matMesh.maxIndex = indiciesAndUvs(BlockFace.TOP, cubes[z][y][x].model(), matMesh);
							
							lighting(offX, offY, offZ, x, y + 1, z, lightMap, matMesh);
						}
						if((!(withinB = within(x, y - 1, z)) &&
								(bottom == null || bottom.cubes[z][bottom.height() - 1][x] == null)) 
									|| withinB && cubes[z][y - 1][x] == null)
						{
							for(float f : cubes[z][y][x].model().verticies(BlockFace.BOTTOM, x, y, z))
								matMesh.verticies.add(f);
							
							matMesh.maxIndex = indiciesAndUvs(BlockFace.BOTTOM, cubes[z][y][x].model(), matMesh);
							
							lighting(offX, offY, offZ, x, y - 1, z, lightMap, matMesh);
						}
						
						if((!(withinB = within(x, y, z + 1)) &&
								(front == null || front.cubes[0][y][x] == null)) 
									|| withinB && cubes[z + 1][y][x] == null)
						{
							for(float f : cubes[z][y][x].model().verticies(BlockFace.FRONT, x, y, z))
								matMesh.verticies.add(f);
							
							matMesh.maxIndex = indiciesAndUvs(BlockFace.FRONT, cubes[z][y][x].model(), matMesh);
							
							lighting(offX, offY, offZ, x, y, z + 1, lightMap, matMesh);
						}
						if((!(withinB = within(x, y, z - 1)) &&
								(back == null || back.cubes[back.length() - 1][y][x] == null)) 
									|| withinB && cubes[z - 1][y][x] == null)
						{
							for(float f : cubes[z][y][x].model().verticies(BlockFace.BACK, x, y, z))
								matMesh.verticies.add(f);
							
							matMesh.maxIndex = indiciesAndUvs(BlockFace.BACK, cubes[z][y][x].model(), matMesh);
							
							lighting(offX, offY, offZ, x, y, z - 1, lightMap, matMesh);
						}
						

						if((!(withinB = within(x + 1, y, z)) &&
								(right == null || right.cubes[z][y][0] == null)) 
									|| withinB && cubes[z][y][x + 1] == null)
						{
							for(float f : cubes[z][y][x].model().verticies(BlockFace.RIGHT, x, y, z))
								matMesh.verticies.add(f);
							
							matMesh.maxIndex = indiciesAndUvs(BlockFace.RIGHT, cubes[z][y][x].model(), matMesh);
							
							lighting(offX, offY, offZ, x + 1, y, z, lightMap, matMesh);
						}
						if((!(withinB = within(x - 1, y, z)) &&
								(left == null || left.cubes[z][y][left.width() - 1] == null)) 
									|| withinB && cubes[z][y][x - 1] == null)
						{
							for(float f : cubes[z][y][x].model().verticies(BlockFace.LEFT, x, y, z))
								matMesh.verticies.add(f);
							
							matMesh.maxIndex = indiciesAndUvs(BlockFace.LEFT, cubes[z][y][x].model(), matMesh);
							
							lighting(offX, offY, offZ, x - 1, y, z, lightMap, matMesh);
						}
					}
				}
			}
		}
	}
	
	private void lighting(int offX, int offY, int offZ, int x, int y, int z, LightMap lightMap, MaterialMeshGenerator matMesh)
	{
		float col = 0;
		if(lightMap.within(offX + x, offY + y, offZ + z))
			col = lightMap.at(x, y, z, offX, offY, offZ);
		
		matMesh.lights.add(col);
		matMesh.lights.add(col);
		matMesh.lights.add(col);
		
		matMesh.lights.add(col);
		matMesh.lights.add(col);
		matMesh.lights.add(col);
		
		matMesh.lights.add(col);
		matMesh.lights.add(col);
		matMesh.lights.add(col);
		
		matMesh.lights.add(col);
		matMesh.lights.add(col);
		matMesh.lights.add(col);
	}
	
	private int indiciesAndUvs(BlockFace side, CubeModel model, MaterialMeshGenerator matMesh)
	{
		int[] indiciesArr = model.indicies(side);
		int max = -1;
		
		for(int index : indiciesArr)
		{
			matMesh.indicies.add(index + matMesh.maxIndex);
			if(max < index)
				max = index;
		}
		  
		float u = model.u(side);
		float v = model.v(side);
		
		float uEnd = u + CubeModel.TEXTURE_DIMENSIONS;
		float vEnd = v + CubeModel.TEXTURE_DIMENSIONS;
		
		matMesh.uvs.add(uEnd);
		matMesh.uvs.add(vEnd);
		
		matMesh.uvs.add(uEnd);
		matMesh.uvs.add(v);
		
		matMesh.uvs.add(u);
		matMesh.uvs.add(v);

		matMesh.uvs.add(u);
		matMesh.uvs.add(vEnd);
		
		return matMesh.maxIndex + max + 1;
	}
	
	/**
	 * algorithm kinda
	 */
	void render(BulkModel left, BulkModel right, BulkModel top, 
			BulkModel bottom, BulkModel front, BulkModel back,
			int offX, int offY, int offZ, LightMap lightMap)
	{
		indevMeshes.clear();
		meshes.clear();
		
		computeEverything(left, right, top, bottom, front, back, offX, offY, offZ, lightMap);
		
		for(Material m : indevMeshes.keySet())
		{
			MaterialMeshGenerator matMesh = indevMeshes.get(m);
			
			int i = 0;
			int[] indiciesArr = new int[matMesh.indicies.size()];
			for(int index : matMesh.indicies)
				indiciesArr[i++] = index;
			
			i = 0;
			float[] verticiesArr = new float[matMesh.verticies.size()];
			
	//		float dz = cubes.length / 2.0f;
	//		float dy = cubes[(int)dz].length / 2.0f;
	//		float dx = cubes[(int)dz][(int)dy].length / 2.0f;
			
			// verticies must be in the order of x,y,z
			for(float vertex : matMesh.verticies)
				verticiesArr[i++] = vertex;// - (i % 3 == 0 ? dx : (i % 3 == 1 ? dy : dz)); // centers everything around the center of the bulk model's 0,0
			
			i = 0;
			float[] uvsArr = new float[matMesh.uvs.size()];
			for(float uv : matMesh.uvs)
				uvsArr[i++] = uv;
			
			i = 0;
			float[] lightsArr = new float[matMesh.lights.size()];
			for(float l : matMesh.lights)
				lightsArr[i++] = l;
			
			meshes.add(new MaterialMesh(m,
					Mesh.createMesh(verticiesArr, indiciesArr, uvsArr, lightsArr)));
		}
	}
	
	public List<MaterialMesh> materialMeshes()
	{
		return meshes;
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

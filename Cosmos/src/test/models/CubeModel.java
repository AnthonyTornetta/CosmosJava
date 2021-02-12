package test.models;

import java.util.LinkedList;
import java.util.List;

import test.Mesh;

public abstract class CubeModel
{
	public static final float TEXTURE_DIMENSIONS = 16.0f / 256.0f;
	
	public static int[] sideIndicies = new int[]
			{
					0, 1, 2,
					2, 3, 0
			};
	
	public abstract float u(BlockSide side);
	public abstract float v(BlockSide side);
	
	public int[] indicies(BlockSide side)
	{
		return sideIndicies;
	}
	
	private static float[] tempReturn = new float[3*4];
	
	public float[] verticies(BlockSide side, float offX, float offY, float offZ)
	{
		switch(side)
		{
			case TOP:
			{
				tempReturn[0] = offX;
				tempReturn[1] = offY + 1;
				tempReturn[2] = offZ;
				
				tempReturn[3] = offX;
				tempReturn[4] = offY + 1;
				tempReturn[5] = offZ + 1;
				
				tempReturn[6] = offX + 1;
				tempReturn[7] = offY + 1;
				tempReturn[8] = offZ + 1;
				
				tempReturn[9] = offX + 1;
				tempReturn[10]= offY + 1;
				tempReturn[11]= offZ;
				return tempReturn;
			}
			case BOTTOM:
			{
				tempReturn[0] = offX;
				tempReturn[1] = offY;
				tempReturn[2] = offZ;
				
				tempReturn[3] = offX;
				tempReturn[4] = offY;
				tempReturn[5] = offZ + 1;
				
				tempReturn[6] = offX + 1;
				tempReturn[7] = offY;
				tempReturn[8] = offZ + 1;
				
				tempReturn[9] = offX + 1;
				tempReturn[10]= offY;
				tempReturn[11]= offZ;
				
				return tempReturn;
			}
			case FRONT:
			{
				tempReturn[0] = offX;
				tempReturn[1] = offY;
				tempReturn[2] = offZ + 1;

				tempReturn[3] = offX;
				tempReturn[4] = offY + 1;
				tempReturn[5] = offZ + 1;

				tempReturn[6] = offX + 1;
				tempReturn[7] = offY + 1;
				tempReturn[8] = offZ + 1;

				tempReturn[9] = offX + 1;
				tempReturn[10]= offY;
				tempReturn[11]= offZ + 1;
				
				return tempReturn;
			}
			case BACK:
			{
				tempReturn[0] = offX;
				tempReturn[1] = offY;
				tempReturn[2] = offZ;

				tempReturn[3] = offX;
				tempReturn[4] = offY + 1;
				tempReturn[5] = offZ;

				tempReturn[6] = offX + 1;
				tempReturn[7] = offY + 1;
				tempReturn[8] = offZ;

				tempReturn[9] = offX + 1;
				tempReturn[10]= offY;
				tempReturn[11]= offZ;
				
				return tempReturn;
			}
			case RIGHT:
			{
				tempReturn[0] = offX + 1;
				tempReturn[1] = offY;
				tempReturn[2] = offZ;

				tempReturn[3] = offX + 1;
				tempReturn[4] = offY + 1;
				tempReturn[5] = offZ;

				tempReturn[6] = offX + 1;
				tempReturn[7] = offY + 1;
				tempReturn[8] = offZ + 1;

				tempReturn[9] = offX + 1;
				tempReturn[10]= offY;
				tempReturn[11]= offZ + 1;
				
				return tempReturn;
			}
			case LEFT:
			{
				tempReturn[0] = offX;
				tempReturn[1] = offY;
				tempReturn[2] = offZ;

				tempReturn[3] = offX;
				tempReturn[4] = offY + 1;
				tempReturn[5] = offZ;

				tempReturn[6] = offX;
				tempReturn[7] = offY + 1;
				tempReturn[8] = offZ + 1;

				tempReturn[9] = offX;
				tempReturn[10]= offY;
				tempReturn[11]= offZ + 1;
				
				return tempReturn;
			}
			default:
				return null;
		}
	}
	
	public Mesh createMesh(float offX, float offY, float offZ, float scale, BlockSide... sides)
	{
		if(sides.length == 0)
			sides = new BlockSide[] { 
					BlockSide.RIGHT, BlockSide.LEFT,
					BlockSide.BOTTOM, BlockSide.TOP,
					BlockSide.FRONT, BlockSide.BACK};
		
		List<Float> verts = new LinkedList<>();
		List<Integer> indicies = new LinkedList<>();
		List<Float> uvs = new LinkedList<>();
		
		int maxI = 0;
		
		for(BlockSide s : sides)
		{
			for(float f : verticies(s, offX, offY, offZ))
				verts.add(f * scale);
			
			int tempMax = maxI;
			
			for(int i : indicies(s))
			{
				indicies.add(maxI + i);
				if(i + maxI > tempMax - 1)
					tempMax = maxI + i + 1;
			}
			
			maxI = tempMax;
			
			float u = u(s);
			float v = v(s);
			
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
		}
		
		float[] asArrVerts = new float[verts.size()];
		int i = 0;
		for(float f : verts)
		{
			asArrVerts[i++] = f;
		}
		
		int[] asArrIndicies = new int[indicies.size()];
		i = 0;
		for(int index : indicies)
		{
			asArrIndicies[i++] = index;
		}
		
		float[] asArrUvs = new float[uvs.size()];
		i = 0;
		for(float uv : uvs)
		{
			asArrUvs[i++] = uv;
		}
		
		return Mesh.createMesh(asArrVerts, asArrIndicies, asArrUvs);
	}
	
	public boolean opaque()
	{
		return true;
	}
}

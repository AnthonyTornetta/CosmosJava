package com.cornchipss.cosmos.models;

import java.util.LinkedList;
import java.util.List;

import org.joml.Vector3f;

import com.cornchipss.cosmos.blocks.BlockFace;
import com.cornchipss.cosmos.material.TexturedMaterial;
import com.cornchipss.cosmos.material.Materials;
import com.cornchipss.cosmos.rendering.Mesh;
import com.cornchipss.cosmos.utils.Utils;

public abstract class CubeModel implements Model
{
	public static int[] sideIndicies = new int[] { 0, 1, 2, 2, 3, 0 };

	public abstract float u(BlockFace side);

	public abstract float v(BlockFace side);

	public float maxU(BlockFace side)
	{
		return u(side) + material().uLength();
	}

	public float maxV(BlockFace side)
	{
		return v(side) + material().vLength();
	}

	public int[] indicies(BlockFace side)
	{
		return sideIndicies;
	}

	private static float[] tempReturn = new float[3 * 4];

	public float[] verticies(BlockFace side, float offX, float offY, float offZ)
	{
		switch (side)
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
				tempReturn[10] = offY + 1;
				tempReturn[11] = offZ;
				return tempReturn;
			}
			case BOTTOM:
			{
				tempReturn[0] = offX;
				tempReturn[1] = offY;
				tempReturn[2] = offZ + 1;

				tempReturn[3] = offX;
				tempReturn[4] = offY;
				tempReturn[5] = offZ;

				tempReturn[6] = offX + 1;
				tempReturn[7] = offY;
				tempReturn[8] = offZ;

				tempReturn[9] = offX + 1;
				tempReturn[10] = offY;
				tempReturn[11] = offZ + 1;

				return tempReturn;
			}
			case FRONT:
			{
				tempReturn[0] = offX + 1;
				tempReturn[1] = offY;
				tempReturn[2] = offZ + 1;

				tempReturn[3] = offX + 1;
				tempReturn[4] = offY + 1;
				tempReturn[5] = offZ + 1;

				tempReturn[6] = offX;
				tempReturn[7] = offY + 1;
				tempReturn[8] = offZ + 1;

				tempReturn[9] = offX;
				tempReturn[10] = offY;
				tempReturn[11] = offZ + 1;

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
				tempReturn[10] = offY;
				tempReturn[11] = offZ;

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
				tempReturn[10] = offY;
				tempReturn[11] = offZ + 1;

				return tempReturn;
			}
			case LEFT:
			{
				tempReturn[0] = offX;
				tempReturn[1] = offY + 1;
				tempReturn[2] = offZ;

				tempReturn[3] = offX;
				tempReturn[4] = offY;
				tempReturn[5] = offZ;

				tempReturn[6] = offX;
				tempReturn[7] = offY;
				tempReturn[8] = offZ + 1;

				tempReturn[9] = offX;
				tempReturn[10] = offY + 1;
				tempReturn[11] = offZ + 1;

				return tempReturn;
			}
			default:
				return null;
		}
	}

	@Override
	public Mesh createMesh(float offX, float offY, float offZ, float scaleX,
		float scaleY, float scaleZ)
	{
		return createMesh(offX, offY, offZ, scaleX, scaleY, scaleZ,
			BlockFace.RIGHT, BlockFace.LEFT, BlockFace.BOTTOM, BlockFace.TOP,
			BlockFace.FRONT, BlockFace.BACK);
	}

	@Override
	public Mesh createMesh(float offX, float offY, float offZ, float scale)
	{
		return createMesh(offX, offY, offZ, scale, scale, scale);
	}

	protected void fillVertices(List<Float> vertices, float offX, float offY,
		float offZ, float scaleX, float scaleY, float scaleZ,
		BlockFace... sides)
	{
		Vector3f scales = new Vector3f(scaleX, scaleY, scaleZ);

		for (BlockFace s : sides)
		{
			int i = 0;
			for (float f : verticies(s, offX, offY, offZ))
			{
				vertices.add(f * scales.get(i % 3));
				i++;
			}
		}
	}

	protected void fillIndices(List<Integer> indices, float offX, float offY,
		float offZ, BlockFace... sides)
	{
		int maxI = 0;

		for (BlockFace s : sides)
		{
			int tempMax = maxI;

			for (int i : indicies(s))
			{
				indices.add(maxI + i);
				if (i + maxI > tempMax - 1)
					tempMax = maxI + i + 1;
			}

			maxI = tempMax;
		}
	}

	protected void fillUVs(List<Float> uvs, float offX, float offY, float offZ,
		BlockFace... sides)
	{
		for (BlockFace s : sides)
		{
			float u = u(s);
			float v = v(s);

			float uEnd = maxU(s);
			float vEnd = maxV(s);

			uvs.add(uEnd);
			uvs.add(vEnd);

			uvs.add(uEnd);
			uvs.add(v);

			uvs.add(u);
			uvs.add(v);

			uvs.add(u);
			uvs.add(vEnd);
		}
	}

	public Mesh createMesh(float offX, float offY, float offZ, float scaleX,
		float scaleY, float scaleZ, BlockFace... sides)
	{
		return createMesh(true, offX, offY, offZ, scaleX, scaleY, scaleZ,
			sides);
	}

	public Mesh createMesh(float offX, float offY, float offZ, float scale,
		BlockFace... sides)
	{
		return createMesh(true, offX, offY, offZ, scale, sides);
	}

	public Mesh createMesh(boolean unbind, float offX, float offY, float offZ,
		float scaleX, float scaleY, float scaleZ, BlockFace... sides)
	{
		List<Float> verts = new LinkedList<>();
		List<Integer> indices = new LinkedList<>();
		List<Float> uvs = new LinkedList<>();

		fillVertices(verts, offX, offY, offZ, scaleX, scaleY, scaleZ, sides);
		fillIndices(indices, offX, offY, offZ, sides);
		fillUVs(uvs, offX, offY, offZ, sides);

		float[] asArrVerts = Utils.toArray(verts);
		int[] asArrIndicies = Utils.toArrayInt(indices);
		float[] asArrUvs = Utils.toArray(uvs);

		return Mesh.createMesh(asArrVerts, asArrIndicies, asArrUvs, unbind);
	}

	public Mesh createMesh(boolean unbind, float offX, float offY, float offZ,
		float scale, BlockFace... sides)
	{
		return createMesh(unbind, offX, offY, offZ, scale, scale, scale, sides);
	}

	public boolean opaque()
	{
		return true;
	}

	public TexturedMaterial material()
	{
		return Materials.DEFAULT_MATERIAL;
	}
}

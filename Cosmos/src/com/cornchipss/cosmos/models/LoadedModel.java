package com.cornchipss.cosmos.models;

import java.util.Map;

import org.joml.Vector2i;

import com.cornchipss.cosmos.rendering.Mesh;

public class LoadedModel implements Model
{
	private float[] verts, uvs;
	private int[] indices;

	private float[] tempVerts;

	private Map<String, Vector2i> subcomponents;

	public LoadedModel(float[] verts, float[] uvs, int[] indices, Map<String, Vector2i> components)
	{
		this.verts = verts;
		this.uvs = uvs;
		this.indices = indices;
		tempVerts = new float[verts.length];

		subcomponents = components;
	}

	public float[] vertices()
	{
		return verts;
	}

	public float[] uvs()
	{
		return uvs;
	}

	public int[] indices()
	{
		return indices;
	}

	@Override
	public Mesh createMesh(float offX, float offY, float offZ, float scaleX, float scaleY, float scaleZ)
	{
		return createMesh(offX, offY, offZ, scaleX, scaleY, scaleZ, true);
	}
	
	public Mesh createMesh(float offX, float offY, float offZ, float scaleX, float scaleY, float scaleZ, boolean unbind)
	{
		if (offX == 0 && offY == 0 && offZ == 0 && scaleX == 1 && scaleY == 1 && scaleZ == 1)
			return Mesh.createMesh(verts, indices, uvs, unbind);
		else
		{
			for (int i = 0; i < verts.length; i += 3)
			{
				tempVerts[i] = offX + scaleX * verts[i];
				tempVerts[i + 1] = offY + scaleY * verts[i + 1];
				tempVerts[i + 2] = offZ + scaleZ * verts[i + 2];
			}

			return Mesh.createMesh(tempVerts, indices, uvs, unbind);
		}
	}
	
	@Override
	public Mesh createMesh(float offX, float offY, float offZ, float scale)
	{
		return createMesh(offX, offY, offZ, scale, scale, scale);
	}

	public String groupContaining(int index)
	{
		for (String s : subcomponents.keySet())
		{
			Vector2i range = subcomponents.get(s);
			if (range.x <= index && range.y >= index)
			{
				return s;
			}
		}

		return null;
	}

	public int[] indicesForGroup(String group)
	{
		if (!subcomponents.containsKey(group))
			return null;

		int rangeLow = subcomponents.get(group).x;
		int rangeHigh = subcomponents.get(group).y;

		int[] ret = new int[rangeHigh - rangeLow + 1];

		for (int i = rangeLow; i <= rangeHigh; i++)
		{
			ret[i - rangeLow] = indices[i];
		}

		return ret;
	}
}

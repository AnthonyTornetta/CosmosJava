package com.cornchipss.cosmos.models;

import com.cornchipss.cosmos.rendering.Mesh;

public class LoadedModel implements Model
{
	private float[] verts, uvs;
	private int[] indices;
	
	private float[] tempVerts;
	
	public LoadedModel(float[] verts, float[] uvs, int[] indices)
	{
		this.verts = verts;
		this.uvs = uvs;
		this.indices = indices;
		
		tempVerts = new float[verts.length];
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
	public Mesh createMesh(float offX, float offY, float offZ, float scale)
	{
		if(offX == 0 && offY == 0 && offZ == 0 && scale == 1)
			return Mesh.createMesh(verts, indices, uvs);
		else
		{
			for(int i = 0; i < verts.length; i += 3)
			{
				tempVerts[i] = offX + scale * verts[i];
				tempVerts[i+1] = offY + scale * verts[i+1];
				tempVerts[i+2] = offZ + scale * verts[i+2];
			}
			
			return Mesh.createMesh(tempVerts, indices, uvs);
		}
	}
}

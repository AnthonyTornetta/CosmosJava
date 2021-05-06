package com.cornchipss.cosmos.gui;

import org.joml.Vector3fc;

import com.cornchipss.cosmos.material.Material;
import com.cornchipss.cosmos.material.Materials;
import com.cornchipss.cosmos.rendering.Mesh;

public class GUITexture extends GUIElement
{
	public static final int[] indices = new int[]
			{
				0, 1, 3,
				1, 2, 3
			};
	
	public static float[] makeVerts(float w, float h)
	{
		return new float[]
			{
				 w,  h, 0,  // top right
				 w,  0, 0,  // bottom right
			     0,  0, 0,  // bottom left
			     0,  h, 0   // top left 
			};
	}
	
	public static float[] makeUVs(float u, float v, float uWidth, float uHeight)
	{
		return new float[]
			{
				u + uWidth, v,
				u + uWidth, v + uHeight,
				u, v + uHeight,
				u, v
			};
	}
	
	private Mesh guiMesh;
	private Material material;
	
	public GUITexture(Vector3fc position, float w, float h, float u, float v)
	{
		this(position, w, h, u, v, Materials.GUI_MATERIAL);
	}
	
	public GUITexture(Vector3fc position, float w, float h, float u, float v, Material material)
	{
		super(position);
		guiMesh = Mesh.createMesh(makeVerts(w, h), indices, makeUVs(u, v, material.uvWidth(), material.uvHeight()));
		
		this.material = material;
	}
	
	@Override
	public Material material()
	{
		return material;
	}
	
	@Override
	public Mesh guiMesh()
	{
		return guiMesh;
	}
}

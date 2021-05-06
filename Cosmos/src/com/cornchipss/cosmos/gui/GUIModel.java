package com.cornchipss.cosmos.gui;

import org.joml.Vector3fc;

import com.cornchipss.cosmos.blocks.BlockFace;
import com.cornchipss.cosmos.material.Material;
import com.cornchipss.cosmos.models.CubeModel;
import com.cornchipss.cosmos.rendering.Mesh;

public class GUIModel extends GUIElement
{
	private Mesh mesh;
	private Material mat;
	
	public GUIModel(Vector3fc position, float scale, CubeModel model)
	{
		this(position, scale, 
				model.createMesh(0, 0, -1, 1, BlockFace.FRONT), 
				model.material());
	}
	
	public GUIModel(Vector3fc position, float scale, Mesh m, Material mat)
	{
		super(position, 0, 0, 0, scale);
		
		this.mesh = m;
		this.mat = mat;
	}

	@Override
	public Mesh guiMesh()
	{
		return mesh;
	}
	
	@Override
	public Material material()
	{
		return mat;
	}
}

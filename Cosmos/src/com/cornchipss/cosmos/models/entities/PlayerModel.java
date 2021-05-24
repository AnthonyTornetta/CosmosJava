package com.cornchipss.cosmos.models.entities;

import java.io.IOException;

import com.cornchipss.cosmos.material.Material;
import com.cornchipss.cosmos.models.LoadedModel;
import com.cornchipss.cosmos.models.Model;
import com.cornchipss.cosmos.models.ModelLoader;
import com.cornchipss.cosmos.rendering.Mesh;

public class PlayerModel implements Model
{
	private static LoadedModel baseModel;
	private Material material;
	
	public PlayerModel(Material material)
	{
		this.material = material;
	}
	
	public Mesh createMesh(float offX, float offY, float offZ, float scale)
	{
		if(baseModel == null)
		{
			try
			{
				baseModel = ModelLoader.fromFile("assets/models/player");
				
//				ModelLoader.toFile("assets/models/player-min.model", 
//						baseModel.vertices(), baseModel.uvs(), baseModel.indices());
			}
			catch (IOException e)
			{
				throw new RuntimeException(e);
			}
		}
		
		return baseModel.createMesh(offX, offY, offZ, scale);
	}

	public Material material()
	{
		return material;
	}
}

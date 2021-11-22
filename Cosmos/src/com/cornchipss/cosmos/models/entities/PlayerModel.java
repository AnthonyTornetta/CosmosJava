package com.cornchipss.cosmos.models.entities;

import java.io.IOException;

import com.cornchipss.cosmos.material.TexturedMaterial;
import com.cornchipss.cosmos.models.LoadedModel;
import com.cornchipss.cosmos.models.Model;
import com.cornchipss.cosmos.models.ModelLoader;
import com.cornchipss.cosmos.rendering.Mesh;

public class PlayerModel implements Model
{
	private static LoadedModel baseModel;
	private TexturedMaterial material;

	public PlayerModel(TexturedMaterial material)
	{
		this.material = material;
	}

	@Override
	public Mesh createMesh(float offX, float offY, float offZ, float scaleX, float scaleY, float scaleZ)
	{
		if (baseModel == null)
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

		return baseModel.createMesh(offX, offY, offZ, scaleX, scaleY, scaleZ);
	}

	@Override
	public Mesh createMesh(float offX, float offY, float offZ, float scale)
	{
		return createMesh(offX, offY, offZ, scale, scale, scale);
	}

	public TexturedMaterial material()
	{
		return material;
	}
}

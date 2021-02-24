package com.cornchipss.cosmos.blocks;

import com.cornchipss.cosmos.models.CubeModel;
import com.cornchipss.cosmos.models.IHasModel;

public class Block implements IHasModel
{
	private CubeModel model;
	
	public Block(CubeModel m)
	{
		this.model = m;
	}
	
	@Override
	public CubeModel model()
	{
		return model;
	}
}

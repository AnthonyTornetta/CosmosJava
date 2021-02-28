package com.cornchipss.cosmos.blocks;

import com.cornchipss.cosmos.models.CubeModel;
import com.cornchipss.cosmos.models.IHasModel;
import com.cornchipss.cosmos.structures.Structure;

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
	
	public boolean canAddTo(Structure s)
	{
		return true;
	}
}

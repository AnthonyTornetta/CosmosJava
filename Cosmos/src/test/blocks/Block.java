package test.blocks;

import test.models.CubeModel;
import test.models.IHasModel;

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

package com.cornchipss.cosmos.models.blocks;

import com.cornchipss.cosmos.blocks.BlockFace;
import com.cornchipss.cosmos.models.CubeModel;

public class StoneModel extends CubeModel
{
	@Override
	public float u(BlockFace side)
	{
		return material().uLength() * 2;
	}

	@Override
	public float v(BlockFace side)
	{
		return 0;
	}
}

package com.cornchipss.cosmos.models.blocks;

import com.cornchipss.cosmos.blocks.BlockFace;
import com.cornchipss.cosmos.models.CubeModel;

public class LightModel extends CubeModel
{
	@Override
	public float u(BlockFace side)
	{
		return 0;
	}

	@Override
	public float v(BlockFace side)
	{
		return material().uvHeight();
	}
}

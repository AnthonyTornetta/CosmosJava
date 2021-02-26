package com.cornchipss.cosmos.models;

import com.cornchipss.cosmos.blocks.BlockFace;

public class ShipCoreModel extends CubeModel
{
	@Override
	public float u(BlockFace side)
	{
		return CubeModel.TEXTURE_DIMENSIONS * 10; // - max width = 16
	}

	@Override
	public float v(BlockFace side)
	{
		return 0;
	}

}

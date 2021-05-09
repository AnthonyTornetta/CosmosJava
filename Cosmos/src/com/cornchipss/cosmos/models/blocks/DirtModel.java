package com.cornchipss.cosmos.models.blocks;

import com.cornchipss.cosmos.blocks.BlockFace;
import com.cornchipss.cosmos.models.CubeModel;

public class DirtModel extends CubeModel
{
	@Override
	public float u(BlockFace side)
	{
		return material().uvWidth() * 3;
	}

	@Override
	public float v(BlockFace side)
	{
		return 0;
	}

}

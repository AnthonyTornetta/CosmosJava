package com.cornchipss.cosmos.models.blocks;

import com.cornchipss.cosmos.blocks.BlockFace;
import com.cornchipss.cosmos.models.CubeModel;

public class GrassModel extends CubeModel
{
	@Override
	public float u(BlockFace side)
	{
		switch(side)
		{
		case TOP:
			return 1 * material().uvWidth();
		case BOTTOM:
			return 3 * material().uvWidth();
		default:
			return 4 * material().uvWidth();
		}
	}

	@Override
	public float v(BlockFace side)
	{
		return 0;
	}
}

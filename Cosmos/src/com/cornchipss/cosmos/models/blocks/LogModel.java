package com.cornchipss.cosmos.models.blocks;

import com.cornchipss.cosmos.blocks.BlockFace;
import com.cornchipss.cosmos.models.CubeModel;

public class LogModel extends CubeModel
{
	@Override
	public float u(BlockFace side)
	{
		switch (side)
		{
			case TOP:
				return 1 * material().uLength();
			case BOTTOM:
				return 1 * material().uLength();
			default:
				return 2 * material().uLength();
		}
	}

	@Override
	public float v(BlockFace side)
	{
		return material().vLength();
	}
}

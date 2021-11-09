package com.cornchipss.cosmos.models.blocks;

import com.cornchipss.cosmos.blocks.BlockFace;
import com.cornchipss.cosmos.models.CubeModel;

public class LaserCannonModel extends CubeModel
{
	@Override
	public float u(BlockFace side)
	{
		switch(side)
		{
		case BACK:
			return 6 * material().uLength();
		case FRONT:
			return 8 * material().uLength();
		case TOP:
		case BOTTOM:
			return 7 * material().uLength();
		default: // left right
			return 5 * material().uLength();
		}
	}

	@Override
	public float v(BlockFace side)
	{
		return material().vLength();
	}
}

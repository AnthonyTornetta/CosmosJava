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
		case FRONT:
			return 6 * material().uLength();
		default:
			return 5 * material().uLength();
		}
	}

	@Override
	public float v(BlockFace side)
	{
		return material().vLength();
	}
}

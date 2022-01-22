package com.cornchipss.cosmos.models.blocks;

import com.cornchipss.cosmos.blocks.BlockFace;
import com.cornchipss.cosmos.models.CubeModel;

public class CameraModel extends CubeModel
{
	@Override
	public float u(BlockFace side)
	{
		switch (side)
		{
			case BACK:
				return 9 * material().uLength();
			default: // left right
				return 4 * material().uLength();
		}
	}

	@Override
	public float v(BlockFace side)
	{
		return material().vLength();
	}
}

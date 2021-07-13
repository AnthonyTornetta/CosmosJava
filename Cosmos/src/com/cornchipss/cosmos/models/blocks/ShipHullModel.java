package com.cornchipss.cosmos.models.blocks;

import com.cornchipss.cosmos.blocks.BlockFace;
import com.cornchipss.cosmos.models.CubeModel;

public class ShipHullModel extends CubeModel
{
	@Override
	public float u(BlockFace side)
	{
		return material().uLength() * 4;
	}

	@Override
	public float v(BlockFace side)
	{
		return material().vLength();
	}
}

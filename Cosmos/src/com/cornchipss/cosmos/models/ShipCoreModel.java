package com.cornchipss.cosmos.models;

import com.cornchipss.cosmos.blocks.BlockFace;
import com.cornchipss.cosmos.material.Material;
import com.cornchipss.cosmos.material.Materials;

public class ShipCoreModel extends CubeModel
{
	@Override
	public float u(BlockFace side)
	{
		return 0; // - max width = 16
	}

	@Override
	public float v(BlockFace side)
	{
		return 0;
	}
	
	public float maxU(BlockFace side)
	{
		return u(side) + TEXTURE_DIMENSIONS;
	}
	
	public float maxV(BlockFace side)
	{
		return v(side) + TEXTURE_DIMENSIONS;
	}
	
	@Override
	public Material material()
	{
		return Materials.ANIMATED_DEFAULT_MATERIAL;
	}
}

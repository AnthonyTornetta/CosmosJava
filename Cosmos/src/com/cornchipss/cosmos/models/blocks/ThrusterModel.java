package com.cornchipss.cosmos.models.blocks;

import com.cornchipss.cosmos.blocks.BlockFace;
import com.cornchipss.cosmos.material.Material;
import com.cornchipss.cosmos.material.Materials;
import com.cornchipss.cosmos.models.AnimatedCubeModel;

public class ThrusterModel extends AnimatedCubeModel
{
	@Override
	public float u(BlockFace side)
	{
		if(side == BlockFace.FRONT)
			return 0;
		else if(side == BlockFace.BACK)
			return material().uvWidth() * 6;
		else if(side == BlockFace.LEFT || side == BlockFace.RIGHT)
			return material().uvWidth() * 4;
		else
			return material().uvWidth() * 2;
	}
	
	@Override
	public float v(BlockFace side)
	{
		return material().uvHeight();
	}
	
	@Override
	public int maxAnimationStage(BlockFace side)
	{
		if(side != BlockFace.BACK)
			return 2;
		else
			return 1;
	}

	@Override
	public float animationDelay(BlockFace side)
	{
		return 1/3.0f;
	}
	
	@Override
	public Material material()
	{
		return Materials.ANIMATED_DEFAULT_MATERIAL;
	}
}

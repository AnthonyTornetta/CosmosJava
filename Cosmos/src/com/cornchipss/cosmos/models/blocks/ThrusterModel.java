package com.cornchipss.cosmos.models.blocks;

import com.cornchipss.cosmos.blocks.BlockFace;
import com.cornchipss.cosmos.material.TexturedMaterial;
import com.cornchipss.cosmos.material.Materials;
import com.cornchipss.cosmos.models.AnimatedCubeModel;

public class ThrusterModel extends AnimatedCubeModel
{
	@Override
	public float u(BlockFace side)
	{
		if (side == BlockFace.FRONT)
			return 0;
		else if (side == BlockFace.BACK)
			return material().uLength() * 6;
		else if (side == BlockFace.LEFT || side == BlockFace.RIGHT)
			return material().uLength() * 4;
		else
			return material().uLength() * 2;
	}

	@Override
	public float v(BlockFace side)
	{
		return material().vLength();
	}

	@Override
	public int maxAnimationStage(BlockFace side)
	{
		if (side != BlockFace.BACK)
			return 2;
		else
			return 1;
	}

	@Override
	public float animationDelay(BlockFace side)
	{
		return 1 / 3.0f;
	}

	@Override
	public TexturedMaterial material()
	{
		return Materials.ANIMATED_DEFAULT_MATERIAL;
	}
}

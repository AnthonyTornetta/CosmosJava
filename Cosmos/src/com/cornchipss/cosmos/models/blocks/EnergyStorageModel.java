package com.cornchipss.cosmos.models.blocks;

import com.cornchipss.cosmos.blocks.BlockFace;
import com.cornchipss.cosmos.material.TexturedMaterial;
import com.cornchipss.cosmos.material.Materials;
import com.cornchipss.cosmos.models.AnimatedCubeModel;

public class EnergyStorageModel extends AnimatedCubeModel
{
	@Override
	public float u(BlockFace side)
	{
		return 0;
	}

	@Override
	public float v(BlockFace side)
	{
		return material().vLength() * 3;
	}

	@Override
	public int maxAnimationStage(BlockFace side)
	{
		return 8;
	}

	@Override
	public float animationDelay(BlockFace side)
	{
		return 1 / 10.0f;
	}

	@Override
	public TexturedMaterial material()
	{
		return Materials.ANIMATED_DEFAULT_MATERIAL;
	}
}

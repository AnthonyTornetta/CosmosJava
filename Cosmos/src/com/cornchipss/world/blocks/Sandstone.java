package com.cornchipss.world.blocks;

public class Sandstone extends Block
{
	@Override
	public int getTexture(BlockFace face)
	{
		return 8;
	}

	@Override
	public float getMass()
	{
		return 2323; // https://www.aqua-calc.com/page/density-table/substance/sandstone-coma-and-blank-solid
	}
}


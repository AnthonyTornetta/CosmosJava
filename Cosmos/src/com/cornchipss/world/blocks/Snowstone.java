package com.cornchipss.world.blocks;

public class Snowstone extends Block
{
	@Override
	public int getTexture(BlockFace face)
	{
		return 6;
	}

	@Override
	public float getMass()
	{
		return 2515; // https://www.aqua-calc.com/page/density-table/substance/stone-coma-and-blank-common-blank-generic
	}
}

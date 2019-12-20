package com.cornchipss.world.blocks;

public class Snow extends Block
{
	@Override
	public int getTexture(BlockFace face)
	{
		return 5;
	}

	@Override
	public float getMass()
	{
		return 400; // https://www.sciencelearn.org.nz/resources/1391-snow-and-ice-density (wind packed)
	}
}

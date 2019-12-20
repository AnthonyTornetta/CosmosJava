package com.cornchipss.world.blocks;

public class Sand extends Block
{
	@Override
	public int getTexture(BlockFace face)
	{
		return 7;
	}

	@Override
	public float getMass()
	{
		return 1680; // https://civiltoday.com/civil-engineering-materials/sand/299-bulk-density-of-sand
	}
}

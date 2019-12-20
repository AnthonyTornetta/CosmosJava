package com.cornchipss.world.blocks;

public class Dirt extends Block
{
	@Override
	public int getTexture(BlockFace face)
	{
		return 3;
	}

	@Override
	public float getMass()
	{
		return 1220; // https://www.engineeringtoolbox.com/dirt-mud-densities-d_1727.html
	}
}

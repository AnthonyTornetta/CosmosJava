package com.cornchipss.world.blocks;

public class Grass extends Block
{
	@Override
	public int getTexture(BlockFace face)
	{
		switch(face)
		{
			case TOP:
				return 1;
			case BOTTOM:
				return 3;
			default:
				return 4;
		}
	}

	@Override
	public float getMass()
	{
		return 1220; // https://www.engineeringtoolbox.com/dirt-mud-densities-d_1727.html
	}
}

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
}

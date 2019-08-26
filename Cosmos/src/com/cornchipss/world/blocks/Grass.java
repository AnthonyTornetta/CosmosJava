package com.cornchipss.world.blocks;

import com.cornchipss.world.Block;

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

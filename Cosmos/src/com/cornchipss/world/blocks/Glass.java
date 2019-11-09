package com.cornchipss.world.blocks;

public class Glass extends Block
{
	public Glass()
	{
		setOpaque(false);
	}
	
	@Override
	public int getTexture(BlockFace face)
	{
		return 9;
	}
}

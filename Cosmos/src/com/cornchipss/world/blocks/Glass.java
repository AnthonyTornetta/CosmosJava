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

	@Override
	public float getMass()
	{
		return 2500; // https://uk.saint-gobain-building-glass.com/en-gb/architects/physical-properties
	}
}

package com.cornchipss.cosmos.blocks;

import com.cornchipss.cosmos.blocks.data.BlockData;
import com.cornchipss.cosmos.structures.Structure;

public interface IHasData
{
	/**
	 * Generates the block's default data
	 * @param s The structure the block is a part of
	 * @param x The x position of the block relative to the structure
	 * @param y The y position of the block relative to the structure
	 * @param z The z position of the block relative to the structure
	 * @return the block's default data
	 */
	public BlockData generateData(Structure s, int x, int y, int z);
}

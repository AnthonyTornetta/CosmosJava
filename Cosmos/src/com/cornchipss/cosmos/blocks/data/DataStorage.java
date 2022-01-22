package com.cornchipss.cosmos.blocks.data;

import java.util.HashMap;
import java.util.Map;

import com.cornchipss.cosmos.utils.Utils;

public class DataStorage
{
	// Not enough entries to warrant an array
	private Map<Integer, BlockData> dataMap;

	private int width;
	private int height;

	/**
	 * A way of storing BlockData
	 * 
	 * @param width  The width of the container of blocks
	 * @param height The height of the container of blocks
	 * @param length The length of the container of blocks (not currently used
	 *               but may be used later for optimizations)
	 */
	public DataStorage(int width, int height, int length)
	{
		dataMap = new HashMap<>();

		this.width = width;
		this.height = height;
	}

	/**
	 * Sets block data set at that position
	 * 
	 * @param x    position x
	 * @param y    position y
	 * @param z    position z
	 * @param data The data
	 */
	public void set(int x, int y, int z, BlockData data)
	{
		dataMap.put(Utils.array3Dto1D(x, y, z, width, height), data);
	}

	/**
	 * The block data set at that position - null if there is none
	 * 
	 * @param x position x
	 * @param y position y
	 * @param z position z
	 * @return block data set at that position - null if there is none
	 */
	public BlockData get(int x, int y, int z)
	{
		return dataMap.get(Utils.array3Dto1D(x, y, z, width, height));
	}

	/**
	 * Removes the block's data from this location
	 * 
	 * @param x position x
	 * @param y position y
	 * @param z position z
	 */
	public void remove(int x, int y, int z)
	{
		dataMap.remove(Utils.array3Dto1D(x, y, z, width, height));
	}
}

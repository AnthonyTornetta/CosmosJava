package com.cornchipss.registry;

import com.cornchipss.rendering.Model;
import com.cornchipss.rendering.ModelCreator;
import com.cornchipss.world.blocks.Air;
import com.cornchipss.world.blocks.Block;
import com.cornchipss.world.blocks.Dirt;
import com.cornchipss.world.blocks.Grass;
import com.cornchipss.world.blocks.Sand;
import com.cornchipss.world.blocks.Sandstone;
import com.cornchipss.world.blocks.Snow;
import com.cornchipss.world.blocks.Snowstone;
import com.cornchipss.world.blocks.Stone;

public class Blocks
{
	public static final int MAX_BLOCKS = 4096;
	
	private static Block[] blocks = new Block[MAX_BLOCKS];
	private static Model[] models = new Model[MAX_BLOCKS];
	
	public static final Air air = new Air();
	public static final Stone stone = new Stone();
	public static final Grass grass = new Grass();
	public static final Dirt dirt = new Dirt();
	public static final Snow snow = new Snow();
	public static final Snowstone snowstone = new Snowstone();
	public static final Sand sand = new Sand();
	public static final Sandstone sandstone = new Sandstone();
	
	public static void registerBlocks()
	{
		registerBlock(air);
		registerBlock(stone);
		registerBlock(grass);
		registerBlock(dirt);
		registerBlock(snow);
		registerBlock(snowstone);
		registerBlock(sand);
		registerBlock(sandstone);
	}
	
	public static void registerBlock(Block b)
	{
		blocks[b.getId()] = b;
		models[b.getId()] = b.createModel(ModelCreator.DEFAULT);
	}
	
	public static Block getBlock(short id)
	{
		return blocks[id];
	}

	public static Model getModel(short id)
	{
		return models[id];
	}
}

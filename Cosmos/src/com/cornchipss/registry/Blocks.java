package com.cornchipss.registry;

import com.cornchipss.rendering.Model;
import com.cornchipss.rendering.ModelCreator;
import com.cornchipss.world.Block;
import com.cornchipss.world.blocks.Air;
import com.cornchipss.world.blocks.Dirt;
import com.cornchipss.world.blocks.Grass;
import com.cornchipss.world.blocks.Stone;

public class Blocks
{
	private static Block[] blocks = new Block[4096];
	private static Model[] models = new Model[4096];
	
	public static final Air air = new Air();
	public static final Stone stone = new Stone();
	public static final Grass grass = new Grass();
	public static final Dirt dirt = new Dirt();
	
	public static void registerBlocks()
	{
		registerBlock(air);
		registerBlock(stone);
		registerBlock(grass);
		registerBlock(dirt);
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

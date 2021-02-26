package com.cornchipss.cosmos.blocks;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.cornchipss.cosmos.lights.LightSource;
import com.cornchipss.cosmos.models.DirtModel;
import com.cornchipss.cosmos.models.GrassModel;
import com.cornchipss.cosmos.models.LeafModel;
import com.cornchipss.cosmos.models.LightModel;
import com.cornchipss.cosmos.models.LogModel;
import com.cornchipss.cosmos.models.StoneModel;

public class Blocks
{
	private static List<Block> allBlocks;
	
	public static final Block
		GRASS = new Block(new GrassModel()),
		DIRT  = new Block(new DirtModel()),
		STONE = new Block(new StoneModel()),
		LIGHT = new LitBlock(new LightModel(), new LightSource(16)),
		LOG   = new Block(new LogModel()),
		LEAF  = new Block(new LeafModel()),
		SHIP_CORE = new ShipCoreBlock();


	public static void init()
	{
		allBlocks = Collections.unmodifiableList(
				Arrays.asList(STONE, GRASS, DIRT, 
						LIGHT, LOG, LEAF));
	}
	
	/**
	 * Returns a list of all the blocks in the game - this cannot be modified.
	 * @return a list of all the blocks in the game - this cannot be modified.
	 */
	public static List<Block> all()
	{
		return allBlocks;
	}
}

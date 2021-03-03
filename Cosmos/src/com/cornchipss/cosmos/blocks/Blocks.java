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
import com.cornchipss.cosmos.models.ShipHullModel;
import com.cornchipss.cosmos.models.StoneModel;

/**
 * The instance of every block in the base game
 */
public class Blocks
{
	private static List<Block> allBlocks;
	
	public static final Block
		GRASS = new Block(new GrassModel(), "grass"),
		DIRT  = new Block(new DirtModel(), "dirt"),
		STONE = new Block(new StoneModel(), "stone"),
		LIGHT = new LitBlock(new LightModel(), new LightSource(16), "light"),
		LOG   = new Block(new LogModel(), "log"),
		LEAF  = new Block(new LeafModel(), "leaf"),
		SHIP_CORE = new ShipCoreBlock(),
		SHIP_HULL = new Block(new ShipHullModel(), "ship_hull");
	
	/**
	 * Adds all the blocks to a list
	 */
	public static void init()
	{
		allBlocks = Collections.unmodifiableList(
				Arrays.asList(STONE, GRASS, DIRT, 
						LIGHT, LOG, LEAF, SHIP_CORE, SHIP_HULL));
		
		for(short i = 0; i < allBlocks.size(); i++)
			allBlocks.get(i).blockId((short)(i + 1));
	}
	
	public static Block fromNumericId(short id)
	{
		if(id == 0)
			return null;
		return allBlocks.get(id - 1);
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

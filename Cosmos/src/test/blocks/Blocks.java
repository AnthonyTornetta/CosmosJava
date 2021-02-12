package test.blocks;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import test.lights.LightSource;
import test.models.DirtModel;
import test.models.GrassModel;
import test.models.LightModel;
import test.models.StoneModel;

public class Blocks
{
	private static List<Block> allBlocks;
	
	public static final Block
		GRASS = new Block(new GrassModel()),
		DIRT  = new Block(new DirtModel()),
		STONE = new Block(new StoneModel()),
		LIGHT = new LitBlock(new LightModel(), new LightSource(16));

	public static void init()
	{
		allBlocks = Collections.unmodifiableList(
				Arrays.asList(STONE, GRASS, DIRT, LIGHT));
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

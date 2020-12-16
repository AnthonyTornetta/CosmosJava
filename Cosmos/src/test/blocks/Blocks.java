package test.blocks;

import test.models.DirtModel;
import test.models.GrassModel;
import test.models.StoneModel;

public class Blocks
{
	public static final Block
		GRASS = new Block(new GrassModel()),
		DIRT  = new Block(new DirtModel()),
		STONE = new Block(new StoneModel());
}

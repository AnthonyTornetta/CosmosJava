package test.blocks;

import test.lights.LightSource;
import test.models.DirtModel;
import test.models.GrassModel;
import test.models.LightModel;
import test.models.StoneModel;

public class Blocks
{
	public static final Block
		GRASS = new Block(new GrassModel()),
		DIRT  = new Block(new DirtModel()),
		STONE = new Block(new StoneModel()),
		LIGHT = new LitBlock(new LightModel(), new LightSource(16));
}

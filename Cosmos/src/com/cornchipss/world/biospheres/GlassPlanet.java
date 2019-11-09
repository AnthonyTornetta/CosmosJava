package com.cornchipss.world.biospheres;

import com.cornchipss.registry.Blocks;
import com.cornchipss.registry.annotations.RegisteredBiosphere;
import com.cornchipss.world.blocks.Block;
import com.cornchipss.world.generation.DefaultPlanetGenerator;

import libs.noise.SimplexNoise;

@RegisteredBiosphere(id="cosmos:glass")
public class GlassPlanet extends Biosphere
{
	@Override
	public void generate(boolean render, int delay, SimplexNoise noiseMaker)
	{
		DefaultPlanetGenerator.generatePlanet(getPlanet(), noiseMaker, render, delay);
	}

	@Override
	public float frequency()
	{
		return 0.1f;
	}
	
	@Override
	public Block getBlockY(int y, int maxY)
	{
		if(y == maxY)
			return Blocks.glass;
		else
			return Blocks.stone;
	}
}

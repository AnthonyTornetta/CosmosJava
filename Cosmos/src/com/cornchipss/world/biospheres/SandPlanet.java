package com.cornchipss.world.biospheres;

import com.cornchipss.registry.Blocks;
import com.cornchipss.world.blocks.Block;
import com.cornchipss.world.generation.DefaultPlanetGenerator;

import libs.noise.SimplexNoise;

//@RegisteredBiosphere(id="cosmos:sand")
public class SandPlanet extends Biosphere
{
	@Override
	public void generate(boolean render, int delay, SimplexNoise noiseMaker)
	{
		DefaultPlanetGenerator.generatePlanet(getPlanet(), noiseMaker, render, delay, this);
	}

	@Override
	public float frequency()
	{
		return 0.001f;
	}
	
	@Override
	public Block getBlockY(int y, int maxY)
	{
		if(maxY - y <= 5)
			return Blocks.sand;
		if(Math.random() * (maxY - y - 20) > 5)
			return Blocks.stone;
		else
			return Blocks.sandstone;
	}

}

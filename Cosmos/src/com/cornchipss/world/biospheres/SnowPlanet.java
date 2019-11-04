package com.cornchipss.world.biospheres;

import com.cornchipss.registry.Blocks;
import com.cornchipss.registry.annotations.RegisteredBiosphere;
import com.cornchipss.world.blocks.Block;
import com.cornchipss.world.generation.DefaultPlanetGenerator;

import libs.noise.SimplexNoise;

@RegisteredBiosphere(id="cosmos:snow")
public class SnowPlanet extends Biosphere
{
	@Override
	public void generate(boolean render, int delay, SimplexNoise noiseMaker)
	{
		DefaultPlanetGenerator.generatePlanet(getPlanet(), noiseMaker, render, delay);
	}
	
	@Override
	public float frequency()
	{
		return 0.005f;
	}

	@Override
	public Block getBlockY(int y, int maxY)
	{
		return maxY - y <= 5 ? Blocks.snow : Blocks.snowstone;
	}
}

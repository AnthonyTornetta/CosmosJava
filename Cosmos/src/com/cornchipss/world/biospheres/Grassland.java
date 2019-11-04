package com.cornchipss.world.biospheres;

import com.cornchipss.registry.Blocks;
import com.cornchipss.registry.annotations.RegisteredBiosphere;
import com.cornchipss.world.blocks.Block;
import com.cornchipss.world.generation.DefaultPlanetGenerator;

import libs.noise.SimplexNoise;

@RegisteredBiosphere(id="cosmos:grassland")
public class Grassland extends Biosphere
{	
	@Override
	public void generate(boolean render, int delay, SimplexNoise noiseMaker)
	{
		DefaultPlanetGenerator.generatePlanet(getPlanet(), noiseMaker, render, delay);
	}
	
	@Override
	public Block getBlockY(int y, int maxY)
	{
		if(y == maxY)
			return Blocks.grass;
		if(maxY - y <= 5)
			return Blocks.dirt;
		else
			return Blocks.stone;
	}
}

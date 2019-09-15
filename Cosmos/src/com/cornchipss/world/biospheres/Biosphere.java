package com.cornchipss.world.biospheres;

import com.cornchipss.world.blocks.Block;
import com.cornchipss.world.planet.Planet;

import libs.noise.SimplexNoise;

public abstract class Biosphere
{
	private Planet planet;
	private SimplexNoise noise;
	
	public abstract void generate(boolean render, int delay, SimplexNoise noiseMaker);
	
	public Planet getPlanet() { return planet; }
	public void setPlanet(Planet planet) { this.planet = planet; }

	public SimplexNoise getNoise() { return noise; }

	public abstract Block getBlockY(int y, int maxY);

	public int minGenerationLevel()
	{
		return 0;
	}

	public float frequency()
	{
		return 0.01f;
	}

	public float amplitude()
	{
		return 6;
	}

	public int baseHeight()
	{
		return 64;
	}
}

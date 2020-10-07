package com.cornchipss.world.biospheres;

import java.util.Random;

import com.cornchipss.world.blocks.Block;
import com.cornchipss.world.planet.Planet;

import libs.noise.SimplexNoise;

public abstract class Biosphere
{
	private Planet planet;
	private SimplexNoise noise;
	private Random random;
		
	public abstract void generate();
	
	public Planet planet() { return planet; }
	public void planet(Planet planet) { this.planet = planet; }
	
	public void noise(SimplexNoise n) { noise = n; }
	public void random(Random r) { random = r; }
	
	public SimplexNoise noise() { return noise; }
	public Random random() { return random; }
	
	/*
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
	*/
}

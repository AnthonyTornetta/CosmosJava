package com.cornchipss.world.biospheres;

import com.cornchipss.world.blocks.Block;
import com.cornchipss.world.planet.Planet;

import libs.noise.SimplexNoise;

public abstract class Biosphere
{
	private Planet planet;
	private SimplexNoise noise;
	
	public abstract void generate(boolean render, int delay);
	
	public Planet getPlanet() { return planet; }
	public void setPlanet(Planet planet) { this.planet = planet; }

	public SimplexNoise getNoise() { return noise; }

	public Block getBlockAtY(int y) {
		// TODO Auto-generated method stub
		return null;
	}

	public int minGenerationLevel() {
		// TODO Auto-generated method stub
		return 0;
	}

	public float frequency() {
		// TODO Auto-generated method stub
		return 0;
	}

	public float amplitude()
	{
		return 0;
	}

	public int baseHeight()
	{
		return 200;
	}
}

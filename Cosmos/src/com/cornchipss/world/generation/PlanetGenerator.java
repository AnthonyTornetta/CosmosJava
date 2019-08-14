package com.cornchipss.world.generation;

import com.cornchipss.registry.Blocks;
import com.cornchipss.world.planet.Planet;

import libs.noise.SimplexNoise;

public class PlanetGenerator
{
	private SimplexNoise noiseMaker;
	
	public PlanetGenerator(long seed)
	{
		noiseMaker = new SimplexNoise(seed);
	}
	
	/**
	 * Generates the planet & makes it ready for redering
	 * @param planet The planet to generate
	 * @param render Whether or not to actually make it ready for rendering
	 * @param delay How long to rest in milliseconds between each z coordinate generated - this helps reduce FPS crashes when this function is called on a seperate thread, but also makes planets generate slower, 20 is decent number
	 */
	public void generatePlanet(Planet planet, boolean render, int delay)
	{
		for(int z = planet.getBeginningCornerZ(); z < planet.getEndingCornerZ(); z++)
		{
			for(int x = planet.getBeginningCornerX(); x < planet.getEndingCornerX(); x++)
			{
				int y = noise(x, z, 0.02f, 6) + 64;
				int ogY = y;
				
				// Updating the model at this stage is pointless because we're making so many changes
				planet.setBlock(x, y, z, false, Blocks.grass);
				
				for(--y; y >= ogY - 5; y--)
				{
					planet.setBlock(x, y, z, false, Blocks.dirt);
				}
				for(; y >= 0; y--)
				{
					planet.setBlock(x, y, z, false, Blocks.stone);
				}	
			}
			
			if(delay != 0)
			{
				try
				{
					Thread.sleep(delay);
				}
				catch (InterruptedException e)
				{}
			}
		}
		
		if(render)
			planet.render(); // Update the final model
		
		planet.setGenerated(true);
	}
	
	private int noise(int x, int z, float frequency, float amplitude)
	{
		return (int) (amplitude * noiseMaker.noise(x * frequency, z * frequency));
	}
}

package com.cornchipss.world.generation;

import com.cornchipss.world.biospheres.Biosphere;
import com.cornchipss.world.planet.Planet;

import libs.noise.SimplexNoise;

public class DefaultPlanetGenerator
{
	/**
	 * Generates the planet & makes it ready for redering
	 * @param planet The planet to generate
	 * @param render Whether or not to actually make it ready for rendering
	 * @param delay How long to rest in milliseconds between each z coordinate generated - this helps reduce FPS crashes when this function is called on a seperate thread, but also makes planets generate slower, 20 is decent number
	 */
	public static void generatePlanet(Planet planet, SimplexNoise noiseMaker, boolean render, int delay)
	{
		Biosphere bio = planet.getBiosphere();
		
		for(int z = planet.getBeginningCornerZ(); z < planet.getEndingCornerZ(); z++)
		{
			for(int x = planet.getBeginningCornerX(); x < planet.getEndingCornerX(); x++)
			{
				int maxY;
				int y = maxY = noise(noiseMaker, x, z, bio.frequency(), bio.amplitude()) + bio.baseHeight(); // 0.01f, 6 + 64
				
				for(; y >= bio.minGenerationLevel(); y--)
				{
					// Updating the model at this stage is pointless because we're making so many changes
					planet.setBlock(x, y, z, false, bio.getBlockY(y, maxY));
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
	
	private static int noise(SimplexNoise noiseMaker, int x, int z, float frequency, float amplitude)
	{
		return (int) (amplitude * noiseMaker.noise(x * frequency, z * frequency));
	}
}

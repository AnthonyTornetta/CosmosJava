package com.cornchipss.world.biospheres;

import com.cornchipss.registry.Blocks;
import com.cornchipss.registry.annotations.RegisteredBiosphere;
import com.cornchipss.world.planet.Planet;

@RegisteredBiosphere(id="cosmos:glass")
public class GlassPlanetBiosphere extends Biosphere
{
	@Override
	public void generate()
	{
		Planet p = planet();
		
		int averageHeight = 64;// + random().nextInt(24) - 12;
		
		for(int z = p.getBeginningCornerZ(); z <= p.getEndingCornerZ(); z++)
		{
			for(int x = p.getBeginningCornerX(); x <= p.getEndingCornerX(); x++)
			{
				int heightHere = averageHeight;// + noise(x, z, 0.01f, 4);// + noise(x, z, 0.1f, 4);
				
				for(int y = p.getBeginningCornerY(); 
						y <= p.getBeginningCornerY() + heightHere; y++)
				{
					p.setBlock(x, y, z, false, Blocks.grass);
				}
			}
		}
		
		p.setGenerated(true);
	}
	
	private int noise(int x, int z, float frequency, float amplitude)
	{
		return (int) (amplitude * noise().noise(x * frequency, z * frequency));
	}
}

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
		
		int averageHeight = 5;// + random().nextInt(24) - 12;
		
		for(int z = p.beginningCornerZ(); z <= p.endingCornerZ(); z++)
		{
			for(int x = p.beginningCornerX(); x <= p.endingCornerX(); x++)
			{
				int heightHere = averageHeight;// + noise(x, z, 0.01f, 4);// + noise(x, z, 0.1f, 4);
				
				for(int y = p.beginningCornerY(); 
						y <= p.beginningCornerY() + heightHere; y++)
				{
					p.setBlock(x, y, z, true, Blocks.grass);
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

package com.cornchipss.cosmos.biospheres;

import com.cornchipss.cosmos.structures.Structure;

public abstract class Biosphere
{
	public void generatePlanet(Structure s)
	{
		generateTerrain(s);
		populate(s);
	}
	
	protected abstract void generateTerrain(Structure s);
	
	protected abstract void populate(Structure s);
}
package com.cornchipss.cosmos.structures;

import com.cornchipss.cosmos.world.World;

public class Planet extends Structure
{
	public Planet(World world, int width, int height, int length, int id)
	{
		super(world, width, height, length, id);
	}

	public Planet(World world, int id)
	{
		super(world, id);
	}
}

package com.cornchipss.cosmos.structures;

import com.cornchipss.cosmos.world.ZaWARUDO;

/**
 * A structure representing a ship
 */
public class Ship extends Structure
{
	private final static int MAX_DIMENSIONS = 16 * 10;
	
	public Ship(ZaWARUDO world)
	{
		super(world, MAX_DIMENSIONS, MAX_DIMENSIONS, MAX_DIMENSIONS);
	}
}

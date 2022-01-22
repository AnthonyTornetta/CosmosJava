package com.cornchipss.cosmos.blocks.data;

import com.cornchipss.cosmos.structures.Ship;

public class ShipCoreData extends BlockData
{
	private Ship s;
	
	public ShipCoreData(Ship s)
	{
		this.s = s;
	}
	
	public Ship ship()
	{
		return s;
	}
}

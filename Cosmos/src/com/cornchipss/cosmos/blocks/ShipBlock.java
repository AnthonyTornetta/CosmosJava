package com.cornchipss.cosmos.blocks;

import com.cornchipss.cosmos.models.CubeModel;
import com.cornchipss.cosmos.structures.Ship;
import com.cornchipss.cosmos.structures.Structure;

/**
 * A block that is unique to ships
 */
public class ShipBlock extends Block
{
	public ShipBlock(CubeModel m, String name)
	{
		super(m, name);
	}
	
	@Override
	public boolean canAddTo(Structure s)
	{
		return s instanceof Ship;
	}
}

package com.cornchipss.cosmos.blocks;

import com.cornchipss.cosmos.models.CubeModel;
import com.cornchipss.cosmos.structures.Ship;
import com.cornchipss.cosmos.structures.Structure;

public class ShipBlock extends Block
{
	public ShipBlock(CubeModel m)
	{
		super(m);
	}
	
	@Override
	public boolean canAddTo(Structure s)
	{
		return s instanceof Ship;
	}
}

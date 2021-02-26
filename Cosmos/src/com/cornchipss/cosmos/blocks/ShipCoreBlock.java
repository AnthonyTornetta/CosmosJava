package com.cornchipss.cosmos.blocks;

import com.cornchipss.cosmos.blocks.data.BlockData;
import com.cornchipss.cosmos.lights.LightSource;
import com.cornchipss.cosmos.models.ShipCoreModel;
import com.cornchipss.cosmos.structures.Structure;

public class ShipCoreBlock extends LitBlock implements IHasData
{
	public ShipCoreBlock() 
	{
		super(new ShipCoreModel(), new LightSource(8));
	}

	@Override
	public BlockData generateData(Structure s, int x, int y, int z)
	{
		BlockData data = new BlockData();
		data.data("ship", s);
		return data;
	}
}

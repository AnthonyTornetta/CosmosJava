package com.cornchipss.cosmos.blocks;

import com.cornchipss.cosmos.blocks.data.BlockData;
import com.cornchipss.cosmos.models.ShipCoreModel;
import com.cornchipss.cosmos.structures.Structure;
import com.cornchipss.cosmos.utils.Utils;

public class ShipCoreBlock extends ShipBlock implements IHasData, IInteractable
{
	public ShipCoreBlock()
	{
		super(new ShipCoreModel());
	}

	@Override
	public BlockData generateData(Structure s, int x, int y, int z)
	{
		BlockData data = new BlockData();
		data.data("ship", s);
		return data;
	}

	@Override
	public void onInteract()
	{
		Utils.println("Ship Core Used!");
	}
}

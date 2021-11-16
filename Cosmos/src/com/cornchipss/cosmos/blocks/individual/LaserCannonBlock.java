package com.cornchipss.cosmos.blocks.individual;

import com.cornchipss.cosmos.blocks.ShipBlock;
import com.cornchipss.cosmos.blocks.modifiers.ISystemBlock;
import com.cornchipss.cosmos.models.blocks.LaserCannonModel;
import com.cornchipss.cosmos.systems.BlockSystemIDs;

public class LaserCannonBlock extends ShipBlock implements ISystemBlock
{
	private String[] systems = new String[] { BlockSystemIDs.LASER_CANNON_ID };

	public LaserCannonBlock()
	{
		super(new LaserCannonModel(), "laser-cannon", 10);
	}

	@Override
	public String[] systemIds()
	{
		return systems;
	}
}

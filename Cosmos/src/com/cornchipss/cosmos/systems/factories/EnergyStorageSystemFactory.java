package com.cornchipss.cosmos.systems.factories;

import com.cornchipss.cosmos.structures.Structure;
import com.cornchipss.cosmos.systems.BlockSystem;
import com.cornchipss.cosmos.systems.blocksystems.EnergyStorageSystem;

public class EnergyStorageSystemFactory implements BlockSystemFactory
{
	@Override
	public BlockSystem create(Structure s)
	{
		return new EnergyStorageSystem(s);
	}
}

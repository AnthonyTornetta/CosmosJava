package com.cornchipss.cosmos.systems.factories;

import com.cornchipss.cosmos.structures.Structure;
import com.cornchipss.cosmos.systems.BlockSystem;
import com.cornchipss.cosmos.systems.blocksystems.EnergyGenerationSystem;

public class EnergyGenerationSystemFactory implements BlockSystemFactory
{
	@Override
	public BlockSystem create(Structure s)
	{
		return new EnergyGenerationSystem(s);
	}
}

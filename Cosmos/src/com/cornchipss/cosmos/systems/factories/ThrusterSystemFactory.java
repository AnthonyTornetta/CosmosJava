package com.cornchipss.cosmos.systems.factories;

import com.cornchipss.cosmos.structures.Structure;
import com.cornchipss.cosmos.systems.BlockSystem;
import com.cornchipss.cosmos.systems.ThrusterSystem;

public class ThrusterSystemFactory implements BlockSystemFactory
{
	@Override
	public BlockSystem create(Structure s)
	{
		return new ThrusterSystem(s);
	}
}

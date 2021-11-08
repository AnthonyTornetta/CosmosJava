package com.cornchipss.cosmos.systems.factories;

import com.cornchipss.cosmos.structures.Structure;
import com.cornchipss.cosmos.systems.BlockSystem;

public interface BlockSystemFactory
{
	public BlockSystem create(Structure s);
}

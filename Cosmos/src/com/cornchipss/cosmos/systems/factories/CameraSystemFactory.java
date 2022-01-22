package com.cornchipss.cosmos.systems.factories;

import com.cornchipss.cosmos.structures.Ship;
import com.cornchipss.cosmos.structures.Structure;
import com.cornchipss.cosmos.systems.BlockSystem;
import com.cornchipss.cosmos.systems.blocksystems.CameraBlockSystem;

public class CameraSystemFactory implements BlockSystemFactory
{
	@Override
	public BlockSystem create(Structure s)
	{
		if(s instanceof Ship)
			return new CameraBlockSystem(s);
		else
			return null;
	}
}

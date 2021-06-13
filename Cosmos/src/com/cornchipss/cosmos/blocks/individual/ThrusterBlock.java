package com.cornchipss.cosmos.blocks.individual;

import com.cornchipss.cosmos.blocks.IThrustProducer;
import com.cornchipss.cosmos.blocks.ShipBlock;
import com.cornchipss.cosmos.models.blocks.ThrusterModel;

public class ThrusterBlock extends ShipBlock implements IThrustProducer
{
	public ThrusterBlock()
	{
		super(new ThrusterModel(), "thruster", 10);
	}

	@Override
	public float thrustGeneratedPerTick()
	{
		return 2000;
	}

	@Override
	public float powerUsedPerTick()
	{
		return 200;
	}
}

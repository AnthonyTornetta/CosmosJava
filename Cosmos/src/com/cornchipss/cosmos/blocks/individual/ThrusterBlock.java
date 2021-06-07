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
	public float thrustGenerated(float delta)
	{
		return 200*delta;
	}

	@Override
	public float powerUsed(float delta)
	{
		return 200*delta;
	}
}

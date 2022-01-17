package com.cornchipss.cosmos.blocks.individual;

import com.cornchipss.cosmos.blocks.ShipBlock;
import com.cornchipss.cosmos.blocks.modifiers.IThrustProducer;
import com.cornchipss.cosmos.models.blocks.ThrusterModel;
import com.cornchipss.cosmos.systems.BlockSystemIDs;

public class ThrusterBlock extends ShipBlock implements IThrustProducer
{
	private static final String[] properties = new String[] {
		BlockSystemIDs.THRUSTER_ID };

	public ThrusterBlock()
	{
		super(new ThrusterModel(), "thruster", 10);
	}

	@Override
	public float thrustGeneratedPerSecond()
	{
		return 2000;
	}

	@Override
	public float powerUsedPerSecond()
	{
		return 200;
	}

	@Override
	public String[] systemIds()
	{
		return properties;
	}
}

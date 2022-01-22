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
		super(new ThrusterModel(), "thruster", 10, 100);
	}

	@Override
	public float thrustGeneratedPerSecond()
	{
		return 1_250_000; // 1 ship hull is 50k kg
	}

	@Override
	public float powerUsedPerSecond()
	{
		return 1_250_000; // just a guess
	}

	@Override
	public String[] systemIds()
	{
		return properties;
	}
}

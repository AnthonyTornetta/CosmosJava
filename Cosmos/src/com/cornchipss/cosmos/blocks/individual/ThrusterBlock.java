package com.cornchipss.cosmos.blocks.individual;

import com.cornchipss.cosmos.blocks.ShipBlock;
import com.cornchipss.cosmos.blocks.modifiers.BlockSystems;
import com.cornchipss.cosmos.blocks.modifiers.IThrustProducer;
import com.cornchipss.cosmos.models.blocks.ThrusterModel;
import com.cornchipss.cosmos.systems.BlockSystem;

public class ThrusterBlock extends ShipBlock implements IThrustProducer
{
	private static final BlockSystem[] properties = new BlockSystem[]
			{
					BlockSystems.THRUSTER
			};
	
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
	public BlockSystem[] systems()
	{
		return properties;
	}
}

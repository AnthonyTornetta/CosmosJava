package com.cornchipss.cosmos.blocks.individual;

import com.cornchipss.cosmos.blocks.Block;
import com.cornchipss.cosmos.blocks.modifiers.BlockSystems;
import com.cornchipss.cosmos.blocks.modifiers.IEnergyProducerBlock;
import com.cornchipss.cosmos.blocks.modifiers.IEnergyStorageBlock;
import com.cornchipss.cosmos.models.blocks.ReactorModel;
import com.cornchipss.cosmos.systems.BlockSystem;

public class ReactorBlock extends Block implements IEnergyProducerBlock, IEnergyStorageBlock
{
	private static final BlockSystem[] properties = new BlockSystem[]
			{
					BlockSystems.POWER_GENERATOR, BlockSystems.POWER_STORAGE
			};
	
	public ReactorBlock()
	{
		super(new ReactorModel(), "reactor", 10);
	}
	
	@Override
	public float energyGeneratedPerSecond()
	{
		return 1000;
	}

	@Override
	public float energyCapacity()
	{
		return 1_000;
	}
	
	@Override
	public BlockSystem[] systems()
	{
		return properties;
	}
}

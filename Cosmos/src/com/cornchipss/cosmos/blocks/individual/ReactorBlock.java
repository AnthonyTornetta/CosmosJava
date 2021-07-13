package com.cornchipss.cosmos.blocks.individual;

import com.cornchipss.cosmos.blocks.Block;
import com.cornchipss.cosmos.blocks.StructureBlock;
import com.cornchipss.cosmos.blocks.modifiers.IEnergyProducerBlock;
import com.cornchipss.cosmos.blocks.modifiers.IEnergyStorageBlock;
import com.cornchipss.cosmos.models.blocks.ReactorModel;

public class ReactorBlock extends Block implements IEnergyProducerBlock, IEnergyStorageBlock
{
	public ReactorBlock()
	{
		super(new ReactorModel(), "reactor", 10);
	}
	
	@Override
	public float energyGeneratedPerSecond(StructureBlock source)
	{
		return 1000;
	}

	@Override
	public float energyCapacity(StructureBlock source)
	{
		return 1_000_000;
	}
}

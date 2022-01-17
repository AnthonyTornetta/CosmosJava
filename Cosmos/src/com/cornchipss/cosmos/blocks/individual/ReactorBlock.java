package com.cornchipss.cosmos.blocks.individual;

import com.cornchipss.cosmos.blocks.Block;
import com.cornchipss.cosmos.blocks.modifiers.IEnergyProducerBlock;
import com.cornchipss.cosmos.blocks.modifiers.IEnergyStorageBlock;
import com.cornchipss.cosmos.models.blocks.ReactorModel;
import com.cornchipss.cosmos.systems.BlockSystemIDs;

public class ReactorBlock extends Block
	implements IEnergyProducerBlock, IEnergyStorageBlock
{
	private static final String[] properties = new String[] {
		BlockSystemIDs.POWER_GENERATOR_ID, BlockSystemIDs.POWER_STORAGE_ID };

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
	public String[] systemIds()
	{
		return properties;
	}
}

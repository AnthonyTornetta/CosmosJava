package com.cornchipss.cosmos.blocks.individual;

import com.cornchipss.cosmos.blocks.Block;
import com.cornchipss.cosmos.blocks.modifiers.BlockSystems;
import com.cornchipss.cosmos.blocks.modifiers.IEnergyStorageBlock;
import com.cornchipss.cosmos.models.blocks.EnergyStorageModel;
import com.cornchipss.cosmos.systems.BlockSystem;

public class EnergyStorageBlock extends Block implements IEnergyStorageBlock
{
	private static final BlockSystem[] systems = new BlockSystem[]
			{
					BlockSystems.POWER_STORAGE
			};
	
	public EnergyStorageBlock()
	{
		super(new EnergyStorageModel(), "energy_storage", 10);
	}

	@Override
	public BlockSystem[] systems()
	{
		return systems;
	}

	@Override
	public float energyCapacity()
	{
		return 10_000;
	}
}

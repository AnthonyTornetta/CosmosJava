package com.cornchipss.cosmos.blocks.individual;

import com.cornchipss.cosmos.blocks.Block;
import com.cornchipss.cosmos.blocks.modifiers.IEnergyStorageBlock;
import com.cornchipss.cosmos.models.blocks.EnergyStorageModel;
import com.cornchipss.cosmos.systems.BlockSystemIDs;

public class EnergyStorageBlock extends Block implements IEnergyStorageBlock
{
	private static final String[] systems = new String[] {
		BlockSystemIDs.POWER_STORAGE_ID };

	public EnergyStorageBlock()
	{
		super(new EnergyStorageModel(), "energy_storage", 10);
	}

	@Override
	public String[] systemIds()
	{
		return systems;
	}

	@Override
	public float energyCapacity()
	{
		return 10_000;
	}
}

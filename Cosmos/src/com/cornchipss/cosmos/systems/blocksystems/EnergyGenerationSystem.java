package com.cornchipss.cosmos.systems.blocksystems;

import com.cornchipss.cosmos.blocks.StructureBlock;
import com.cornchipss.cosmos.blocks.modifiers.IEnergyProducerBlock;
import com.cornchipss.cosmos.structures.Structure;
import com.cornchipss.cosmos.systems.BlockSystem;
import com.cornchipss.cosmos.systems.BlockSystemIDs;

public class EnergyGenerationSystem extends BlockSystem
{
	private float energyGeneratedPerSec = 0;

	public EnergyGenerationSystem(Structure s)
	{
		super(s);
	}

	@Override
	public void addBlock(StructureBlock added)
	{
		energyGeneratedPerSec += ((IEnergyProducerBlock) added.block()).energyGeneratedPerSecond();
	}

	@Override
	public void removeBlock(StructureBlock removed)
	{
		energyGeneratedPerSec -= ((IEnergyProducerBlock) removed.block()).energyGeneratedPerSecond();
	}

	@Override
	public void update(float delta)
	{
		structure().addEnergy(energyGeneratedPerSec * delta);
	}

	@Override
	public String id()
	{
		return BlockSystemIDs.POWER_GENERATOR_ID;
	}
}

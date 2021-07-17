package com.cornchipss.cosmos.systems;

import java.util.List;

import com.cornchipss.cosmos.blocks.StructureBlock;
import com.cornchipss.cosmos.blocks.modifiers.IEnergyProducerBlock;
import com.cornchipss.cosmos.structures.Structure;

public class EnergyGenerationSystem extends BlockSystem
{
	@Override
	public void addBlock(StructureBlock added, List<StructureBlock> otherBlocks)
	{
	}

	@Override
	public void removeBlock(StructureBlock removed, List<StructureBlock> otherBlocks)
	{
	}

	@Override
	public void update(Structure s, List<StructureBlock> blocks, float delta)
	{
		if(blocks.size() > 0)
			s.addEnergy(((IEnergyProducerBlock)blocks.get(0).block()).energyGeneratedPerSecond() * blocks.size() * delta);
	}
}

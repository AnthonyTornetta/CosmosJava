package com.cornchipss.cosmos.systems;

import java.util.List;

import com.cornchipss.cosmos.blocks.StructureBlock;
import com.cornchipss.cosmos.blocks.modifiers.IEnergyStorageBlock;
import com.cornchipss.cosmos.structures.Structure;

public class EnergyStorageSystem extends BlockSystem
{
	@Override
	public void addBlock(StructureBlock added, List<StructureBlock> otherBlocks)
	{
		try
		{
			IEnergyStorageBlock holder = (IEnergyStorageBlock)added.block();
			
			added.structure().increasePowerCapacity(holder.energyCapacity());
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
	}

	@Override
	public void removeBlock(StructureBlock removed, List<StructureBlock> otherBlocks)
	{
		try
		{
			IEnergyStorageBlock holder = (IEnergyStorageBlock)removed.block();
			
			removed.structure().decreasePowerCapacity(holder.energyCapacity());
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
	}

	@Override
	public void update(Structure s, List<StructureBlock> blocks, float delta)
	{
		
	}
}

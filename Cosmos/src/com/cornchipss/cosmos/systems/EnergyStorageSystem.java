package com.cornchipss.cosmos.systems;

import com.cornchipss.cosmos.blocks.StructureBlock;
import com.cornchipss.cosmos.blocks.modifiers.IEnergyStorageBlock;
import com.cornchipss.cosmos.structures.Structure;

public class EnergyStorageSystem extends BlockSystem
{
	public EnergyStorageSystem(Structure s)
	{
		super(s);
	}

	@Override
	public void addBlock(StructureBlock added)
	{
		try
		{
			IEnergyStorageBlock holder = (IEnergyStorageBlock) added.block();

			added.structure().increasePowerCapacity(holder.energyCapacity());
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
	}

	@Override
	public void removeBlock(StructureBlock removed)
	{
		try
		{
			IEnergyStorageBlock holder = (IEnergyStorageBlock) removed.block();

			removed.structure().decreasePowerCapacity(holder.energyCapacity());
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
	}

	@Override
	public void update(float delta)
	{
		
	}

	@Override
	public String id()
	{
		return BlockSystemIDs.POWER_STORAGE_ID;
	}
}

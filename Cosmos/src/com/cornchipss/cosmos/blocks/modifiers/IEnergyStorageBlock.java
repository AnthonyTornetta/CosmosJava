package com.cornchipss.cosmos.blocks.modifiers;

import com.cornchipss.cosmos.blocks.StructureBlock;

public interface IEnergyStorageBlock
{
	/**
	 * The capacity of energy this block can store in Joules
	 * @param source The capacity of energy this block has
	 * @return The capacity of energy this block can store in Joules
	 */
	public float energyCapacity(StructureBlock source);
}

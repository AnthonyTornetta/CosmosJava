package com.cornchipss.cosmos.blocks.modifiers;

public interface IEnergyStorageBlock extends ISystemBlock
{
	/**
	 * The capacity of energy this block can store in Joules
	 * @param source The capacity of energy this block has
	 * @return The capacity of energy this block can store in Joules
	 */
	public float energyCapacity();
}

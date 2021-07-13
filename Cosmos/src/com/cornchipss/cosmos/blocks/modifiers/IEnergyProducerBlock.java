package com.cornchipss.cosmos.blocks.modifiers;

import com.cornchipss.cosmos.blocks.StructureBlock;

public interface IEnergyProducerBlock
{
	/**
	 * The amount of energy in Joules this block generates per second
	 * @param source The block that generated this energy
	 * @return The amount of thrust this generated in 1 second in Joules
	 */
	public float energyGeneratedPerSecond(StructureBlock source);
}

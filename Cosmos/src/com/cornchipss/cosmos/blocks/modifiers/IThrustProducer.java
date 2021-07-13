package com.cornchipss.cosmos.blocks.modifiers;

public interface IThrustProducer
{
	/**
	 * The amount of thrust this generated in the given time in Newtons
	 * @return The amount of thrust this generated in the given time in Newtons
	 */
	public float thrustGeneratedPerSecond();
	
	/**
	 * The energy consumed during that time
	 * @return The amount of power that is used in the given time in Joules
	 */
	public float powerUsedPerSecond();
}

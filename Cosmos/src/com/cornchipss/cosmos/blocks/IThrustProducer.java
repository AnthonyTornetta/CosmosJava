package com.cornchipss.cosmos.blocks;

public interface IThrustProducer
{
	/**
	 * The amount of thrust this generated in the given time in Newtons
	 * @param delta the time
	 * @return The amount of thrust this generated in the given time in Newtons
	 */
	public float thrustGeneratedPerTick();
	
	/**
	 * The energy consumed during that time
	 * @param delta the time
	 * @return The amount of thrust this generated in the given time in Newtons
	 */
	public float powerUsedPerTick();
}

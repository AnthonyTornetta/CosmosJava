package com.cornchipss.cosmos.memory;

/**
 * Marks something as usable by the {@link MemoryPool}
 */
public interface IReusable
{
	/**
	 * Restores the variable to as if it had just been initialized with an empty constructor
	 */
	public void reuse();
}

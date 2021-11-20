package com.cornchipss.cosmos.blocks.modifiers;

import java.util.HashMap;
import java.util.Map;

import com.cornchipss.cosmos.systems.factories.BlockSystemFactory;

public class BlockSystemFactories
{
	private static final Map<String, BlockSystemFactory> factories = new HashMap<>();

	public static void register(BlockSystemFactory f, String id)
	{
		factories.put(id, f);
	}

	public static void remove(String id)
	{
		factories.remove(id);
	}

	public static BlockSystemFactory get(String id)
	{
		if (!factories.containsKey(id))
			throw new IllegalArgumentException(id + " is bad - " + factories.keySet());
		return factories.get(id);
	}
}

package com.cornchipss.cosmos.registry;

import java.util.HashSet;
import java.util.Set;

import com.cornchipss.cosmos.blocks.modifiers.ISystemBlock;

public class BlockAttributes
{
	private static Set<Class<? extends ISystemBlock>> modifiers = new HashSet<>();
	
	public static void register(Class<? extends ISystemBlock> c)
	{
		modifiers.add(c);
	}
}

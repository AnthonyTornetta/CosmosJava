package com.cornchipss.registry;

import java.util.HashMap;
import java.util.Map;

public class Options
{
	public static class IntOption
	{
		int defaultVal, min, max, interval;
		int value;
	}
	
	private static Map<String, IntOption> intOptions = new HashMap<>();
	
	public static int getIntOption(String name)
	{
		return intOptions.get(name).value;
	}
	
	public static void setIntOption(String name, int value)
	{		
		intOptions.get(name).value = value;
	}
	
	public static void createIntOption(String name, int value, int min, int max, int interval)
	{
		IntOption op = new IntOption();
		op.defaultVal = op.value = value;
		op.min = min;
		op.max = max;
		op.interval = interval;
		
		intOptions.put(name, op);
	}
	
	public static void registerDefaults()
	{
		createIntOption("cosmos:render_distance", 1000, 1000, 5000, 1000);
	}
}

package com.cornchipss.cosmos.memory;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.joml.Vector3f;

/**
 * A quick and dirty memory reuse handler
 */
public class MemoryPool
{
	private MemoryPool()
	{
		
	}
	
	private static Map<Class<?>, List<Object>> instances = new HashMap<>();
	
	public static void addToPool(Object o)
	{
		if(instances.containsKey(o.getClass()))
			instances.get(o.getClass()).add(o);
		else
		{
			List<Object> l = new LinkedList<>();
			instances.put(o.getClass(), l);
			l.add(o);
		}
	}
	
	@SuppressWarnings("unchecked")
	public static <T> T getInstance(Class<T> clazz)
	{
		if(instances.containsKey(clazz))
		{
			List<Object> os = instances.get(clazz);
			if(os.size() == 0)
				return null;
			
			return (T) os.remove(0);
		}
		else
			return null;
	}

	@SuppressWarnings("unchecked")
	public static <T> T getInstanceOrCreate(Class<T> clazz)
	{
		try
		{
			if(instances.containsKey(clazz))
			{
				List<Object> os = instances.get(clazz);
				if(os.size() == 0)
					return clazz.getConstructor().newInstance();
				
				return (T) os.remove(0);
			}
			else
				return clazz.getConstructor().newInstance();
		}
		catch(Exception ex)
		{
			throw new RuntimeException(ex);
		}
	}
}

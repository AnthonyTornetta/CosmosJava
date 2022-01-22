package com.cornchipss.cosmos.memory;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.joml.Vector3f;
import org.joml.Vector3i;

/**
 * A quick and dirty memory reuse handler
 */
public class MemoryPool
{
	private MemoryPool()
	{

	}

	private static Map<Thread, Map<Class<?>, List<Object>>> instances = new HashMap<>();

	public static void addToPool(Object o)
	{
		if (o instanceof Vector3f)
			((Vector3f) o).set(0, 0, 0);
		if (o instanceof Vector3i)
			((Vector3i) o).set(0, 0, 0);

		if(!instances.containsKey(Thread.currentThread()))
		{
			instances.put(Thread.currentThread(), new HashMap<>());
		}
		
		Map<Class<?>, List<Object>> here = instances.get(Thread.currentThread());
		
		if (here.containsKey(o.getClass()))
			here.get(o.getClass()).add(o);
		else
		{
			List<Object> l = new LinkedList<>();
			here.put(o.getClass(), l);
			l.add(o);
		}
	}

	@SuppressWarnings("unchecked")
	public static <T> T getInstance(Class<T> clazz)
	{
		if(!instances.containsKey(Thread.currentThread()))
		{
			return null;
		}
		
		Map<Class<?>, List<Object>> here = instances.get(Thread.currentThread());
		
		if (here.containsKey(clazz))
		{
			List<Object> os = here.get(clazz);
			
			if (os.size() == 0)
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
			if(!instances.containsKey(Thread.currentThread()))
			{
				return clazz.getConstructor().newInstance();
			}
			
			Map<Class<?>, List<Object>> here = instances.get(Thread.currentThread());
			
			if (here.containsKey(clazz))
			{
				List<Object> os = here.get(clazz);
				if (os.size() == 0)
					return clazz.getConstructor().newInstance();

				return (T) os.remove(0);
			}
			else
				return clazz.getConstructor().newInstance();
		}
		catch (Exception ex)
		{
			throw new RuntimeException(ex);
		}
	}
}

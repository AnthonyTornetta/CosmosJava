package com.cornchipss.registry;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.reflections.Reflections;

import com.cornchipss.registry.annotations.RegisteredBiosphere;
import com.cornchipss.world.biospheres.Biosphere;

public class Biospheres
{
	private static Map<String, Class<? extends Biosphere>> biospheres = new HashMap<>();
	
	public static void registerBiospheres(String packge)
	{
		Reflections reflections = new Reflections(packge);
		Set<Class<?>> annotated = reflections.getTypesAnnotatedWith(RegisteredBiosphere.class);
		
		for (Class<?> clazz : annotated)
		{
			registerBiosphere(clazz);
		}
	}
	
	public static void registerBiosphere(Class<?> clazz)
	{
		if(Biosphere.class.isAssignableFrom(clazz))
		{
			Class<? extends Biosphere> clas = clazz.asSubclass(Biosphere.class);
			
			RegisteredBiosphere annotation = clas.getAnnotation(RegisteredBiosphere.class);
			if(annotation.id() == null)
				throw new IllegalArgumentException("Annotation for class " + clas.getName() + " must have an id provided!");
			else
			{
				biospheres.put(annotation.id(), clas);
			}
		}
		else
			throw new IllegalArgumentException("A Biosphere annotation was added to a non-biosphere class!\r\nClass: " + clazz.getName());
	}
	
	public int getBiosphereAmount() { return biospheres.size(); }
	public static Set<String> getBiosphereIds() { return biospheres.keySet(); }
	public static Biosphere newInstance(String id)
	{
		try
		{
			return biospheres.get(id).newInstance();
		}
		catch (InstantiationException | IllegalAccessException e)
		{
			throw new RuntimeException(e);
		}
	}
}

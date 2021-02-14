package test.registry;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nonnull;

import org.reflections.Reflections;

import test.biospheres.Biosphere;
import test.registry.annotations.RegisteredBiosphere;

public class Biospheres
{
	private static Map<String, Class<? extends Biosphere>> biospheres = new HashMap<>();
	
	public static void registerBiospheres(String packge)
	{
		Reflections reflections = new Reflections(packge);
		
		// this isn't actually an error.
		// Idk why eclipse marks it as one, and I'm too afraid to ask why.
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
	public static List<String> getBiosphereIds() { return new ArrayList<>(biospheres.keySet()); }
	public static Biosphere newInstance(String id)
	{
		try
		{
			return biospheres.get(id).getDeclaredConstructor().newInstance();
		}
		catch (Exception e)
		{
			throw new RuntimeException(e);
		}
	}
}
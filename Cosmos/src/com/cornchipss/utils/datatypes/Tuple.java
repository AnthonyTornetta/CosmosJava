package com.cornchipss.utils.datatypes;

/**
 * A sorry excuse for a "Tuple" written in 2 minutes
 * @author Cornchip
 *
 * @param <T> The type it will store
 */
public class Tuple<T>
{
	private final Object[] contents;
	
	@SafeVarargs
	public Tuple(T... stuff)
	{
		contents = stuff.clone();
	}
	
	public Tuple(int size)
	{
		contents = new Object[size];
	}
	
	@SuppressWarnings("unchecked")
	public T get(int i)
	{
		return (T)contents[i];
	}
	
	public void set(int i, T data)
	{
		contents[i] = data;
	}
	
	public int size()
	{
		return contents.length;
	}
}

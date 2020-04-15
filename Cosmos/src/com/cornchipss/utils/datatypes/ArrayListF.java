package com.cornchipss.utils.datatypes;

import java.io.Serializable;
import java.util.Iterator;
import java.util.RandomAccess;

/**
 * <p>A way faster version of the standard {@link java.util.ArrayList} just for primitive floats</p>
 * <p>Main benefit is the {@link ArrayListF#asArray} being quite fast.</p>
 * @author Cornchip
 */
public class ArrayListF implements Serializable, RandomAccess, Cloneable, Iterable<Float>
{
	private static final long serialVersionUID = -6294067774439727369L;
	
	public static final int DEFAULT_SIZE = 3000;
	public static final int DEFAULT_INCR_AMOUNT = 100;
	
	private float[] list;
	
	private int size = 0;
	private int icrAmount;
	
	public ArrayListF(float...floats)
	{
		this(DEFAULT_SIZE, floats);
	}
	
	public ArrayListF(int size, float...floats)
	{
		this(size, DEFAULT_INCR_AMOUNT, floats);
	}
	
	public ArrayListF(int size, int icrAmount, float... floats)
	{
		if(floats.length == size)
			list = floats.clone();
		else if(floats.length != 0)
		{
			list = new float[size];
			System.arraycopy(floats, 0, list, 0, floats.length);
		}
		else
			list = new float[size];
		
		this.icrAmount = icrAmount;
	}
	
	public void add(float f)
	{
		add(size, f);
	}
	
	private void expand(int amt)
	{
		if(amt != 0)
		{
			float[] temp = new float[list.length + amt];
			System.arraycopy(list, 0, temp, 0, size());
			list = temp;
		}
	}
	
	public void add(int i, float f)
	{
		while(i + 1 >= list.length)
			expand(icrAmount);
		
		if(i != size())
			System.arraycopy(list, i, list, i + 1, size - i);
		list[i] = f;
		
		size++;
	}
	
	public void set(int index, float f)
	{
		if(index >= size())
			expand(index - size() + 1);
		
		list[index] = f;
	}

	public void remove(int i)
	{
		remove(i, 1);
	}
	
	public void remove(int start, int len)
	{
		if(start + len > size() || start < 0)
			throw new IndexOutOfBoundsException("Cannot remove index " + start + " to " + (start + len) + " from list of size " + size());
		
		System.arraycopy(list, start + len, list, start, list.length - (start + len));
		
		size -= len;
	}
	
	public void shrink()
	{
		if(size() != list.length)
		{
			float[] temp = new float[size()];
			System.arraycopy(list, 0, temp, 0, size());
			list = temp;
		}
	}
	
	/**
	 * <p>Converts the {@link ArrayListF} to a standard float array that has the length of {@link ArrayListF#size()}</p>
	 * <p>To save time, this array is a direct reference to the internal array in the ArrayListF, so modifying one will effect the other (aka don't mess with the returned copy).</p>
	 * <p>The {@link #shrink()} method is called during this to ensure lengths are correct
	 * @return The ArrayListF as standard float array that does <b>not</b> have the length of {@link ArrayListF#size()}
	 */
	public float[] getArray()
	{
		shrink();
		return list;
	}
	
	public float get(int i)
	{
		return list[i];
	}
	
	/**
	 * The amount of elements in this array
	 * @return The amount of elements in this array
	 */
	public int size()
	{
		return size;
	}
	
	@Override
	public String toString()
	{
		StringBuilder b = new StringBuilder();
		b.append("[");
		for(int i = 0; i < size(); i++)
		{
			if(i != 0)
				b.append(", ");
			b.append(get(i));
		}
		b.append("]");
		return b.toString();
	}
	
	@Override
	public ArrayListF clone()
	{
		return new ArrayListF(list.length, icrAmount, list);
	}

	public void clear()
	{
		clear(list.length);
	}
	
	public void clear(int newSize)
	{
		list = new float[newSize];
		size = 0;
	}

	public void swap(int i, int j)
	{
		float temp = list[i];
		list[i] = list[j];
		list[j] = temp;
	}

	public boolean empty()
	{
		return size() == 0;
	}

	/**
	 * Removes the ending portion of an array and is constant time as opposed to {@link #remove(int)} or {@link #remove(int, int)}
	 * @param len The length from the end to trim - must be <= size()
	 */
	public void trimEnd(int len)
	{
		if(len > size())
			throw new IndexOutOfBoundsException("Argument len cannot be larger than size() (" + len + " > " + size() + ")");
		
		size -= len;
	}
	
	private static class Itr implements Iterator<Float>
	{
		int i = 0;
		private ArrayListF inst;
		
		private Itr(ArrayListF instance)
		{
			inst = instance;
		}
		
		@Override
		public boolean hasNext()
		{
			return i != inst.size();
		}

		@Override
		public Float next()
		{
			i++;
			return inst.list[i - 1];
		}
	}

	@Override
	public Iterator<Float> iterator()
	{
		return new Itr(this);
	}

}

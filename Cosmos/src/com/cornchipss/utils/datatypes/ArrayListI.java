package com.cornchipss.utils.datatypes;

import java.io.Serializable;
import java.util.Iterator;
import java.util.RandomAccess;

/**
 * <p>A way faster version of the standard {@link java.util.ArrayList} just for primitive ints</p>
 * <p>Main benifit is the {@link ArrayListI#asArray} being quite fast.</p>
 * @author Cornchip
 */
public class ArrayListI implements Serializable, RandomAccess, Cloneable, Iterable<Integer>
{	
	private static final long serialVersionUID = -4624129838906945316L;
	
	public static final int DEFAULT_SIZE = 3000;
	public static final int DEFAULT_INCR_AMOUNT = 100;
	
	private int[] list;
	
	private int size = 0;
	private int icrAmount;
	
	public ArrayListI()
	{
		this(DEFAULT_SIZE);
	}
	
	public ArrayListI(int size)
	{
		this(size, DEFAULT_INCR_AMOUNT);
	}
	
	public ArrayListI(int size, int icrAmount, int... ints)
	{
		if(ints.length == size)
			list = ints.clone();
		else if(ints.length != 0)
		{
			list = new int[size];
			System.arraycopy(ints, 0, list, 0, ints.length);
		}
		else
			list = new int[size];
		
		this.icrAmount = icrAmount;
	}
	
	public void add(int f)
	{
		add(size, f);
	}
	
	private void expand(int amt, int splitIndex, int value)
	{
		int[] temp = new int[list.length + amt * (int)Math.ceil(splitIndex / (double)icrAmount)];
		System.arraycopy(list, 0, temp, 0, splitIndex);
		temp[splitIndex] = value;
		
		if(splitIndex < size())
			System.arraycopy(list, splitIndex, temp, splitIndex + 1, size() - splitIndex);
		
		list = temp;
	}
	
	public void add(int i, int f)
	{
		if(i + 1 >= list.length)
			expand(icrAmount, i, f);
		else if(i < size())
			System.arraycopy(list, i, list, i + 1, size - i);
		
		list[i] = f;
		size++;
	}
	
	public void remove(int i)
	{
		if(i >= size() || i < 0)
			throw new IndexOutOfBoundsException("Cannot remove index " + i + " from list of size " + size());
		if(i < size() - 1)
			System.arraycopy(list, i + 1, list, i, size() - i - 1);
		else
			list[i] = 0; // No shifting required if it's the last digit
		size--;
	}
	
	public void shrink()
	{
		if(size() != list.length)
		{
			int[] temp = new int[size()];
			System.arraycopy(list, 0, temp, 0, size());
			list = temp;
		}
	}
	
	/**
	 * <p>Converts the {@link ArrayListF} to a standard int array that has the length of {@link ArrayListF#size()}</p>
	 * <p>To save time, this array is a direct reference to the internal array in the ArrayListF, so modifying one will effect the other (aka don't mess with the returned copy).</p>
	 * @return The ArrayListF as standard int array that does <b>not</b> have the length of {@link ArrayListF#size()}
	 */
	public int[] getArray()
	{
		return list;
	}
	
	public int get(int i)
	{
		return list[i];
	}
	
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
	public ArrayListI clone()
	{
		return new ArrayListI(list.length, icrAmount, list);
	}
	
	public void clear()
	{
		clear(list.length);
	}
	
	public void clear(int newSize)
	{
		list = new int[newSize];
		size = 0;
	}

	private static class ArrayListInterator implements Iterator<Integer> // see what i did there
	{
		int i = 0;
		private ArrayListI inst;
		
		private ArrayListInterator(ArrayListI instance)
		{
			inst = instance;
		}
		
		@Override
		public boolean hasNext()
		{
			return i != inst.size();
		}

		@Override
		public Integer next()
		{
			i++;
			return inst.list[i - 1];
		}
	}
	
	@Override
	public Iterator<Integer> iterator()
	{
		return new ArrayListInterator(this);
	}
}

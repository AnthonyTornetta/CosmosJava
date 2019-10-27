package com.cornchipss.utils.datatypes;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

/**
 * <p>Combines the advantages of the {@link LinkedList} and the {@link ArrayList} to negate the negatives of each one.</p>
 * Initially, the list is prepared to have content added/removed at an O(1) rate.
 * Once the {@link #finalize()} method is called, the List is ready to access things at an O(1) rate.
 * If at any point something is added/removed from the list, the list transitions back into addition/removal mode, and {@link #finalize()} must be called again to make it ready to get things efficiently.
 * If {@link #finalize()} isn't called before getting something, it will be worst case O(n/2) efficiency.
 * @author Cornchip
 *
 * @param <T> The type this list should hold
 */
public class LinkedArrayList<T> implements List<T>
{
	private LinkedList<T> linked = new LinkedList<>(); // for adding/removing things quickly
	private T[] finalizedArray; // for getting stuff quickly
	
	@Override
	public boolean add(T arg0)
	{
		setFinalized(false);
		return linked.add(arg0);
	}

	@Override
	public void add(int arg0, T arg1)
	{
		setFinalized(false);
		linked.add(arg0, arg1);
	}

	@Override
	public boolean addAll(Collection<? extends T> arg0)
	{
		setFinalized(false);
		return linked.addAll(arg0);
	}

	@Override
	public boolean addAll(int arg0, Collection<? extends T> arg1)
	{
		setFinalized(false);
		return linked.addAll(arg0, arg1);
	}

	@Override
	public void clear()
	{
		setFinalized(false);
		linked.clear();
	}

	@Override
	public boolean contains(Object arg0)
	{
		return linked.contains(arg0);
	}

	@Override
	public boolean containsAll(Collection<?> arg0)
	{
		return linked.containsAll(arg0);
	}

	@Override
	public T get(int arg0)
	{
		if(finalized())
			return finalizedArray[arg0];
		else
			return linked.get(arg0);
	}

	@Override
	public int indexOf(Object arg0)
	{
		return linked.indexOf(arg0);
	}

	@Override
	public boolean isEmpty()
	{
		return linked.isEmpty();
	}

	@Override
	public Iterator<T> iterator()
	{
		return linked.iterator();
	}

	@Override
	public int lastIndexOf(Object arg0)
	{
		return linked.lastIndexOf(arg0);
	}

	@Override
	public ListIterator<T> listIterator()
	{
		return linked.listIterator();
	}

	@Override
	public ListIterator<T> listIterator(int arg0)
	{
		return linked.listIterator(arg0);
	}

	@Override
	public boolean remove(Object arg0)
	{
		setFinalized(false);
		return linked.remove(arg0);
	}

	@Override
	public T remove(int arg0)
	{
		setFinalized(false);
		return linked.remove(arg0);
	}

	@Override
	public boolean removeAll(Collection<?> arg0)
	{
		if(linked.removeAll(arg0))
		{
			setFinalized(false);
			return true;
		}
		else
			return false;
	}

	@Override
	public boolean retainAll(Collection<?> arg0)
	{
		if(linked.retainAll(arg0))
		{
			setFinalized(false);
			return true;
		}
		else
			return false;
	}

	@Override
	public T set(int arg0, T arg1)
	{
		setFinalized(false);
		return linked.set(arg0, arg1);
	}

	@Override
	public int size()
	{
		return linked.size();
	}

	@Override
	public List<T> subList(int fromIndex, int toIndex)
	{
		return linked.subList(fromIndex, toIndex);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public T[] toArray()
	{
		if(finalized())
			return finalizedArray;
		else
			return (T[]) linked.toArray();
	}
	
	@SuppressWarnings({ "unchecked" })
	@Override
	public <K> K[] toArray(K[] arg0)
	{
		try
		{
			K[] arr = (K[]) toArray();
			
			if(arr.length > arg0.length)
				return arr;
			else
			{
				System.arraycopy(arr, 0, arg0, 0, arr.length);
				return arg0;
			}
		}
		catch(ClassCastException ex)
		{
			throw new ArrayStoreException("Types of arrays did not match.");
		}
	}
	
	public boolean finalized()
	{
		return finalizedArray != null;
	}
	
	/**
	 * <p>Gets the list ready to have the {@link #get(int)} function called at O(1) speed.</p>
	 * <p>This function should be called after you are done setting variables in the list.</p>
	 */
	public void finalize()
	{
		setFinalized(true);
	}
	
	@SuppressWarnings("unchecked")
	private void setFinalized(boolean f)
	{
		if(f)
			finalizedArray = (T[]) linked.toArray();
		else
			finalizedArray = null;
	}
}

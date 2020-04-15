package com.cornchipss.utils.datatypes;

import java.util.HashMap;
import java.util.Map;

import org.joml.Vector3f;
import org.joml.Vector3fc;

public class Vector3fList
{
	private ArrayListF floats;
	private Map<Vector3fc, Integer> indicies = new HashMap<>();
		
	/**
	 * A list of vectors also stored as an array of floats that supports constant time additions/deletions of vectors
	 */
	public Vector3fList()
	{
		floats = new ArrayListF();
	}
	
	/**
	 * A list of vectors also stored as an array of floats that supports constant time additions/deletions of vectors
	 * @param defaultSize The default size of the list of floats (should be 3 * approx number of vectors this will contain) This can expand/shrink on demand. See {@link #shrink()} to shrink this and save some memory
	 */
	public Vector3fList(int defaultSize)
	{
		floats = new ArrayListF(defaultSize);
	}
	
	/**
	 * Must be constant time or very near
	 * @param vec The vector to add
	 * @return If the vector was actually added or already existed
	 */
	public boolean addVector(Vector3fc vec)
	{
		if(!indicies.containsKey(vec))
		{
			indicies.put(vec, floats.size());
			floats.add(vec.x());
			floats.add(vec.y());
			floats.add(vec.z());
			return true;
		}
		else
			return false;
	}
	
	/**
	 * Must be constant time or very near
	 * @param vec The vector to remove
	 * @return True if a model was removed, false if not
	 */
	public boolean removeVector(Vector3fc vec)
	{
		if(indicies.containsKey(vec))
		{
			int index = indicies.get(vec);
			
			Vector3fc atEnd = new Vector3f(floats.get(floats.size() - 3), floats.get(floats.size() - 2), floats.get(floats.size() - 1));
			
			boolean sameAsLast = atEnd.equals(vec);
			
			if(!sameAsLast)
			{
				floats.set(index, atEnd.x());
				floats.set(index + 1, atEnd.y());
				floats.set(index + 2, atEnd.z());
			}
			
			// This is constant time
			floats.trimEnd(3);
			
			indicies.remove(vec);
			
			if(!sameAsLast && !floats.empty())
			{
				indicies.put(atEnd, index);
			}
			
			return true;
		}
		return false;
	}
	
	/**
	 * Reduces the size of the floats list to save memory to only what is needed
	 */
	public void shrink()
	{
		floats.shrink();
	}
	
	/**
	 * A constant time operation to view the vectors as an array of floats
	 * @return The vectors as an array of floats
	 */
	public float[] asFloats()
	{
		return floats.getArray();
	}
	
	public Vector3fc[] asVecs()
	{
		Vector3fc[] vecs = new Vector3fc[floats.size() / 3];
		for(int i = 0; i < floats.size(); i+=3)
		{
			vecs[i / 3] = new Vector3f(floats.get(i), floats.get(i + 1), floats.get(i + 2));
		}
		return vecs;
	}
	
	/**
	 * How many floats are in {@link #asFloats()}
	 * @return How many floats are in {@link #asFloats()}
	 */
	public int size()
	{
		return floats.size();
	}
	
	/**
	 * Checks if this vector list contains a vector in a constant time
	 * @param vec The vector to check
	 * @return If this vector list contains a vector
	 */
	public boolean containsVector(Vector3fc vec)
	{
		return indicies.containsKey(vec);
	}
	
	@Override
	public String toString()
	{
		StringBuilder builder = new StringBuilder("[ ");
		for(float f : floats)
		{
			builder.append(f);
			builder.append(" ");
		}
		builder.append("]");
		
		return builder.toString();
	}
}

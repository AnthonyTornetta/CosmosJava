package com.cornchipss.utils;

import java.nio.FloatBuffer;
import java.util.List;

import org.joml.Matrix4f;
import org.joml.Vector3d;
import org.joml.Vector3f;
import org.joml.Vector3fc;
import org.joml.Vector3i;
import org.lwjgl.BufferUtils;

public class Utils
{
	
	private static FloatBuffer matrixBuffer = BufferUtils.createFloatBuffer(16);
	
	public static void println(Object obj)
	{
		printraw(toString(obj) + "\n");
	}
	
	public static void print(Object obj)
	{
		printraw(toString(obj));
	}
	
	private static void printraw(String s)
	{		
		StackTraceElement trace = Thread.currentThread().getStackTrace()[3];
		
		String clazz = trace.getClassName();

		System.out.print(clazz.substring(clazz.lastIndexOf(".") + 1) + " (" + trace.getLineNumber() + ")> " + s);
	}
	
	public static String arrayToString(Object[] obj)
	{
		if(obj == null)
			return "null";
		
		StringBuilder builder = new StringBuilder("[");
		
		for(int i = 0; i < obj.length; i++)
		{
			if(i != 0)
				builder.append(", ");
			
			builder.append(toString(obj[i]));
		}
		
		builder.append("]");
		
		return builder.toString();
	}
	
	public static String toString(Object obj)
	{
		if(obj == null)
			return "null";
		
		if(obj.getClass().isArray())
		{
			Object[] arr = (Object[])obj;
			return arrayToString(arr);
		}
		
		if(obj instanceof Vector3f)
		{
			Vector3f t = (Vector3f)obj;
			return "[" + t.x + ", " + t.y + ", " + t.z + "]";
		}
		else if(obj instanceof Vector3i)
		{
			Vector3i t = (Vector3i)obj;
			return "[" + t.x + ", " + t.y + ", " + t.z + "]";
		}
		else if(obj instanceof Vector3d)
		{
			Vector3d t = (Vector3d)obj;
			return "[" + t.x + ", " + t.y + ", " + t.z + "]";
		}
		
		return obj.toString();
	}
	
	public static float[] toArray(List<Float> list)
	{
		float[] arr = new float[list.size()];
		
		int i = 0;
		for(float f : list) // Avoids multiple O(n) calculations w/ linked lists
		{
			arr[i++] = f;
		}
		
		return arr;
	}

	public static int[] toArrayInt(List<Integer> list)
	{
		int[] arr = new int[list.size()];
		
		int i = 0;
		for(int num : list) // Avoids multiple O(n) calculations w/ linked lists
		{
			arr[i++] = num;
		}
		
		return arr;
	}
	
	/**
	 * NOT THREAD SAFE
	 * @param mat
	 * @return
	 */
	public static FloatBuffer toFloatBuffer(Matrix4f mat)
	{
		mat.get(matrixBuffer);
		return matrixBuffer;
	}
	
	/**
	 * Converts 3D array coordinates to 1D array coordinates, useful for parallel arrays but with a 3D and 1D
	 * @param x The x in the 3D array
	 * @param y The y in the 3D array
	 * @param z The z in the 3D array
	 * @param width The width of the array
	 * @param height The height of the array
	 * @return The index that would be in a 1D array
	 */
	public static int array3Dto1D(int x, int y, int z, int width, int height)
	{
		return x + y * width + z * width * height;
	}

	/**
	 * Adds two vectors without modifying either one
	 * @param a The first vector
	 * @param b The second vector
	 * @return A new vector of the two vectors added
	 */
	public static Vector3f add(Vector3fc a, Vector3fc b)
	{
		return new Vector3f(a.x() + b.x(), a.y() + b.y(), a.z() + b.z());
	}
	
	/**
	 * Subtracts two vectors without modifying either one
	 * @param a The first vector
	 * @param b The second vector
	 * @return A new vector of the two vectors subtracted
	 */
	public static Vector3f sub(Vector3fc a, Vector3fc b)
	{
		return new Vector3f(a.x() - b.x(), a.y() - b.y(), a.z() - b.z());
	}
	
	/**
	 * Multiplies two vectors without modifying either one
	 * @param a The first vector
	 * @param b The second vector
	 * @return A new vector of the two vectors multiplied
	 */
	public static Vector3f mul(Vector3fc a, Vector3fc b)
	{
		return new Vector3f(a.x() * b.x(), a.y() * b.y(), a.z() * b.z());
	}
	
	/**
	 * Multiplies two vectors without modifying either one
	 * @param x The first vector (<code>new Vector3f(x, x, x)</code>)
	 * @param b The second vector
	 * @return A new vector of the two vectors multiplied
	 */
	public static Vector3f mul(float x, Vector3fc a)
	{
		return mul(a, new Vector3f(x));
	}
	
	/**
	 * Divides two vectors without modifying either one
	 * @param a The first vector
	 * @param b The second vector
	 * @return A new vector of the two vectors divided
	 */
	public static Vector3f div(Vector3fc a, Vector3fc b)
	{
		return new Vector3f(a.x() / b.x(), a.y() / b.y(), a.z() / b.z());
	}
	
	/**
	 * Takes the modulus two vectors without modifying either one
	 * @param a The first vector
	 * @param b The second vector
	 * @return A new vector of the two vectors modulus'ed
	 */
	public static Vector3f mod(Vector3fc a, Vector3fc b)
	{
		return new Vector3f(a.x() % b.x(), a.y() % b.y(), a.z() % b.z());
	}

	public static Vector3f zero()
	{
		return new Vector3f(0, 0, 0);
	}
	
	/**
	 * Clamps a number between two other numbers
	 * @param x The number to clamp between two others
	 * @param m The minimum number
	 * @param M The maximum number
	 * @return The number within <code>m</code> and <code>M</code> inclusive
	 */
	public static float clamp(float x, float m, float M)
	{
		return x > M ? M : x < m ? m : x;
	}

	public static boolean contains(Object[] arr, Object o)
	{
		for(int i = 0; i < arr.length; i++)
			if(arr[i].equals(o))
				return true;
		return false;
	}

	public static boolean equals(Object a, Object b)
	{
		if(a == null && b != null)
			return false;
		if(a != null && b == null)
			return false;
		if(a == null && b == null)
			return true;
		   
		return a.equals(b);
	}
}

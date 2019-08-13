package com.cornchipss.utils;

import java.nio.FloatBuffer;
import java.util.List;

import org.joml.Matrix4f;
import org.lwjgl.BufferUtils;

public class Utils
{
	public static final int FRONT = 0, BACK = 1, TOP = 2, BOTTOM = 3, RIGHT = 4, LEFT = 5;
	
	private static FloatBuffer matrixBuffer = BufferUtils.createFloatBuffer(16);
	
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
}

package com.cornchipss.utils;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector3fc;

public class Maths
{
	/**
	 * Creates a view matrix based on coordinates + rotations
	 * @param x X
	 * @param y Y
	 * @param z Z
	 * @param rx Rotation X
	 * @param ry Rotation Y
	 * @param rz Rotation Z
	 * @param dest The destiantion matrix
	 */
	public static void createViewMatrix(float x, float y, float z, float rx, float ry, float rz, Matrix4f dest)
	{
		dest.identity();
		
		dest.rotate(rx, 1, 0, 0);
		dest.rotate(ry, 0, 1, 0);
		dest.rotate(rz, 0, 0, 1);
		
		dest.translate(-x, -y, -z);
	}
	
	/**
	 * Calculates the ending point based off the starting position, rotation values, and the total distance
	 * @param start The starting point
	 * @param rx The x rotation
	 * @param ry The y rotation
	 * @param dist The total distance travelable
	 * @return The ending point
	 */
	public static Vector3f pointAt(Vector3fc start, float rx, float ry, float dist)
	{
		return add(toComponents(rx, ry, dist), start);
	}
	
	/**
	 * Calculates the ending point based off the starting position, rotation values, and the total distance
	 * @param rx The x rotation
	 * @param ry The y rotation
	 * @param dist The total distance travelable
	 * @return The ending point
	 */
	public static Vector3fc toComponents(float rx, float ry, float velMagnitude)
	{
		Vector3f components = new Vector3f();
		
		final double j = velMagnitude * Math.cos(rx);
		
		components.x = (float) (j * Math.sin(ry));
		components.y = (float) (-velMagnitude * Math.sin(rx));
		components.z = (float) (-j * Math.cos(ry));
		
		return components;
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
	 * Adds vectors without modifying them
	 * @param vecs The vectors
	 * @return A new vector of two vectors added
	 */
	public static Vector3f add(Vector3fc... vecs)
	{
		Vector3f v = Maths.zero();
		
		for(Vector3fc c : vecs)
			v.add(c);
		
		return v;
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
	 * Subtracts vectors without modifying them
	 * @param vecs The vectors
	 * @return A new vector of two vectors subtracted
	 */
	public static Vector3f sub(Vector3fc... vecs)
	{
		Vector3f v = Maths.zero();
		
		for(Vector3fc c : vecs)
			v.sub(c);
		
		return v;
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
	 * Multiplies vectors without modifying them
	 * @param vecs The vectors
	 * @return A new vector of two vectors multiplid
	 */
	public static Vector3f mul(Vector3fc... vecs)
	{
		if(vecs.length == 0)
			return Maths.zero();
		
		Vector3f v = Maths.one();
		
		for(Vector3fc c : vecs)
			v.mul(c);
		
		return v;
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
	 * Divides two vectors without modifying either one
	 * @param a The first vector
	 * @param b The second vector
	 * @return A new vector of the two vectors divided
	 */
	public static Vector3f div(Vector3fc a, float d)
	{
		return new Vector3f(a.x() / d, a.y() / d, a.z() / d);
	}
	
	/**
	 * Divides vectors without modifying them
	 * @param vecs The vectors
	 * @return A new vector of two vectors divided
	 */
	public static Vector3f div(Vector3fc... vecs)
	{
		if(vecs.length == 0)
			return Maths.zero();
		
		Vector3f v = Maths.one();
		
		for(Vector3fc c : vecs)
			v.div(c);
		
		return v;
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
	
	/**
	 * A Vector3f with all values being 0
	 * @return a Vector3f with all values being 0
	 */
	public static Vector3f zero()
	{
		return new Vector3f(0, 0, 0);
	}
	
	public static Vector3f one()
	{
		return new Vector3f(1, 1, 1);
	}

	public static Vector3fc negative()
	{
		return new Vector3f(-1, -1, -1);
	}
}

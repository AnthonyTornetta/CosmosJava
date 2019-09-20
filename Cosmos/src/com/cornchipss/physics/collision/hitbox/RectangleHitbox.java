package com.cornchipss.physics.collision.hitbox;

import org.joml.Vector3f;

import com.cornchipss.physics.shapes.Rectangle;
import com.cornchipss.utils.Utils;

public class RectangleHitbox extends Hitbox implements Rectangle
{
	private Vector3f[] corners;
	private Vector3f dimensions;
	
	/**
	 * A hitbox that represents a rectangle
	 * @param width The width of each side
	 * @param height The height of each side
	 * @param length The length of each side
	 */
	public RectangleHitbox(float width, float height, float length)
	{
		this(new Vector3f(-width, -height, -length), 
				new Vector3f(width, height, length));
	}
	
	/**
	 * A hitbox that represents a rectangle
	 * @param start The start of the rectangle
	 * @param width How far out from the start does it go (x)
	 * @param height How far out from the start does it go (y)
	 * @param length How far out from the start does it go (z)
	 */
	public RectangleHitbox(Vector3f start, 
			float width, float height, float length)
	{
		this(start, new Vector3f(width, height, length));
	}
	
	/**
	 * A hitbox that represents a rectangle
	 * @param start The first corner
	 * @param end The second corner
	 */
	public RectangleHitbox(Vector3f start, Vector3f end)
	{
		corners = new Vector3f[2];
		
		corners[0] = new Vector3f(start);
		corners[1] = new Vector3f(end);
		
		dimensions = Utils.sub(end, start);
	}
	
	@Override
	public Vector3f[] getCorners()
	{
		return corners;
	}
	
	@Override
	public Vector3f getBoundingBox()
	{
		return getDimensions();
	}

	@Override
	public Vector3f getDimensions()
	{
		return new Vector3f(dimensions);
	}

	@Override
	public Vector3f getPosition()
	{
		return corners[0];
	}
}

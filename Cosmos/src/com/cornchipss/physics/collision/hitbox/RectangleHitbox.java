package com.cornchipss.physics.collision.hitbox;

import org.joml.Vector3f;

public class RectangleHitbox extends Hitbox
{
	private Vector3f[] corners;
	
	public RectangleHitbox(float width, float height, float length)
	{
		corners = new Vector3f[2];
		
		corners[0] = new Vector3f(-width, -height, -length);
		corners[1] = new Vector3f(width, height, length);
	}
	
	@Override
	public Vector3f[] getCorners()
	{
		return corners;
	}
	
	@Override
	public Vector3f getBoundingBox()
	{
		return new Vector3f(Math.abs(corners[0].x - corners[1].x),
				Math.abs(corners[0].y - corners[1].y),
				Math.abs(corners[0].z - corners[1].z));
	}
}

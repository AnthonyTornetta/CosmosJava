package com.cornchipss.physics.collision.hitbox;

public class CubeHitbox extends RectangleHitbox
{
	/**
	 * A rectangle hitbox but with the same dimension on each side
	 * @param dimensions The dimensions of each side
	 */
	public CubeHitbox(float dimensions)
	{
		super(dimensions, dimensions, dimensions);
	}
}

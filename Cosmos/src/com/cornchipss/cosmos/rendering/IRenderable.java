package com.cornchipss.cosmos.rendering;

import org.joml.Matrix4fc;

import com.cornchipss.cosmos.client.world.entities.ClientPlayer;

public interface IRenderable
{
	/**
	 * Updates the graphics of the object - this is not guarenteed to be called
	 * before the first draw(). DO NOT do any drawing here, only use this to
	 * create any OpenGL objects needed
	 */
	public void updateGraphics();

	/**
	 * Draws the object to the screen
	 */
	public void draw(Matrix4fc projectionMatrix, Matrix4fc camera,
		ClientPlayer p);

	/**
	 * True if this object should be drawn, false if not. The updateGraphics()
	 * method will be called no matter what
	 * 
	 * @return True if this object should be drawn, false if not.
	 */
	public boolean shouldBeDrawn();
}

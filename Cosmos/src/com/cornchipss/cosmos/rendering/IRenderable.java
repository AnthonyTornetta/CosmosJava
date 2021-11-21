package com.cornchipss.cosmos.rendering;

import org.joml.Matrix4fc;

import com.cornchipss.cosmos.world.entities.player.ClientPlayer;

public interface IRenderable
{
	/**
	 * Updates the graphics of the object - this is called before draw().
	 * DO NOT do any drawing here, only use this to create any OpenGL objects needed
	 */
	public void updateGraphics();
	
	/**
	 * Draws the object to the screen
	 */
	public void draw(Matrix4fc projectionMatrix, ClientPlayer p);
}

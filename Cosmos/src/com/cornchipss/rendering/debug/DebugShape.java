package com.cornchipss.rendering.debug;

import org.joml.Matrix4fc;
import org.newdawn.slick.Color;

public interface DebugShape
{
	public void draw();
	
	public Color color();
	
	public Matrix4fc matrix();
}

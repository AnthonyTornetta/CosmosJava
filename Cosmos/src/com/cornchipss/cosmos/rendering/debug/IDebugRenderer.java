package com.cornchipss.cosmos.rendering.debug;

import java.awt.Color;

import org.joml.Matrix4fc;
import org.joml.Vector3fc;

import com.cornchipss.cosmos.rendering.IRenderable;

public interface IDebugRenderer extends IRenderable
{
	public void drawRectangle(Matrix4fc transform, Vector3fc halfwidths,
		Color color);
}

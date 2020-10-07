package com.cornchipss.rendering.debug;

import org.joml.Matrix4fc;
import org.joml.Vector3fc;
import org.lwjgl.opengl.GL11;
import org.newdawn.slick.Color;

import com.cornchipss.utils.Maths;

public class DebugLine implements DebugShape
{
	private Vector3fc start;
	private Vector3fc end;
	private Color color;
	
	public DebugLine(Vector3fc start, Vector3fc end, Color color)
	{
		this.color = color;
		this.start = start;
		this.end = end;
	}
	
	@Override
	public void draw()
	{
		// TODO: make this not use outdated stuff
		GL11.glLineWidth(10.5f);
		GL11.glColor3f(color.r / 255.0f, color.g / 255.0f, color.b / 255.0f);
		GL11.glBegin(GL11.GL_LINES);
			GL11.glVertex3f(start.x(), start.y(), start.z());
			GL11.glVertex3f(end.x(), end.y(), end.z());
		GL11.glEnd();
	}

	@Override
	public Color color()
	{
		return color;
	}

	@Override
	public Matrix4fc matrix()
	{
		return Maths.identity();
	}
}

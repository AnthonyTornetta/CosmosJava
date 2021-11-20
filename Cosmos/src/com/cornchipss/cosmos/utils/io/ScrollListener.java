package com.cornchipss.cosmos.utils.io;

import org.lwjgl.glfw.GLFWScrollCallbackI;

public class ScrollListener implements GLFWScrollCallbackI
{
	private double yOffset;

	public void update()
	{
		yOffset = 0;
	}

	@Override
	public void invoke(long window, double xOffset, double yOffset)
	{
		this.yOffset += yOffset;
	}

	public double scrollOffset()
	{
		return yOffset;
	}
}

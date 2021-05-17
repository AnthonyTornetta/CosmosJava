package com.cornchipss.cosmos.gui.text;

import java.awt.Font;

public class Fonts
{
	public static final OpenGLFont ARIAL_28 = new OpenGLFont(new Font("Arial", Font.PLAIN, 28));
	public static final OpenGLFont ARIAL_8 = new OpenGLFont(new Font("Arial", Font.PLAIN, 16));
	
	public static void init()
	{
		ARIAL_28.init();
		ARIAL_8.init();
	}
}

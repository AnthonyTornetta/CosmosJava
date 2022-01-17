package com.cornchipss.cosmos.material;

import com.cornchipss.cosmos.material.types.AnimatedDefaultMaterial;
import com.cornchipss.cosmos.material.types.DefaultMaterial;
import com.cornchipss.cosmos.material.types.GuiMaterial;

public class Materials
{
	public static final TexturedMaterial DEFAULT_MATERIAL = new DefaultMaterial(),
		GUI_MATERIAL = new GuiMaterial(),
		ANIMATED_DEFAULT_MATERIAL = new AnimatedDefaultMaterial(),
		GUI_PAUSE_MENU = new GuiMaterial("assets/images/atlas/gui");

	public static void initMaterials()
	{
		DEFAULT_MATERIAL.init();

		ANIMATED_DEFAULT_MATERIAL.init();

		GUI_MATERIAL.init();

		GUI_PAUSE_MENU.init();
	}
}

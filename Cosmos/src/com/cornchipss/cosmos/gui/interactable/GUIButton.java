package com.cornchipss.cosmos.gui.interactable;

import org.joml.Vector3fc;

import com.cornchipss.cosmos.gui.GUIElement;
import com.cornchipss.cosmos.gui.GUITexture;
import com.cornchipss.cosmos.rendering.Mesh;

public class GUIButton extends GUIElement
{
	private GUITexture active, inactive;
	
	private boolean hovered;
	
	public GUIButton(Vector3fc position, float width, float height)
	{
		super(position);
		
		hovered = false;
		active = new GUITexture(position, width, height, 0.5f, 0.0f);
		inactive = new GUITexture(position, width, height, 0.75f, 0.0f);
	}
	
	@Override
	public void delete()
	{
		active.guiMesh().delete();
		inactive.guiMesh().delete();
	}
	
	@Override
	public Mesh guiMesh()
	{
		if(hovered)
			return active.guiMesh();
		else
			return inactive.guiMesh();
	}
}

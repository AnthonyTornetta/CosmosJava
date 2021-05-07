package com.cornchipss.cosmos.gui.interactable;

import org.joml.Vector3fc;
import org.lwjgl.glfw.GLFW;

import com.cornchipss.cosmos.gui.GUITexture;
import com.cornchipss.cosmos.rendering.Mesh;
import com.cornchipss.cosmos.utils.io.Input;

public class GUIButton extends GUIElementInteractable
{
	private GUITexture active, inactive;
	
	private Runnable onclick;
	
	public GUIButton(Vector3fc position, float width, float height,
			Runnable onclick)
	{
		super(position, width, height);
		
		this.onclick = onclick;
		
		active = new GUITexture(position, width, height, 0.5f, 0.0f);
		inactive = new GUITexture(position, width, height, 0.75f, 0.0f);
	}
	
	@Override
	public void delete()
	{
		active.delete();
		inactive.delete();
	}
	
	@Override
	public Mesh guiMesh()
	{
		if(!locked() && hovered())
			return active.guiMesh();
		else
			return inactive.guiMesh();
	}

	@Override
	public boolean update(float delta)
	{
		if(locked())
			return true;
		
		if(hovered() && Input.isMouseBtnJustDown(GLFW.GLFW_MOUSE_BUTTON_LEFT))
		{
			onclick.run();
		}
		
		return true;
	}
}

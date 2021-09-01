package com.cornchipss.cosmos.gui.interactable;

import org.lwjgl.glfw.GLFW;

import com.cornchipss.cosmos.gui.GUIElement;
import com.cornchipss.cosmos.gui.GUITexture;
import com.cornchipss.cosmos.gui.measurement.MeasurementPair;
import com.cornchipss.cosmos.rendering.Mesh;
import com.cornchipss.cosmos.utils.io.Input;

public class GUIButton extends GUIElement implements IGUIInteractable
{
	private GUITexture active, inactive;
	
	private Runnable onclick;
	
	private boolean locked = false;	
	private boolean wasHovered = false;
	
	public GUIButton(MeasurementPair position, MeasurementPair dim,
			Runnable onclick)
	{
		super(position, dim);
		
		this.onclick = onclick;
		
		active = new GUITexture(position, dim, 0.5f, 0.0f);
		inactive = new GUITexture(position, dim, 0.75f, 0.0f);
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
		if(!locked() && wasHovered)
			return active.guiMesh();
		else
			return inactive.guiMesh();
	}

	@Override
	public boolean update(float delta)
	{
		if(locked())
			return true;
		
		if(hovered())
		{
			wasHovered = true;
			
			if(!hidden() && Input.isMouseBtnJustDown(GLFW.GLFW_MOUSE_BUTTON_LEFT))
				onclick.run();
		}
		else
			wasHovered = false;
		
		return true;
	}

	@Override
	public boolean locked()
	{
		return locked;
	}

	@Override
	public void lock()
	{
		locked = true;
	}

	@Override
	public void unlock()
	{
		locked = false;
	}
}

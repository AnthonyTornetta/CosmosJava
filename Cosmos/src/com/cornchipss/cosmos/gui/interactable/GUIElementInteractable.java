package com.cornchipss.cosmos.gui.interactable;

import org.joml.Vector3fc;

import com.cornchipss.cosmos.gui.GUIElement;
import com.cornchipss.cosmos.utils.IUpdatable;
import com.cornchipss.cosmos.utils.io.Input;

public abstract class GUIElementInteractable extends GUIElement implements IUpdatable
{
	private float minX, minY, maxX, maxY;
	
	private boolean locked;
	
	public GUIElementInteractable(Vector3fc position, float width, float height)
	{
		super(position);
		
		minX = position.x();
		minY = position.y();
		maxX = position.x() + width;
		maxY = position.y() + height;
	}
	
	public boolean hovered()
	{
		float mouseX = Input.getRelativeMouseX();
		float mouseY = Input.getRelativeMouseY();
		
		return mouseX >= minX && mouseY >= minY 
				&& mouseX <= maxX && mouseY <= maxY;
	}
	
	public boolean locked()
	{
		return locked;
	}
	
	public void lock()
	{
		locked = true;
	}

	public void unlock()
	{
		locked = false;
	}
}

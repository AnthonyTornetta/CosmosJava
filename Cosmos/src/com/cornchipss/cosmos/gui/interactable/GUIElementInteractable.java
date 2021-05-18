package com.cornchipss.cosmos.gui.interactable;

import com.cornchipss.cosmos.gui.GUIElement;
import com.cornchipss.cosmos.gui.measurement.MeasurementPair;
import com.cornchipss.cosmos.rendering.Window;
import com.cornchipss.cosmos.utils.IUpdatable;
import com.cornchipss.cosmos.utils.io.Input;

public abstract class GUIElementInteractable extends GUIElement implements IUpdatable
{
	private boolean locked;
	
	private MeasurementPair dimensions;
	
	public GUIElementInteractable(MeasurementPair position, MeasurementPair dimensions)
	{
		super(position);
		
		this.dimensions = dimensions;
	}
	
	public boolean hovered()
	{
		float width = Window.instance().getWidth();
		float height = Window.instance().getHeight();
		
		float minX = position().x().actualValue(width);
		float minY = position().y().actualValue(height);
		float maxX = position().x().actualValue(width) + this.dimensions.x().actualValue(width);
		float maxY = position().y().actualValue(height) + this.dimensions.y().actualValue(height);
		
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

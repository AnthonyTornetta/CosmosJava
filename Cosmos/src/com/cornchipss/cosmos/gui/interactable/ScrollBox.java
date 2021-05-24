package com.cornchipss.cosmos.gui.interactable;

import java.awt.Color;
import java.util.List;

import com.cornchipss.cosmos.gui.GUIContainer;
import com.cornchipss.cosmos.gui.GUIElement;
import com.cornchipss.cosmos.gui.GUIRectangle;
import com.cornchipss.cosmos.gui.measurement.AddedMeasurement;
import com.cornchipss.cosmos.gui.measurement.MeasurementPair;
import com.cornchipss.cosmos.gui.measurement.PixelMeasurement;
import com.cornchipss.cosmos.utils.Utils;
import com.cornchipss.cosmos.utils.io.Input;

public class ScrollBox extends GUIRectangle implements GUIContainer, IGUIInteractable	
{
	private List<GUIElement> children;
	
	private boolean locked = false;
	
	public ScrollBox(MeasurementPair position, MeasurementPair dimensions, Color color)
	{
		super(position, dimensions, color);
	}
	
	@Override
	public List<GUIElement> children()
	{
		return null;
	}

	@Override
	public boolean update(float delta)
	{
		if(Input.scrollWheelScrolled())
		{
			float amt = Input.scrollAmount();
			
			position(new MeasurementPair(
					new AddedMeasurement(position().x(), 
							new PixelMeasurement(amt)), 
					position().y()));
		}
		
		return false;
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

package com.cornchipss.cosmos.gui.interactable;

import java.awt.Color;
import java.util.ArrayList;
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
	
	private PixelMeasurement scrollOffset;
	
	public ScrollBox(MeasurementPair position, MeasurementPair dimensions, Color color)
	{
		super(position, dimensions, color);
		
		children = new ArrayList<>();
		
		scrollOffset = new PixelMeasurement(0);
	}
	
	@Override
	public List<GUIElement> children()
	{
		return children;
	}

	@Override
	public boolean update(float delta)
	{
		if(Input.scrollWheelScrolled())
		{
			float amt = Input.scrollAmount() * 8;
			
			scrollOffset.value(scrollOffset.value() + amt);
			
			for(GUIElement c : children)
			{
				c.updateTransform();
			}
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

	@Override
	public void addChild(GUIElement elem)
	{
		children.add(elem);
		
		MeasurementPair p = elem.position();
		
		p.y(new AddedMeasurement(p.x(), scrollOffset));
		elem.position(p);
		
		Utils.println(elem.position());
	}

	@Override
	public void removeChild(GUIElement elem)
	{
		children.remove(elem);
		
		MeasurementPair p = elem.position();
		
		p.y(((AddedMeasurement)p.y()).a());
		elem.position(p);
	}
}

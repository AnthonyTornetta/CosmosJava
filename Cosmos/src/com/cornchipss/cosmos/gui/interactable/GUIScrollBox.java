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
import com.cornchipss.cosmos.rendering.Window;
import com.cornchipss.cosmos.utils.io.Input;

public class GUIScrollBox extends GUIRectangle implements GUIContainer, IGUIInteractable	
{
	private List<GUIElement> children;
	
	private boolean locked = false;
	
	private PixelMeasurement scrollOffset;
	
	public GUIScrollBox(MeasurementPair position, MeasurementPair dimensions, Color color)
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
		if(hovered() && Input.scrollWheelScrolled())
		{
			float amt = -Input.scrollAmount() * 8;
			
			float maxScroll = maxScroll();
			
			if(scrollOffset.value() + amt < 0)
				scrollOffset.value(0);
			else if(scrollOffset.value() + amt < maxScroll)
				scrollOffset.value(scrollOffset.value() + amt);
			else
				scrollOffset.value(maxScroll);
			
			
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
	
	private float maxScroll()
	{
		float maxY = 0;
		
		for(GUIElement elem : children)
		{
			float y = elem.position().x().actualValue(Window.instance().getHeight());
			float height = elem.dimensions().y().actualValue(Window.instance().getHeight());
			
			if(y + height > maxY)
				maxY = y;
		}
		
		return maxY;
	}

	@Override
	public void addChild(GUIElement elem)
	{
		children.add(elem);
		
		MeasurementPair p = elem.position();
		
		p.y(new AddedMeasurement(p.y(), scrollOffset));
		elem.position(p);
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

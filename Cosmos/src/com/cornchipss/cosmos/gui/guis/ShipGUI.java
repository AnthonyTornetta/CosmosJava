package com.cornchipss.cosmos.gui.guis;

import java.awt.Color;

import com.cornchipss.cosmos.gui.GUI;
import com.cornchipss.cosmos.gui.GUIElementHolder;
import com.cornchipss.cosmos.gui.GUIRectangle;
import com.cornchipss.cosmos.gui.measurement.MeasurementPair;
import com.cornchipss.cosmos.gui.measurement.MeasurementParser;
import com.cornchipss.cosmos.gui.measurement.PixelMeasurement;
import com.cornchipss.cosmos.structures.Ship;
import com.cornchipss.cosmos.utils.IUpdatable;

public class ShipGUI extends GUIElementHolder implements IUpdatable
{
	private Ship ship;
	
	private PixelMeasurement energyWidth;
	private GUIRectangle energy;
	
	public ShipGUI(MeasurementPair position, MeasurementPair dimensions)
	{
		super(position, dimensions);
		
		energyWidth = new PixelMeasurement(640);
	}
	
	@Override
	public void onAdd(GUI gui)
	{
		GUIRectangle bg = new GUIRectangle(
				new MeasurementPair(
						MeasurementParser.parse("50% - 320"), new PixelMeasurement(64)), 
				new MeasurementPair(
						new PixelMeasurement(640), new PixelMeasurement(26)), Color.GRAY);
		
		energy = new GUIRectangle(
				new MeasurementPair(
						MeasurementParser.parse("50% - 320"), new PixelMeasurement(64)), 
				new MeasurementPair(
						energyWidth, new PixelMeasurement(26)), new Color(0.4f, 0.4f, 1));
		
		addChild(bg);
		addChild(energy);
	}
	
	@Override
	public boolean hidden()
	{
		return ship() == null;
	}
	
	public void ship(Ship s)
	{
		ship = s;
	}
	
	public Ship ship()
	{
		return ship;
	}

	@Override
	public boolean update(float delta)
	{
		if(ship != null)
		{
			energyWidth.value(ship.energy() / ship.maxEnergy() * 640);
			energy.dimensions(energy.dimensions());
		}
		
		return false;
	}
}
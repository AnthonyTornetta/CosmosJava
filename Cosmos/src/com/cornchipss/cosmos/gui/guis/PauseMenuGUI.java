package com.cornchipss.cosmos.gui.guis;

import com.cornchipss.cosmos.client.Client;
import com.cornchipss.cosmos.gui.GUI;
import com.cornchipss.cosmos.gui.GUIElementHolder;
import com.cornchipss.cosmos.gui.interactable.GUIButtonText;
import com.cornchipss.cosmos.gui.measurement.MeasurementPair;
import com.cornchipss.cosmos.gui.measurement.MeasurementParser;
import com.cornchipss.cosmos.gui.measurement.PixelMeasurement;
import com.cornchipss.cosmos.utils.io.Input;

public class PauseMenuGUI extends GUIElementHolder
{
	private boolean active = false;
	
	private boolean hideCursorOnClose;
	
	public PauseMenuGUI(MeasurementPair xy, MeasurementPair wh)
	{
		super(xy, wh);
	}
	
	@Override
	public void onAdd(GUI gui)
	{
		GUIButtonText quitBtn = new GUIButtonText("QUIT",
			new MeasurementPair(
					MeasurementParser.parse("50% - 150"), 
					MeasurementParser.parse("50% - 30 - 50")), 
			new MeasurementPair(
					new PixelMeasurement(300), 
					new PixelMeasurement(60)), 	
			() ->
			{
				Client.instance().quit();
			});
	
		GUIButtonText resumeBtn = new GUIButtonText("RESUME",
			new MeasurementPair(
					MeasurementParser.parse("50% - 150"), 
					MeasurementParser.parse("50% - 30 + 50")), 
			new MeasurementPair(
					new PixelMeasurement(300), 
					new PixelMeasurement(60)), 	
			() ->
			{
				active(false);
			});
	
		addChild(quitBtn);
		addChild(resumeBtn);
	}
	
	@Override
	public boolean hidden()
	{
		return !active;
	}
	
	public void active(boolean a)
	{
		if(a)
		{
			hideCursorOnClose = !Input.cursorShown();
			Input.hideCursor(false);
		}
		else
		{
			Input.hideCursor(hideCursorOnClose);
		}
		
		active = a;
	}

	public boolean active()
	{
		return active;
	}
}

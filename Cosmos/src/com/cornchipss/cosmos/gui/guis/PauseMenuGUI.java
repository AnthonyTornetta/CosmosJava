package com.cornchipss.cosmos.gui.guis;

import com.cornchipss.cosmos.client.Client;
import com.cornchipss.cosmos.gui.GUI;
import com.cornchipss.cosmos.gui.GUIElementHolder;
import com.cornchipss.cosmos.gui.interactable.GUIButtonText;
import com.cornchipss.cosmos.gui.measurement.MeasurementPair;
import com.cornchipss.cosmos.gui.measurement.MeasurementParser;
import com.cornchipss.cosmos.gui.measurement.PixelMeasurement;

public class PauseMenuGUI extends GUIElementHolder
{
	private Runnable togglePause;
	private boolean active = false;
	
	public PauseMenuGUI(Runnable togglePause, MeasurementPair xy, MeasurementPair wh)
	{
		super(xy, wh);
		
		this.togglePause = togglePause;
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
				togglePause.run();
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
		active = a;
	}

	public boolean active()
	{
		return active;
	}
}

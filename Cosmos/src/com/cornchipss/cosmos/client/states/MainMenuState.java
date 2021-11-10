package com.cornchipss.cosmos.client.states;

import java.io.IOException;

import com.cornchipss.cosmos.client.CosmosClient;
import com.cornchipss.cosmos.gui.GUI;
import com.cornchipss.cosmos.gui.GUITexture;
import com.cornchipss.cosmos.gui.interactable.GUIButton;
import com.cornchipss.cosmos.gui.interactable.GUITextBox;
import com.cornchipss.cosmos.gui.measurement.AddedMeasurement;
import com.cornchipss.cosmos.gui.measurement.MeasurementPair;
import com.cornchipss.cosmos.gui.measurement.PercentMeasurement;
import com.cornchipss.cosmos.gui.measurement.PixelMeasurement;
import com.cornchipss.cosmos.gui.measurement.SubtractedMeasurement;
import com.cornchipss.cosmos.gui.text.Fonts;
import com.cornchipss.cosmos.gui.text.GUIText;
import com.cornchipss.cosmos.gui.text.OpenGLFont;
import com.cornchipss.cosmos.material.Material;
import com.cornchipss.cosmos.material.Materials;
import com.cornchipss.cosmos.material.RawImageMaterial;
import com.cornchipss.cosmos.rendering.Window;

public class MainMenuState extends State
{
	private GUI gui;
	
	private GUIText dbgMessage;
	private GUIButton connectBtn;
	
	private GUITextBox nameBox, ipBox;
	
	@Override
	public void init(Window window)
	{
		gui = new GUI(Materials.GUI_MATERIAL);
		gui.init(0, 0, window.getWidth(), window.getHeight());
		
		Material bgTexture = new RawImageMaterial("assets/images/screenshot-upgraded");
		bgTexture.init();
		GUITexture background = new GUITexture(
				new MeasurementPair(PixelMeasurement.ZERO, PixelMeasurement.ZERO), 
				new MeasurementPair(PercentMeasurement.ONE, PercentMeasurement.ONE), 
				0, 0, bgTexture);
		
		OpenGLFont font = Fonts.ARIAL_28;
		
		dbgMessage = new GUIText("", font, new MeasurementPair(PixelMeasurement.ZERO, PixelMeasurement.ZERO));
		
		String txt = "Connect";
		
		int w = 400;
		int h = font.height() + 8;
		
		MeasurementPair widthHeight = new MeasurementPair(new PixelMeasurement(w), new PixelMeasurement(h));

		GUIText titleLabel = new GUIText("COSMOS", Fonts.ARIAL_72, 
				new MeasurementPair(
						new SubtractedMeasurement(
							PercentMeasurement.HALF,
							new PixelMeasurement(Fonts.ARIAL_72.stringWidth("COSMOS") / 2.f)),
						new AddedMeasurement(
								PercentMeasurement.HALF, 
								new PixelMeasurement(128*1.5f))));
		
		GUIText nameLabel = new GUIText("Name", font, 
				new MeasurementPair(
						new SubtractedMeasurement(
							PercentMeasurement.HALF,
							new PixelMeasurement(w / 2)),
						new AddedMeasurement(
								PercentMeasurement.HALF, 
								new PixelMeasurement(h + 8*2))));
		
		nameBox = new GUITextBox(
				new MeasurementPair(
						new SubtractedMeasurement(
							PercentMeasurement.HALF,
							new PixelMeasurement(w / 2)
						),
						PercentMeasurement.HALF
					), widthHeight, 
				font);
		
		GUIText ipLabel = new GUIText("Server Address", font, 
				new MeasurementPair(
					new SubtractedMeasurement(
							PercentMeasurement.HALF, 
							new PixelMeasurement(w / 2)),
					new SubtractedMeasurement(
							PercentMeasurement.HALF,
							new PixelMeasurement(h + 8)
					)
				));

		ipBox = new GUITextBox(new MeasurementPair(
					new SubtractedMeasurement(
							PercentMeasurement.HALF,
							new PixelMeasurement(w / 2)),
					new SubtractedMeasurement(
							PercentMeasurement.HALF,
							new PixelMeasurement(h * 2 + 8 * 2))
				), widthHeight, font);
		
		GUIText btnText = new GUIText(txt, font, 
				new MeasurementPair(
					new SubtractedMeasurement(
							PercentMeasurement.HALF,
							new PixelMeasurement(font.stringWidth(txt) / 2)),
					new SubtractedMeasurement(
							PercentMeasurement.HALF,
							new PixelMeasurement(h * 3 + 8 * 3)
						)
				));
		
		connectBtn = new GUIButton(new MeasurementPair(
				new SubtractedMeasurement(
						PercentMeasurement.HALF,
						new PixelMeasurement(w / 2)),
				new SubtractedMeasurement(
						PercentMeasurement.HALF,
						new PixelMeasurement(h * 3 + 8 * 3)
					)
			), widthHeight, () ->
		{
			connectBtn.lock();
			
			String[] split = ipBox.text().split(":");
			
			if(split.length > 2)
			{
				dbgMessage.text("You must specify the host and optionally port!");
				return;
			}
			
			String ip = split[0];
			int port;
			
			if(split.length == 2)
			{
				try
				{
					port = Integer.parseInt(split[1]);
				}
				catch(NumberFormatException ex)
				{
					dbgMessage.text("Port must be a number!");
					return;
				}
			}
			else
				port = 1337;
			
			String name = nameBox.text();
			
			if(name.trim().length() == 0)
			{
				name = System.currentTimeMillis() + "";
			}
			if(ip.trim().length() == 0)
			{
				ip = "localhost";
			}
			
			dbgMessage.text("Connecting to " + ip + ":" + port + " as " + name);
			
			final String nameo = name;
			
			Thread t = new Thread(() ->
			{
				try
				{
					CosmosClient.instance().connectTo("127.0.0.1", 1337, nameo);
				}
				catch (IOException e)
				{
					String text = "Connection failed - " + e.getLocalizedMessage();
					dbgMessage.text(text);
					
					connectBtn.unlock();
				}
			});
			
			t.start();
		});
		
		gui.addElement(background, connectBtn, btnText, dbgMessage,
				nameLabel, nameBox, ipLabel, ipBox, titleLabel);
	}
	
	@Override
	public void update(float delta)
	{
		if(CosmosClient.instance().hasCompleteConnection())
		{
			CosmosClient.instance().state(new GameState());
		}
		else
			gui.update(delta);
	}

	@Override
	public void render(float delta)
	{
		if(Window.instance().wasWindowResized())
		{
			gui.onResize(Window.instance().getWidth(), Window.instance().getHeight());
		}
		
		gui.draw();
	}

	@Override
	public void postUpdate()
	{
		
	}

	@Override
	public void remove()
	{
		gui.deleteAll();
	}
}

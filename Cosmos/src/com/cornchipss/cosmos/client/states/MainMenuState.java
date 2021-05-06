package com.cornchipss.cosmos.client.states;

import java.awt.Font;
import java.io.IOException;

import org.joml.Vector3f;

import com.cornchipss.cosmos.client.Client;
import com.cornchipss.cosmos.gui.GUI;
import com.cornchipss.cosmos.gui.GUITexture;
import com.cornchipss.cosmos.gui.interactable.GUIButton;
import com.cornchipss.cosmos.gui.interactable.GUITextBox;
import com.cornchipss.cosmos.gui.text.GUIText;
import com.cornchipss.cosmos.gui.text.OpenGLFont;
import com.cornchipss.cosmos.material.Materials;
import com.cornchipss.cosmos.rendering.Window;
import com.cornchipss.cosmos.utils.Utils;

public class MainMenuState extends State
{
	private GUI gui;
	
	private GUIText dbgMessage;
	private GUIButton connectBtn;
	
	private GUIText nameLabel, ipLabel;
	
	private GUITextBox nameBox, ipBox;
	
	@Override
	public void init(Window window)
	{
		gui = new GUI(Materials.GUI_MATERIAL);
		gui.init(window.getWidth(), window.getHeight());
	
//		gui.addElement(new GUITexture(Maths.zero(), 100, 100, 0, 0));
		
		OpenGLFont font = new OpenGLFont(new Font("Arial", Font.PLAIN, 28));
		font.init();
		
		dbgMessage = new GUIText("", font, 0, 0);

		String txt = "Connect";
		
		int w = 400;
		int h = font.height() + 8;
		
		Vector3f pos = new Vector3f(
				window.getWidth() / 2 - w / 2, 
				window.getHeight() / 2 + 200, 0);
		
		nameLabel = new GUIText("Name", font, pos.x + w / 2 - font.stringWidth("Name") / 2, pos.y);
		
		pos.y -= h + 8;
		nameBox = new GUITextBox(pos, w, h, font);
		
		pos.y -= h + 16;
		
		ipLabel = new GUIText("Server Address", font, pos.x + w / 2 - font.stringWidth("Server Address") / 2, pos.y);
		pos.y -= h + 8;
		ipBox = new GUITextBox(pos, w, h, font);
		
		pos.y -= h + 16;
		
		GUIText btnText = new GUIText(txt, font, 
				pos.x + w / 2 - font.stringWidth(txt) / 2, pos.y);
		
		connectBtn = new GUIButton(pos, w, h, () ->
		{
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
					Client.instance().connectTo("127.0.0.1", 1337, nameo);
				}
				catch (IOException e)
				{
					String text = "Connection failed - " + e.getLocalizedMessage();
					dbgMessage.text(text);
					
					connectBtn.unlock();
				}
			});
			
			t.start();
			
			connectBtn.lock();
			
			Utils.println("ASDF");
		});
		
		gui.addElement(connectBtn, btnText, dbgMessage,
				nameLabel, nameBox, ipLabel, ipBox);
	}
	
	@Override
	public void update(float delta)
	{
		if(Client.instance().hasCompleteConnection())
		{
			Client.instance().state(new GameState());
		}
		else
			gui.update(delta);
	}

	@Override
	public void render(float delta)
	{
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

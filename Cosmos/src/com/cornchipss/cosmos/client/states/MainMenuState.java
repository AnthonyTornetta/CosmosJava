package com.cornchipss.cosmos.client.states;

import java.awt.Font;
import java.io.IOException;

import org.joml.Vector3f;

import com.cornchipss.cosmos.client.Client;
import com.cornchipss.cosmos.gui.GUI;
import com.cornchipss.cosmos.gui.interactable.GUIButton;
import com.cornchipss.cosmos.gui.text.GUIText;
import com.cornchipss.cosmos.gui.text.OpenGLFont;
import com.cornchipss.cosmos.material.Materials;
import com.cornchipss.cosmos.rendering.Window;
import com.cornchipss.cosmos.utils.Utils;

public class MainMenuState extends State
{
	private GUI gui;
	
	private GUIText message;
	private GUIButton btn;
	
	@Override
	public void init(Window window)
	{
		gui = new GUI(Materials.GUI_MATERIAL);
		gui.init(window.getWidth(), window.getHeight());
	
//		gui.addElement(new GUITexture(Maths.zero(), 100, 100, 0, 0));
		
		OpenGLFont font = new OpenGLFont(new Font("Arial", Font.PLAIN, 28));
		font.init();
		
		String txt = "Connect";
		
		int w = font.stringWidth(txt) + 80;
		int h = font.height() + 8;
		
		Vector3f btnPos = new Vector3f(window.getWidth() / 2 - w / 2, window.getHeight() / 2 - h / 2, 0);
		
		GUIText btnText = new GUIText(txt, font, btnPos.x + 40, btnPos.y + 4);
		
		message = new GUIText("", font, 0, 0);
		
		btn = new GUIButton(btnPos, w, h, () ->
		{
			String ip = "127.0.0.1";
			int port = 1337;
			String name = "name";
			
			message.text("Connecting to " + ip + ":" + port + " as " + name);
			
			Thread t = new Thread(() ->
			{
				try
				{
					Client.instance().connectTo("127.0.0.1", 1337, "NAMO");
				}
				catch (IOException e)
				{
					String text = "Connection failed - " + e.getLocalizedMessage();
					message.text(text);
					
					btn.unlock();
				}
			});
			
			t.start();
			
			btn.lock();
			
			Utils.println("ASDF");
		});
		
		gui.addElement(btn, btnText, message);
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

package com.cornchipss.cosmos.client.states;

import java.awt.Font;

import org.joml.Vector3f;

import com.cornchipss.cosmos.client.Client;
import com.cornchipss.cosmos.client.CosmosNettyClient;
import com.cornchipss.cosmos.gui.GUI;
import com.cornchipss.cosmos.gui.interactable.GUIButton;
import com.cornchipss.cosmos.gui.text.GUIText;
import com.cornchipss.cosmos.gui.text.OpenGLFont;
import com.cornchipss.cosmos.material.Materials;
import com.cornchipss.cosmos.rendering.Window;

public class MainMenuState extends State
{
	private GUI gui;
	private CosmosNettyClient client;
	
	@Override
	public void init(Window window, CosmosNettyClient client)
	{
		this.client = client;
		
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
		
		GUIButton btn = new GUIButton(btnPos, w, h);
		gui.addElement(btn);
		gui.addElement(btnText);
	}

	@Override
	public void update(float delta)
	{
		if(client.ready())
		{
			Client.instance().state(new GameState());
		}
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
		
	}
}

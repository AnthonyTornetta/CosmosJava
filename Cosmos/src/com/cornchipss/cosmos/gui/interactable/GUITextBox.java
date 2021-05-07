package com.cornchipss.cosmos.gui.interactable;

import org.joml.Vector3fc;
import org.lwjgl.glfw.GLFW;

import com.cornchipss.cosmos.gui.GUI;
import com.cornchipss.cosmos.gui.GUITexture;
import com.cornchipss.cosmos.gui.text.GUIText;
import com.cornchipss.cosmos.gui.text.OpenGLFont;
import com.cornchipss.cosmos.rendering.Mesh;
import com.cornchipss.cosmos.utils.io.Input;

public class GUITextBox extends GUIElementInteractable 
{	
	private GUITexture active, inactive;
	private GUIText textGUI;
	
	private String text;
	
	private boolean typing = false;
	
	public GUITextBox(Vector3fc position, float width, float height, OpenGLFont font)
	{
		super(position, width, height);
		
		text = "";
		
		textGUI = new GUIText(text, font, position.x() + 6, position.y()
				+ (height - font.height()) / 2);
		
		active = new GUITexture(position, width, height, 0.5f, 0.25f);
		inactive = new GUITexture(position, width, height, 0.75f, 0.25f);
	}
	
	@Override
	public void delete()
	{
		active.delete();
		inactive.delete();
		textGUI.delete();
	}
	
	@Override
	public void prepare(GUI gui)
	{
		super.prepare(gui);
	}
	
	@Override
	public void draw(GUI gui)
	{
		super.draw(gui);
	}
	
	@Override
	public void finish(GUI gui)
	{
		super.finish(gui);
		
		gui.draw(textGUI);
	}
	
	@Override
	public Mesh guiMesh()
	{
		if(typing)
			return active.guiMesh();
		else
			return inactive.guiMesh();
	}

	@Override
	public boolean update(float delta)
	{
		if(locked())
			return true;
		
		if(Input.isMouseBtnJustDown(GLFW.GLFW_MOUSE_BUTTON_LEFT))
		{
			if(hovered())
				typing = true;
			else
				typing = false;
		}
		
		if(!typing())
			return true;
		
		boolean shift = Input.isKeyDown(GLFW.GLFW_KEY_LEFT_SHIFT) || Input.isKeyDown(GLFW.GLFW_KEY_RIGHT_SHIFT);
		
		for(char key = 'a'; key <= 'z'; key++)
		{
			int keycode = GLFW.GLFW_KEY_A + key - 'a';
			
			if(Input.isKeyJustDown(keycode))
			{
				text += shift ? Character.toUpperCase(key) : key;
			}
		}
		
		if(Input.isKeyJustDown(GLFW.GLFW_KEY_SPACE))
			text += " ";
		
		for(char key = '0'; key <= '9'; key++)
		{
			int keycode = GLFW.GLFW_KEY_0 + key - '0';
			int keycode2 = GLFW.GLFW_KEY_KP_0 + key - '0';
			
			if(Input.isKeyJustDown(keycode) || Input.isKeyJustDown(keycode2))
			{
				text += key;
			}
		}
		
		if(Input.isKeyJustDown(GLFW.GLFW_KEY_PERIOD))
			text += ".";
		if(Input.isKeyJustDown(GLFW.GLFW_KEY_SEMICOLON))
			text += shift ? ":" : ";";
		
		if(Input.isKeyJustDown(GLFW.GLFW_KEY_BACKSPACE))
		{
			if(text.length() != 0)
				text = text.substring(0, text.length() - 1);
		}
		
		textGUI.text(text);
		
		return true;
	}

	public String text()
	{
		return text;
	}
	
	public boolean typing()
	{
		return typing;
	}
	
	public void typing(boolean t)
	{
		typing = t;
	}
}

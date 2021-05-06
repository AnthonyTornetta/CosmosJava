package com.cornchipss.cosmos.gui.text;

import org.joml.Vector3f;

import com.cornchipss.cosmos.gui.GUI;
import com.cornchipss.cosmos.gui.GUIElement;
import com.cornchipss.cosmos.rendering.Mesh;

public class GUIText extends GUIElement
{
	private Mesh mesh;
	private OpenGLFont font;
	private String text;
	private String newText;
	
	public GUIText(String text, OpenGLFont font, float x, float y)
	{
		super(new Vector3f(x, y, 0));
		
		this.font = font;
		
		text(text);
	}
	
	public void text(String text)
	{
		newText = text;
	}
	
	public void prepare(GUI gui)
	{
		if(newText != null)
		{
			if(mesh != null)
				mesh.delete();
			
			mesh = TextRenderer.createMesh(newText, font);
			
			newText = null;
		}
		
		super.prepare(gui);
		
		font.bind();
	}
	
	public void finish(GUI gui)
	{
		super.finish(gui);
		
		OpenGLFont.unbind();
		
		gui.material().texture().bind();
	}

	@Override
	public Mesh guiMesh()
	{
		return mesh;
	}

	public OpenGLFont font() { return font; }
	public String text() { return newText != null ? newText : text; }
}

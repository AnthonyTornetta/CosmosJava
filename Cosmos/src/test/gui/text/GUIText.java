package test.gui.text;

import test.Mesh;
import test.Vec3;
import test.gui.GUI;
import test.gui.GUIElement;

public class GUIText extends GUIElement
{
	private Mesh mesh;
	private OpenGLFont font;
	private String text;
	
	public GUIText(String text, OpenGLFont font, float x, float y)
	{
		super(new Vec3(x, y, 0));
		
		this.font = font;
		
		text(text);
	}
	
	public void text(String text)
	{
		this.text = text;
		
		if(mesh != null)
			mesh.delete();
		
		mesh = TextRenderer.createMesh(text, font);
	}
	
	public void prepare(GUI gui)
	{
		super.prepare(gui);
		
		font.bind();
	}
	
	public void finish(GUI gui)
	{
		super.finish(gui);
		
		OpenGLFont.unbind();
		
		gui.texture().bind();
	}

	@Override
	public Mesh guiMesh()
	{
		return mesh;
	}

	public OpenGLFont font() { return font; }
	public String text() { return text; }
}

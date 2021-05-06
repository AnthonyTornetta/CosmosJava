package com.cornchipss.cosmos.gui.interactable;

import org.joml.Vector3fc;
import org.lwjgl.glfw.GLFW;

import com.cornchipss.cosmos.gui.GUIElement;
import com.cornchipss.cosmos.gui.GUITexture;
import com.cornchipss.cosmos.rendering.Mesh;
import com.cornchipss.cosmos.utils.IUpdatable;
import com.cornchipss.cosmos.utils.io.Input;

public class GUIButton extends GUIElement implements IUpdatable
{
	private GUITexture active, inactive;
	
	private boolean hovered;
	
	private float minX, minY, maxX, maxY;
	
	private Runnable onclick;
	
	private boolean locked = false;
	
	public GUIButton(Vector3fc position, float width, float height,
			Runnable onclick)
	{
		super(position);
		
		this.onclick = onclick;
		
		minX = position.x();
		minY = position.y();
		maxX = position.x() + width;
		maxY = position.y() + height;
		
		hovered = false;
		active = new GUITexture(position, width, height, 0.5f, 0.0f);
		inactive = new GUITexture(position, width, height, 0.75f, 0.0f);
	}
	
	@Override
	public void delete()
	{
		active.delete();
		inactive.delete();
	}
	
	@Override
	public Mesh guiMesh()
	{
		if(hovered())
			return active.guiMesh();
		else
			return inactive.guiMesh();
	}

	@Override
	public boolean update(float delta)
	{
		if(locked())
			return true;
		
		float mouseX = Input.getRelativeMouseX();
		float mouseY = Input.getRelativeMouseY();
		
		if(mouseX >= minX && mouseY >= minY && mouseX <= maxX && mouseY <= maxY)
		{
			hovered(true);
			
			if(Input.isMouseBtnJustDown(GLFW.GLFW_MOUSE_BUTTON_LEFT))
			{
				onclick.run();
			}
		}
		else
			hovered(false);
		
		return true;
	}
	
	public boolean hovered()
	{
		return hovered;
	}
	
	public void hovered(boolean b)
	{
		hovered = b;
	}

	public void lock()
	{
		locked = true;
		hovered = false;
	}

	public void unlock()
	{
		locked = false;
	}
	
	public boolean locked()
	{
		return locked;
	}
}

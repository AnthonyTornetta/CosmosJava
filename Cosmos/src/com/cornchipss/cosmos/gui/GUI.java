package com.cornchipss.cosmos.gui;

import java.util.LinkedList;
import java.util.List;

import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL30;

import com.cornchipss.cosmos.material.Material;
import com.cornchipss.cosmos.utils.IUpdatable;

public class GUI
{
	private List<GUIElement> elements;
	private List<IUpdatable> updatableElements;
	
	private Material material;
	
	private Matrix4f projectionMatrix;
	private Matrix4f cameraMatrix;
	
	public GUI(Material material)
	{
		this.material = material;
		elements = new LinkedList<>();
		updatableElements = new LinkedList<>();
		
		cameraMatrix = new Matrix4f().identity();
	}
	
	public void init(int offX, int offY, int width, int height)
	{
		updateProjection(offX, offY, width, height);
	}
	
	public void draw()
	{
		GL11.glEnable(GL13.GL_TEXTURE0);
	
		GL30.glEnable(GL30.GL_DEPTH_TEST);
		GL30.glDepthFunc(GL30.GL_LESS);
		
		GL30.glDisable(GL30.GL_DEPTH_TEST);
		
		for(GUIElement e : elements)
		{
			draw(e);
		}
	}
	
	public void update(float delta)
	{
		for(IUpdatable elem : updatableElements)
		{
			update(elem, delta);
		}
	}
	
	private void update(IUpdatable element, float delta)
	{
		element.update(delta);
		
		if(element instanceof GUIContainer)
		{
			for(GUIElement child : ((GUIContainer)element).children())
			{
				if(child instanceof IUpdatable)
					update((IUpdatable)child, delta);
			}
		}
	}
	
	public Material material()
	{
		return material;
	}
	
	public void addElement(GUIElement... e)
	{
		for(GUIElement elem : e)
		{
			if(elem == null)
				throw new IllegalArgumentException("Attempt to add a null GUI element");
			elements.add(elem);
			
			if(elem instanceof IUpdatable)
				updatableElements.add((IUpdatable)elem);
		}
	}
	
	public void removeElement(GUIElement e)
	{
		elements.remove(e);
		if(e instanceof IUpdatable)
			updatableElements.remove((IUpdatable)e);
	}
	
	public void onResize(float w, float h)
	{
		updateProjection(0, 0, w, h);
		
		for(GUIElement e : elements)
		{
			handleResize(w, h, e);
		}
	}
	
	private void handleResize(float w, float h, GUIElement e)
	{
		e.onResize(w, h);
		
		if(e instanceof GUIContainer)
		{
			for(GUIElement child : ((GUIContainer)e).children())
			{
				handleResize(w, h, child);
			}
		}
	}

	public void updateProjection(float offX, float offY, float width, float height)
	{
		if(projectionMatrix == null)
			projectionMatrix = new Matrix4f();
		projectionMatrix.identity();
//		projectionMatrix.perspective((float)Math.toRadians(90), 
//				width/(float)height,
//				0.1f, 1000);
		
		projectionMatrix.ortho2D(offX, width, offY, height);
	}

	public void deleteAll()
	{
		for(GUIElement g : elements)
			g.delete();
		
		elements.clear();
		updatableElements.clear();
	}

	public void draw(GUIElement elem)
	{
		elem.fullDraw(this, projectionMatrix, cameraMatrix);
		
		if(elem instanceof GUIContainer)
		{
			for(GUIElement child : ((GUIContainer)elem).children())
			{
				draw(child);
			}
		}
	}
}

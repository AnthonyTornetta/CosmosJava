package com.cornchipss.cosmos.gui;

import java.util.LinkedList;
import java.util.List;

import com.cornchipss.cosmos.gui.measurement.MeasurementPair;
import com.cornchipss.cosmos.material.TexturedMaterial;
import com.cornchipss.cosmos.material.Materials;
import com.cornchipss.cosmos.rendering.Mesh;

public abstract class GUIElementHolder extends GUIElement implements IGUIContainer, IHasGUIAddEvent
{
	private List<GUIElement> children = new LinkedList<>();

	public GUIElementHolder(MeasurementPair position, MeasurementPair dimensions)
	{
		super(position, dimensions);
	}

	@Override
	public abstract void onAdd(GUI gui);

	@Override
	public boolean canBeDrawn()
	{
		return false;
	}

	@Override
	public Mesh guiMesh()
	{
		return null;
	}

	@Override
	public List<GUIElement> children()
	{
		return children;
	}

	@Override
	public void addChild(GUIElement elem)
	{
		children.add(elem);
	}

	@Override
	public void removeChild(GUIElement elem)
	{
		children.remove(elem);
	}

	public TexturedMaterial material()
	{
		return Materials.GUI_PAUSE_MENU;
	}
}

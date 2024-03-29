package com.cornchipss.cosmos.gui;

import java.util.List;

public interface IGUIContainer
{
	public List<GUIElement> children();

	public void addChild(GUIElement elem);

	public void removeChild(GUIElement elem);
}

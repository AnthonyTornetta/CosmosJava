package com.cornchipss.cosmos.gui.interactable;

import com.cornchipss.cosmos.utils.IUpdatable;

public interface IGUIInteractable extends IUpdatable
{
	public boolean locked();
	public void lock();
	public void unlock();
}

package com.cornchipss.cosmos.client.states;

import com.cornchipss.cosmos.rendering.Window;

public abstract class State
{
	public abstract void init(Window window);
	
	public abstract void update(float delta);
	
	public abstract void render(float delta);
	
	public abstract void postUpdate();

	public abstract void remove();
}

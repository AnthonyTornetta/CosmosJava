package com.cornchipss.states;

import com.cornchipss.rendering.Window;

public interface State
{
	void start();
	
	void update();
	
	void render(Window window);
	
	void end();
}

package com.cornchipss.states;

import com.cornchipss.rendering.Window;

public interface GameState
{
	void start();
	
	void update();
	
	void render(Window window);
	
	void end();
}

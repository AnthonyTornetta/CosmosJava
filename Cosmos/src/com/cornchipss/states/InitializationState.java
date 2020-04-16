package com.cornchipss.states;

import org.newdawn.slick.Color;

import com.cornchipss.Cosmos;
import com.cornchipss.registry.Biospheres;
import com.cornchipss.registry.Blocks;
import com.cornchipss.registry.Options;
import com.cornchipss.rendering.Window;
import com.cornchipss.utils.Utils;

public class InitializationState implements GameState
{
	private volatile float progress = 0;
	
	private float delta = 0;
	private final float WAIT_TIME = 0.5f;
	
	@Override
	public void start()
	{
		new Thread()
		{
			@Override
			public void run()
			{
				Options.registerDefaults();
				progress = 1.0f / 2;
				
				Biospheres.registerBiospheres("com.cornchipss.world.biospheres");
				progress = 1;				
			}
		}.start();
	}
	
	@Override
	public void update()
	{
		if(progress >= 1.0f)
		{
			if(delta >= WAIT_TIME)
			{
				Cosmos.state(new PlayingState());
			}
			else
			{
				delta += Cosmos.deltaTime();
			}
		}
	}
	
	@Override
	public void render(Window window)
	{
		if(delta == 0)
			window.clear(1 - progress, 1 - progress, 1 - progress, 1);
		else
		{
			Color c = PlayingState.CLEAR_COLOR;
			float ratio = delta / WAIT_TIME;
			window.clear(ratio * c.r, ratio * c.g, ratio * c.b, 1);
		}
	}
	
	@Override
	public void end()
	{
		// Can't do this on seperate thread due to model creation
		// In future, separate this into model creation in dif thread, then swap to main thread to put models into opengl
		Blocks.registerBlocks();
		
		Utils.println("Initialization Phase Complete.");
	}
}

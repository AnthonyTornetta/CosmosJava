package com.cornchipss;

import org.lwjgl.glfw.GLFW;

import com.cornchipss.rendering.Window;
import com.cornchipss.states.GameState;
import com.cornchipss.states.InitializationState;
import com.cornchipss.utils.Input;
import com.cornchipss.utils.Timer;
import com.cornchipss.utils.Utils;

/*
 * Started August 5th, 2019
 */
public class Cosmos
{
	private static int startWidth = 1024, startHeight = 720;
	private static Window window;
	private static GameState state;
	
	public static GameState state() 
	{
		return state;
	}
	
	public static void state(GameState s)
	{
		if(state != null)
			state.end();
		
		state = s;
		
		state.start();
	}
	
	private static Cosmos instance;
	
	/**
	 * Max frames per second
	 */
	public static final int FPS_CAP = 60;
	
	private static long lastTimeTicked;
	private static float delta;
	
	/**
	 * Gets the time since last tick in seconds
	 * @return The time since last tick in seconds (approx {@link Cosmos#FPS_CAP} / 1000)
	 */
	public static float deltaTime() { return delta; }
	
	public Cosmos()
	{
		if(instance != null)
			throw new IllegalStateException("Cannot instantiate Cosmos twice!");
		instance = this;
	}
	
	public static Window window()
	{
		return window;
	}
	
	public void start()
	{
		window = new Window(startWidth, startHeight, "Cosmos");
		Input.setWindow(window);
		
		Timer updateTimer = new Timer();
		Timer secondsTimer = new Timer();
		
		int fps = 0;
		int ups = 0;
		
		Input.hideCursor(true);
		
		Input.update();
		
		lastTimeTicked = System.nanoTime();
		
		state(new InitializationState());
		
		while(!window.shouldClose() && !Input.isKeyDown(GLFW.GLFW_KEY_ESCAPE))
		{
			if(Input.isKeyJustDown(GLFW.GLFW_KEY_F1))
				Input.toggleCursor();
			
			while(updateTimer.getDeltaMillis() >= 1000 / FPS_CAP)
			{
				delta = (System.nanoTime() - lastTimeTicked) / (float)1e9;
				
				state.update();
				
				lastTimeTicked = System.nanoTime();
				updateTimer.subtractTimeMilli(1000 / FPS_CAP);
				ups++;
				
				Input.update();
			}
			
			while(secondsTimer.getDeltaMillis() >= 1000)
			{
				Utils.println("FPS/UPS: " + fps + " / " + ups);
				fps = ups = 0;
				secondsTimer.subtractTimeMilli(1000);
			}
			
			long sleep = 1000 / FPS_CAP - updateTimer.getDeltaMillis();
			Timer.sleep(sleep);
			
			state.render(window);
			
			window.update();

			fps++;
		}
		
		window.destroy();
		
		System.exit(0);
	}
	
	public Window getWindow()
	{
		return window;
	}

	public static Cosmos getInstance()
	{
		return instance;
	}
}
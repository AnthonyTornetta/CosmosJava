package com.cornchipss.cosmos.client;

import org.lwjgl.glfw.GLFW;

import com.cornchipss.cosmos.game.ClientGame;
import com.cornchipss.cosmos.netty.PacketTypes;
import com.cornchipss.cosmos.registry.Initializer;
import com.cornchipss.cosmos.rendering.Window;
import com.cornchipss.cosmos.utils.DebugMonitor;
import com.cornchipss.cosmos.utils.Logger;
import com.cornchipss.cosmos.utils.io.Input;

public class Client implements Runnable
{
	private Window window;
	
	private static Client instance;
	
	public Client()
	{
		if(instance != null)
			throw new IllegalStateException("Cannot have more than 1 running clients!");
		instance = this;
	}
	
	private volatile boolean running = true;
	private ClientGame game;
	
	@Override
	public void run()
	{
		Logger.LOGGER.setLevel(Logger.LogLevel.DEBUG);
		
		window = new Window(1024, 720, "Cosmos");
		
		Initializer loader = new Initializer();
		loader.init();
		
		CosmosNettyClient nettyClient = new CosmosNettyClient();
		
		Thread thread = new Thread(nettyClient);
		thread.start();
		
		game = new ClientGame(window, nettyClient);
		
		PacketTypes.registerAll();
		
		
		Input.setWindow(window);
		
		long t = System.currentTimeMillis();
		
		final int UPS_TARGET = 70;
		
		final int MILLIS_WAIT = 1000 / UPS_TARGET;
		
		long lastSecond = t;
		
		int ups = 0;
		float variance = 0;
		
		Input.hideCursor(true);
		
		Input.update();
		
		DebugMonitor.set("ups", 0);
		DebugMonitor.set("ups-variance", 0.0f);
		
		while(!window.shouldClose() && running)
		{
			if(window.wasWindowResized())
				game.onResize(window.getWidth(), window.getHeight());
			
			float delta = System.currentTimeMillis() - t; 
			
			if(delta < MILLIS_WAIT)
			{
				try
				{
					Thread.sleep(MILLIS_WAIT - (int)delta);
					
					delta = (System.currentTimeMillis() - t);
				}
				catch (InterruptedException e)
				{
					e.printStackTrace();
				}
			}
			
			delta /= 1000.0f;
			
			if(delta > variance)
				variance = delta;
			
			t = System.currentTimeMillis();
			
			if(lastSecond / 1000 != t / 1000)
			{
				DebugMonitor.set("ups", ups);
				DebugMonitor.set("ups-variance", variance);
				
				lastSecond = t;
				ups = 0;
				variance = 0;
			}
			ups++;
			
			if(Input.isKeyJustDown(GLFW.GLFW_KEY_F1))
				Input.toggleCursor();
			
			if(Input.isKeyDown(GLFW.GLFW_KEY_ESCAPE))
				running(false);
			
			game.update(delta);
			
			Input.update();
			
			window.clear(0, 0, 0, 1);
			
			game.render(delta);
			
			window.update();
		}
		
		window.destroy();
		
		try
		{
			thread.join();
		}
		catch (InterruptedException e)
		{
			e.printStackTrace();
		}
		
		Logger.LOGGER.info("Successfully closed.");
	}

	public static Client instance()
	{
		return instance;
	}

	public void running(boolean b)
	{
		game.running(b);
		running = b;
	}
	
	public boolean running()
	{
		return running;
	}
}

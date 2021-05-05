package com.cornchipss.cosmos.client;

import org.lwjgl.glfw.GLFW;

import com.cornchipss.cosmos.client.states.MainMenuState;
import com.cornchipss.cosmos.client.states.State;
import com.cornchipss.cosmos.netty.NettySide;
import com.cornchipss.cosmos.netty.PacketTypes;
import com.cornchipss.cosmos.registry.Initializer;
import com.cornchipss.cosmos.rendering.Window;
import com.cornchipss.cosmos.utils.GameLoop;
import com.cornchipss.cosmos.utils.Logger;
import com.cornchipss.cosmos.utils.io.Input;

public class Client implements Runnable
{
	private Window window;
	
	private static Client instance;
	
	public Client()
	{
		NettySide.initNettySide(NettySide.CLIENT);
		
		if(instance != null)
			throw new IllegalStateException("Cannot have more than 1 running clients!");
		instance = this;
	}
	
	private volatile boolean running = true;
	private State state;
	private CosmosNettyClient client;
	
	@Override
	public void run()
	{
		Logger.LOGGER.setLevel(Logger.LogLevel.DEBUG);
		
		window = new Window(1024, 720, "Cosmos");
		
		Initializer loader = new Initializer();
		loader.init();
		
		PacketTypes.registerAll();

		client = new CosmosNettyClient();
		
		Thread thread = new Thread(client);
		thread.start();		
		
		Input.setWindow(window);
		Input.update();
		
		state(new MainMenuState());

		GameLoop loop = new GameLoop((float delta) ->
		{
			if(Input.isKeyJustDown(GLFW.GLFW_KEY_F1))
				Input.toggleCursor();
			
			if(Input.isKeyDown(GLFW.GLFW_KEY_ESCAPE))
				running(false);
			
			state.update(delta);
			
			Input.update();
			
			window.clear(0, 0, 0, 1);
			
			state.render(delta);
			
			window.update();
			
			state.postUpdate();
			
			return running();
		}, 1000 / 70);
		
		loop.run();
		
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
		running = b;
	}
	
	public boolean running()
	{
		return !window.shouldClose() && running;
	}
	
	public void state(State state)
	{
		if(this.state != null)
			this.state.remove();
		
		this.state = state;
		state.init(window, client);
	}
	
	public State state()
	{
		return state;
	}
}

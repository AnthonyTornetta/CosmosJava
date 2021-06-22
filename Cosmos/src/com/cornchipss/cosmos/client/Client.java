package com.cornchipss.cosmos.client;

import java.io.IOException;

import org.lwjgl.glfw.GLFW;

import com.cornchipss.cosmos.client.states.MainMenuState;
import com.cornchipss.cosmos.client.states.State;
import com.cornchipss.cosmos.gui.text.Fonts;
import com.cornchipss.cosmos.netty.NettySide;
import com.cornchipss.cosmos.netty.PacketTypes;
import com.cornchipss.cosmos.registry.Initializer;
import com.cornchipss.cosmos.rendering.Window;
import com.cornchipss.cosmos.utils.GameLoop;
import com.cornchipss.cosmos.utils.Logger;
import com.cornchipss.cosmos.utils.Utils;
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
	
	private Thread nettyThread;
	
	public boolean connected()
	{
		return client != null;
	}
	
	public void connectTo(String ip, int port, String name) throws IOException
	{
		if(connected())
			throw new IllegalStateException("Already connected");
		
		client = new CosmosNettyClient();
		try
		{
			client.createConnection(ip, port, name);
		}
		catch(IOException ex)
		{
			client = null;
			throw ex;
		}
		
		nettyThread = new Thread(client);
		nettyThread.start();
	}
	
	public void disconnect() throws IOException, InterruptedException
	{
		if(!connected())
			throw new IllegalStateException("Not connected");
		
		client.disconnect();
		nettyThread.join();
		nettyThread = null;
	}
	
	@Override
	public void run()
	{
		Logger.LOGGER.setLevel(Logger.LogLevel.DEBUG);
		
		window = new Window(1024, 720, "Cosmos");
		
		Initializer loader = new Initializer();
		loader.init();
		
		PacketTypes.registerAll();
		
		Input.setWindow(window);
		Input.update();
		
		Fonts.init();
		
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
		
		Logger.LOGGER.info("Window Destroyed");
		
		try
		{
			Logger.LOGGER.info("Netty thread joined");
			if(nettyThread != null)
				nettyThread.join();
			Logger.LOGGER.info("Netty thread terminated gracefully");
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
		state.init(window);
	}
	
	public State state()
	{
		return state;
	}

	public boolean hasCompleteConnection()
	{
		return client != null && client.ready();
	}

	public CosmosNettyClient nettyClient()
	{
		return client;
	}

	public void quit()
	{
		running(false);
	}
}

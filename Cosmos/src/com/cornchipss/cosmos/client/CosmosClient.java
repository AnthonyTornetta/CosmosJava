package com.cornchipss.cosmos.client;

import java.io.IOException;

import com.cornchipss.cosmos.client.states.GameState;
import com.cornchipss.cosmos.client.states.MainMenuState;
import com.cornchipss.cosmos.client.states.ClientState;
import com.cornchipss.cosmos.gui.text.Fonts;
import com.cornchipss.cosmos.netty.NettySide;
import com.cornchipss.cosmos.registry.Initializer;
import com.cornchipss.cosmos.rendering.Window;
import com.cornchipss.cosmos.server.kyros.NettyClientObserver;
import com.cornchipss.cosmos.utils.GameLoop;
import com.cornchipss.cosmos.utils.Logger;
import com.cornchipss.cosmos.utils.io.Input;
import com.esotericsoftware.kryonet.Connection;

public class CosmosClient implements Runnable
{
	private Window window;

	private static CosmosClient instance;

	public CosmosClient()
	{
		NettySide.initNettySide(NettySide.CLIENT);

		if (instance != null)
			throw new IllegalStateException(
				"Cannot have more than 1 running clients!");
		instance = this;
	}

	private volatile boolean running = true;
	private ClientState state;
	private CosmosNettyClient client;

	public boolean connected()
	{
		return client != null;
	}

	public void connectTo(String ip, int port, String name) throws IOException
	{
		if (connected())
			throw new IllegalStateException("Already connected");

		client = new CosmosNettyClient();
		try
		{
			client.createConnection(ip, port, name);
		}
		catch (IOException ex)
		{
			client = null;
			throw ex;
		}

		client.run();

		client.addObserver(new NettyClientObserver()
		{
			@Override
			public boolean onReceiveObject(Connection connection, Object object)
			{
				return false;
			}

			@Override
			public void onDisconnect(Connection connection)
			{

			}

			@Override
			public void onConnect()
			{
				state(new GameState());
			}
		});
	}

	public void disconnect() throws IOException, InterruptedException
	{
		if (!connected())
			throw new IllegalStateException("Not connected");

		client.disconnect();

		state(new MainMenuState());
	}

	@Override
	public void run()
	{
		Logger.LOGGER.setLevel(Logger.LogLevel.DEBUG);

		window = new Window(1024, 720, "Cosmos");

		Initializer loader = new Initializer();
		loader.init();

		Input.setWindow(window);
		Input.update();

		Fonts.init();

		state(new MainMenuState());

		GameLoop loop = new GameLoop((float delta) ->
		{
			state.update(delta);

			window.clear(0, 0, 0, 1);

			state.render(delta);

			Input.update();

			window.update();

			state.postUpdate();

			return running();
		}, 1000 / 70);

		loop.run();

		window.destroy();

		Logger.LOGGER.info("Window Destroyed");

		try
		{
			if (nettyClient() != null)
			{
				nettyClient().disconnect();
				Logger.LOGGER.info("Netty connection closed gracefully");
			}
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}

		Logger.LOGGER.info("Successfully closed.");

		// Remove once this issue is fixed:
		// https://github.com/EsotericSoftware/kryonet/issues/142
		System.exit(0);
	}

	public static CosmosClient instance()
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

	public void state(ClientState state)
	{
		if (this.state != null)
			this.state.remove();

		this.state = state;
		state.init(window);
	}

	public ClientState state()
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

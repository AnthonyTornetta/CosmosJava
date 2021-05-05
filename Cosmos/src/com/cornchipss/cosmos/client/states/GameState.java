package com.cornchipss.cosmos.client.states;

import com.cornchipss.cosmos.client.CosmosNettyClient;
import com.cornchipss.cosmos.game.ClientGame;
import com.cornchipss.cosmos.rendering.Window;
import com.cornchipss.cosmos.utils.io.Input;

public class GameState extends State
{
	private ClientGame game;
	private Window window;
	
	@Override
	public void init(Window window, CosmosNettyClient client)
	{
		game = new ClientGame(window, client);
		this.window = window;
		
		Input.hideCursor(true);
	}
	
	@Override
	public void update(float delta)
	{
		game.world().lock();
		game.update(delta);
		game.world().unlock();
	}

	@Override
	public void render(float delta)
	{
		game.render(delta);
	}

	@Override
	public void postUpdate()
	{
		if(window.wasWindowResized())
			game.onResize(window.getWidth(), window.getHeight());
		
		game.postUpdate();
	}
	
	@Override
	public void remove()
	{
		game.running(false);
	}
}

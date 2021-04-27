package com.cornchipss.cosmos.game;

import com.cornchipss.cosmos.world.World;

public abstract class Game
{
	private World world;
	
	public Game()
	{
		world = new World();
	}
	
	public void update(float delta)
	{
		world.lock();
		world.update(delta);
		world.unlock();
	}
	
	public World world()
	{
		return world;
	}
}

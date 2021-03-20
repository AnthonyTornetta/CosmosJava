package com.cornchipss.cosmos.server;

import com.cornchipss.cosmos.cameras.Camera;
import com.cornchipss.cosmos.cameras.GimbalLockCamera;
import com.cornchipss.cosmos.world.World;
import com.cornchipss.cosmos.world.entities.player.Player;

public class ServerPlayer extends Player
{
	private ServerClient serverClient;
	private String name;
	
	public ServerPlayer(World world, ServerClient serverClient, String name)
	{
		super(world);
		
		this.serverClient = serverClient;
		
		this.name = name;
	}

	@Override
	public void update(float delta)
	{
		
	}

	@Override
	public Camera camera()
	{
		return new GimbalLockCamera(this);
	}
	
	@Override
	public boolean equals(Object other)
	{
		if(other instanceof ServerPlayer)
		{
			ServerPlayer o = (ServerPlayer)other;
			return name().equals(o.name());
		}
		
		return false;
	}
	
	@Override
	public int hashCode()
	{
		return name.hashCode();
	}
	
	public String name() { return name; }
	public ServerClient client() { return serverClient; }
}

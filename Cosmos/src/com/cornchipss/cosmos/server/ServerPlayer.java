package com.cornchipss.cosmos.server;

import com.cornchipss.cosmos.cameras.Camera;
import com.cornchipss.cosmos.cameras.GimbalLockCamera;
import com.cornchipss.cosmos.physx.RigidBodyProxy;
import com.cornchipss.cosmos.server.kyros.ClientConnection;
import com.cornchipss.cosmos.world.World;
import com.cornchipss.cosmos.world.entities.player.Player;

public class ServerPlayer extends Player
{
	private ClientConnection connection;

	private Camera camera;

	public ServerPlayer(World world, ClientConnection connection, String name)
	{
		super(world, name);

//		movement = Movement.movement(MovementType.NONE);

		this.connection = connection;
	}

	@Override
	public void addToWorld(RigidBodyProxy rb)
	{
		super.addToWorld(rb);

		camera = new GimbalLockCamera(rb);
	}

	@Override
	public void update(float delta)
	{
		
	}

	@Override
	public Camera camera()
	{
		return camera;
	}

	public ClientConnection connection()
	{
		return connection;
	}
}

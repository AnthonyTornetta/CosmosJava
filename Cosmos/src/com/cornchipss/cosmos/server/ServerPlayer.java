package com.cornchipss.cosmos.server;

import com.cornchipss.cosmos.cameras.Camera;
import com.cornchipss.cosmos.cameras.GimbalLockCamera;
import com.cornchipss.cosmos.physx.Transform;
import com.cornchipss.cosmos.world.World;
import com.cornchipss.cosmos.world.entities.player.Player;

public class ServerPlayer extends Player
{
	private ClientConnection serverClient;
	
	private Camera camera;
	
	public ServerPlayer(World world, ClientConnection serverClient, String name)
	{
		super(world, name);
		
//		movement = Movement.movement(MovementType.NONE);
		
		this.serverClient = serverClient;
	}
	
	@Override
	public void addToWorld(Transform transform)
	{
		super.addToWorld(transform);
		
		camera = new GimbalLockCamera(transform);
	}

	@Override
	public void update(float delta)
	{
//		if(isPilotingShip())
//		{
//			Vector3f movementDir = movement.direction(new Vector3f());
//			
//			
//		}
	}

	@Override
	public Camera camera()
	{
		return camera;
	}
	
	public ClientConnection client() { return serverClient; }
}

package com.cornchipss.cosmos.server;

import com.cornchipss.cosmos.cameras.Camera;
import com.cornchipss.cosmos.cameras.GimbalLockCamera;
import com.cornchipss.cosmos.physx.Transform;
import com.cornchipss.cosmos.world.World;
import com.cornchipss.cosmos.world.entities.player.Player;

public class DummyPlayer extends Player
{
	private GimbalLockCamera cam;

	public DummyPlayer(World world, String name)
	{
		super(world, name);
	}

	@Override
	public void addToWorld(Transform transform)
	{
		super.addToWorld(transform);

		cam = new GimbalLockCamera(transform);
	}

	@Override
	public void update(float delta)
	{

	}

	@Override
	public Camera camera()
	{
		return cam;
	}
}

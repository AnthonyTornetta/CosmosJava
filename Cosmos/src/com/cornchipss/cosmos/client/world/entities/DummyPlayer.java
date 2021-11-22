package com.cornchipss.cosmos.client.world.entities;

import org.joml.Matrix4fc;

import com.cornchipss.cosmos.cameras.Camera;
import com.cornchipss.cosmos.cameras.GimbalLockCamera;
import com.cornchipss.cosmos.material.TexturedMaterial;
import com.cornchipss.cosmos.material.types.RawImageMaterial;
import com.cornchipss.cosmos.models.entities.PlayerModel;
import com.cornchipss.cosmos.physx.Transform;
import com.cornchipss.cosmos.rendering.IRenderable;
import com.cornchipss.cosmos.rendering.Mesh;
import com.cornchipss.cosmos.world.World;
import com.cornchipss.cosmos.world.entities.player.Player;

public class DummyPlayer extends Player implements IRenderable
{
	private GimbalLockCamera cam;

	private Mesh playerMesh;
	private TexturedMaterial playerMaterial;

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

	@Override
	public void updateGraphics()
	{
		if (playerMaterial == null)
		{
			playerMaterial = new RawImageMaterial("assets/images/atlas/player");
			playerMaterial.init();

			playerMesh = new PlayerModel(playerMaterial).createMesh(0, 0, 0, 1);
		}
	}

	@Override
	public void draw(Matrix4fc projectionMatrix, Matrix4fc camera, ClientPlayer p)
	{
		playerMaterial.use();

		playerMaterial.initUniforms(projectionMatrix, camera, body().transform().matrix(), false);

		playerMesh.prepare();
		playerMesh.draw();
		playerMesh.finish();

		playerMaterial.stop();
	}

	@Override
	public boolean shouldBeDrawn()
	{
		return playerMaterial != null && !isPilotingShip();
	}
}

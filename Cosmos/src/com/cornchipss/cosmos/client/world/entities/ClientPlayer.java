package com.cornchipss.cosmos.client.world.entities;

import java.awt.Color;
import java.io.IOException;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector3fc;
import org.lwjgl.glfw.GLFW;

import com.cornchipss.cosmos.blocks.Block;
import com.cornchipss.cosmos.blocks.BlockFace;
import com.cornchipss.cosmos.blocks.StructureBlock;
import com.cornchipss.cosmos.blocks.modifiers.IInteractable;
import com.cornchipss.cosmos.cameras.Camera;
import com.cornchipss.cosmos.cameras.GimbalLockCamera;
import com.cornchipss.cosmos.cameras.ShipCamera;
import com.cornchipss.cosmos.client.CosmosClient;
import com.cornchipss.cosmos.client.game.ClientGame;
import com.cornchipss.cosmos.netty.action.PlayerAction;
import com.cornchipss.cosmos.netty.packets.CreateShipPacket;
import com.cornchipss.cosmos.netty.packets.ExitShipPacket;
import com.cornchipss.cosmos.netty.packets.ModifyBlockPacket;
import com.cornchipss.cosmos.netty.packets.MovementPacket;
import com.cornchipss.cosmos.netty.packets.PlayerActionPacket;
import com.cornchipss.cosmos.netty.packets.PlayerInteractPacket;
import com.cornchipss.cosmos.netty.packets.PlayerPacket;
import com.cornchipss.cosmos.physx.Movement;
import com.cornchipss.cosmos.physx.Movement.MovementType;
import com.cornchipss.cosmos.physx.RigidBody;
import com.cornchipss.cosmos.physx.Transform;
import com.cornchipss.cosmos.physx.collision.obb.OBBCollider;
import com.cornchipss.cosmos.rendering.debug.DebugRenderer;
import com.cornchipss.cosmos.rendering.debug.DebugRenderer.DrawMode;
import com.cornchipss.cosmos.structures.Ship;
import com.cornchipss.cosmos.structures.Structure;
import com.cornchipss.cosmos.utils.Maths;
import com.cornchipss.cosmos.utils.io.Input;
import com.cornchipss.cosmos.world.World;
import com.cornchipss.cosmos.world.entities.player.Player;

public class ClientPlayer extends Player
{
	private Camera cam;

	public ClientPlayer(World world, String name)
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
		movement(Movement.movement(MovementType.NONE));

		if (Input.isKeyDown(GLFW.GLFW_KEY_W))
			movement().add(MovementType.FORWARD);
		if (Input.isKeyDown(GLFW.GLFW_KEY_S))
			movement().add(MovementType.BACKWARD);
		if (Input.isKeyDown(GLFW.GLFW_KEY_A))
			movement().add(MovementType.LEFT);
		if (Input.isKeyDown(GLFW.GLFW_KEY_D))
			movement().add(MovementType.RIGHT);
		if (Input.isKeyDown(GLFW.GLFW_KEY_E))
			movement().add(MovementType.UP);
		if (Input.isKeyDown(GLFW.GLFW_KEY_Q))
			movement().add(MovementType.DOWN);
		if (Input.isKeyDown(GLFW.GLFW_KEY_LEFT_SHIFT))
			movement().add(MovementType.STOP);

		Vector3f dRot = new Vector3f();

		dRot.y = (dRot.y() - Input.getMouseDeltaX() * 0.0025f);

		dRot.x = (dRot.x() - Input.getMouseDeltaY() * 0.0025f);

		movement().addDeltaRotation(dRot);

		if (!isPilotingShip())
		{
			handleHotbar();

			handleMovement(delta);

			handleInteractions();
			
			handleShipCreation();
		}
		else
		{
			if (Input.isMouseBtnJustDown(GLFW.GLFW_MOUSE_BUTTON_LEFT))
			{
				PlayerAction action = new PlayerAction.Builder().setFiring(true)
					.create();
				PlayerActionPacket p = new PlayerActionPacket(action);

				try
				{
					CosmosClient.instance().nettyClient().sendTCP(p);
				}
				catch (IOException e)
				{
					e.printStackTrace();
				}
			}

			if (Input.isKeyJustDown(GLFW.GLFW_KEY_R))
			{
				ExitShipPacket esp = new ExitShipPacket();

				try
				{
					CosmosClient.instance().nettyClient().sendTCP(esp);
				}
				catch (IOException e)
				{
					e.printStackTrace();
				}
			}
		}

		camera().update();

		MovementPacket packet = new MovementPacket(movement());
		PlayerPacket p = new PlayerPacket(this);

		CosmosClient.instance().nettyClient().sendUDP(packet);
		CosmosClient.instance().nettyClient().sendUDP(p);
	}
	
	@Override
	public void shipPiloting(Ship s)
	{
		super.shipPiloting(s);
		
		if(s == null)
			this.cam = new GimbalLockCamera(this.body().transform());
		else
			this.cam = new ShipCamera(s);
	}
	
	private void handleShipCreation()
	{
		if(Input.isKeyJustDown(GLFW.GLFW_KEY_K))
		{
			try
			{
				CosmosClient.instance().nettyClient().sendTCP(new CreateShipPacket());
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
	}

	private void handleInteractions()
	{
		Structure.RayRes sb = calculateLookingAt();

		if (sb != null)
		{
			OBBCollider c = sb.block().structure().obbForBlock(
				sb.block().structureX(), sb.block().structureY(),
				sb.block().structureZ());

			Matrix4f mat = new Matrix4f();

			mat.translate(c.center());

			c.orientation().applyRotation(mat);

			Vector3f hw = new Vector3f().set(c.halfwidths());
			hw.add(0.001f, 0.001f, 0.001f);

			// TODO: don't drwa this using the debug renderer
			boolean b = DebugRenderer.instance().enabled();
			DebugRenderer.instance().enabled(true);
			DebugRenderer.instance().drawRectangle(mat, hw, Color.yellow,
				DrawMode.LINES);
			DebugRenderer.instance().enabled(b);
		}

		if (Input.isKeyJustDown(GLFW.GLFW_KEY_F))
		{
			this.body().transform().position(new Vector3f());
		}

		if ((Input.isKeyJustDown(GLFW.GLFW_KEY_R)
			|| Input.isMouseBtnJustDown(GLFW.GLFW_MOUSE_BUTTON_1)
			|| Input.isMouseBtnJustDown(GLFW.GLFW_MOUSE_BUTTON_2)
			|| Input.isMouseBtnJustDown(GLFW.GLFW_MOUSE_BUTTON_3)))
		{

			if (sb != null)
			{
				Structure lookingAt = sb.block().structure();

				Block selectedBlock = null;

				selectedBlock = inventory().block(0, selectedInventoryColumn());

				if (Input.isMouseBtnJustDown(GLFW.GLFW_MOUSE_BUTTON_1))
				{
					ModifyBlockPacket packet = new ModifyBlockPacket(sb.block(),
						null);

					try
					{
						ClientGame.instance().nettyClient().sendTCP(packet);
					}
					catch (IOException e)
					{
						e.printStackTrace();
					}
				}
				else if (Input.isMouseBtnJustDown(GLFW.GLFW_MOUSE_BUTTON_2))
				{
					if (selectedBlock != null
						&& selectedBlock.canAddTo(lookingAt))
					{
						BlockFace face = sb.face();

						int xx = Maths.floor(sb.block().structureX()
							+ (face.getRelativePosition().x)),
							yy = Maths.floor(sb.block().structureY()
								+ (face.getRelativePosition().y)),
							zz = Maths.floor(sb.block().structureZ()
								+ (face.getRelativePosition().z));

						if (lookingAt.withinBlocks(xx, yy, zz)
							&& !lookingAt.hasBlock(xx, yy, zz))
						{
							ModifyBlockPacket packet = new ModifyBlockPacket(
								new StructureBlock(sb.block().structure(), xx,
									yy, zz),
								selectedBlock);

							try
							{
								ClientGame.instance().nettyClient()
									.sendTCP(packet);
							}
							catch (IOException e)
							{
								e.printStackTrace();
							}
						}
					}
				}
				else if (Input.isKeyJustDown(GLFW.GLFW_KEY_R))
				{
					if (sb.block().block() instanceof IInteractable)
					{
						PlayerInteractPacket packet = new PlayerInteractPacket(
							sb.block());

						try
						{
							CosmosClient.instance().nettyClient()
								.sendTCP(packet);
						}
						catch (IOException e)
						{
							e.printStackTrace();
						}
					}
				}
			}

		}
	}

	private void handleMovement(float delta)
	{
		Vector3f dVel = new Vector3f();

		if (movement().forward())
			dVel.add(camera().forward());
		if (movement().backward())
			dVel.sub(camera().forward());
		if (movement().right())
			dVel.add(camera().right());
		if (movement().left())
			dVel.sub(camera().right());
		if (movement().up())
			dVel.add(camera().up());
		if (movement().down())
			dVel.sub(camera().up());

		dVel.x = (dVel.x() * (delta * 1000));
		dVel.z = (dVel.z() * (delta * 1000));
		dVel.y = (dVel.y() * (delta * 1000));

		Vector3fc dRot = movement().deltaRotation();

		cam.rotate(dRot);

		Vector3f vel = new Vector3f(body().velocity());

		vel.mul(0.1f);

		vel.add(dVel);

		if (!Input.isKeyDown(GLFW.GLFW_KEY_LEFT_SHIFT))
			vel = Maths.safeNormalize(vel, 2.5f);
		else
			vel = Maths.safeNormalize(vel, 50.0f);

		if (Input.isKeyJustDown(GLFW.GLFW_KEY_SPACE))
			vel.y = (vel.y() + 5);

		body().velocity(vel);
	}

	private void handleHotbar()
	{
		if (Input.isKeyJustDown(GLFW.GLFW_KEY_0))
		{
			selectedInventoryColumn(9);
		}
		else
		{
			for (int key = GLFW.GLFW_KEY_1; key <= GLFW.GLFW_KEY_9 + 1; key++)
			{
				if (Input.isKeyJustDown(key))
				{
					selectedInventoryColumn(key - GLFW.GLFW_KEY_1);
					break;
				}
			}
		}
	}

	public Camera camera()
	{
		return cam;
	}

	@Override
	public void body(RigidBody b)
	{
		super.body(b);

		if (cam != null)
			cam.parent(b.transform());
	}
}

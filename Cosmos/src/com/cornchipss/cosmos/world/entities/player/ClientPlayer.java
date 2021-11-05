package com.cornchipss.cosmos.world.entities.player;

import java.io.IOException;

import org.joml.Vector3f;
import org.joml.Vector3fc;
import org.lwjgl.glfw.GLFW;

import com.cornchipss.cosmos.blocks.Block;
import com.cornchipss.cosmos.blocks.BlockFace;
import com.cornchipss.cosmos.blocks.Blocks;
import com.cornchipss.cosmos.blocks.StructureBlock;
import com.cornchipss.cosmos.blocks.modifiers.IInteractable;
import com.cornchipss.cosmos.cameras.Camera;
import com.cornchipss.cosmos.cameras.GimbalLockCamera;
import com.cornchipss.cosmos.client.Client;
import com.cornchipss.cosmos.game.ClientGame;
import com.cornchipss.cosmos.netty.packets.ClientInteractPacket;
import com.cornchipss.cosmos.netty.packets.ClientMovementPacket;
import com.cornchipss.cosmos.netty.packets.ExitShipPacket;
import com.cornchipss.cosmos.netty.packets.ModifyBlockPacket;
import com.cornchipss.cosmos.physx.Movement;
import com.cornchipss.cosmos.physx.Movement.MovementType;
import com.cornchipss.cosmos.physx.RigidBody;
import com.cornchipss.cosmos.physx.Transform;
import com.cornchipss.cosmos.structures.Structure;
import com.cornchipss.cosmos.utils.Maths;
import com.cornchipss.cosmos.utils.io.Input;
import com.cornchipss.cosmos.world.World;

public class ClientPlayer extends Player
{
	private GimbalLockCamera cam;

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

	private byte[] buffer = new byte[128];

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

		Vector3f dRot = new Vector3f();

		dRot.y = (dRot.y() - Input.getMouseDeltaX() * 0.0025f);

		dRot.x = (dRot.x() - Input.getMouseDeltaY() * 0.0025f);

		movement().addDeltaRotation(dRot);

		if (!isPilotingShip())
		{
			handleHotbar();

			handleMovement(delta);

			handleInteractions();
		}
		else if (Input.isKeyJustDown(GLFW.GLFW_KEY_R))
		{
			// shipPiloting(null);
			ExitShipPacket esp = new ExitShipPacket(buffer, 0);
			esp.init();
			try
			{
				Client.instance().nettyClient().sendTCP(esp);
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}

		camera().update();

		ClientMovementPacket cmp = new ClientMovementPacket(buffer, 0, movement());
		cmp.init();
		Client.instance().nettyClient().sendUDP(cmp);
	}

	private void handleInteractions()
	{
		if ((Input.isKeyJustDown(GLFW.GLFW_KEY_R)
			|| Input.isMouseBtnJustDown(GLFW.GLFW_MOUSE_BUTTON_1) || Input.isMouseBtnJustDown(GLFW.GLFW_MOUSE_BUTTON_2)
			|| Input.isMouseBtnJustDown(GLFW.GLFW_MOUSE_BUTTON_3)))
		{
			Structure.RayRes sb = calculateLookingAt();
			
			if (sb != null)
			{
				Structure lookingAt = sb.block().structure();

				Block selectedBlock = null;

				selectedBlock = inventory().block(0, selectedInventoryColumn());
			}
		}
/*
				if (Input.isMouseBtnJustDown(GLFW.GLFW_MOUSE_BUTTON_1))
				{
					byte[] buffer = new byte[64];
					ModifyBlockPacket packet = new ModifyBlockPacket(buffer, 0, lookingAt, sb.block().structureX(),
						sb.block().structureY(), sb.block().structureZ(), null);
					packet.init();
					ClientGame.instance().nettyClient().sendUDP(packet);
				}
				else if (Input.isMouseBtnJustDown(GLFW.GLFW_MOUSE_BUTTON_2))
				{
					if (selectedBlock != null && selectedBlock.canAddTo(lookingAt))
					{
						BlockFace face = sb.face();

						int xx = Maths.floor(sb.block().structureX() + (face.getRelativePosition().x * 2)),
							yy = Maths.floor(sb.block().structureY() + (face.getRelativePosition().y * 2)),
							zz = Maths.floor(sb.block().structureZ() + (face.getRelativePosition().z * 2));

						if (lookingAt.withinBlocks(xx, yy, zz) && !lookingAt.hasBlock(xx, yy, zz))
						{
							// lookingAt.block(xx, yy, zz, selectedBlock);
							byte[] buffer = new byte[64];
							ModifyBlockPacket packet = new ModifyBlockPacket(buffer, 0, lookingAt, xx, yy, zz,
								selectedBlock);
							packet.init();
							ClientGame.instance().nettyClient().sendUDP(packet);
						}
					}
				}
				else if (Input.isKeyJustDown(GLFW.GLFW_KEY_R))
				{
					if (sb.block() instanceof IInteractable)
					{
						ClientInteractPacket cip = new ClientInteractPacket(buffer, 0, new StructureBlock(lookingAt,
							sb.block().structureX(), sb.block().structureY(), sb.block().structureZ()));
						cip.init();

						try
						{
							Client.instance().nettyClient().sendTCP(cip);
						}
						catch (IOException e)
						{
							e.printStackTrace();
						}
					}
				}
			}
			
		}
		*/
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

		if (Input.isKeyDown(GLFW.GLFW_KEY_LEFT_CONTROL))
			dVel.mul(0.001f);

		Vector3fc dRot = movement().deltaRotation();

		cam.rotate(dRot);

		Vector3f vel = new Vector3f(body().velocity());

		if (Input.isKeyDown(GLFW.GLFW_KEY_LEFT_SHIFT))
			vel.mul(0.75f);

		vel.add(dVel);

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

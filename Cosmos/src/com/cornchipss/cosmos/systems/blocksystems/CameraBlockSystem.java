package com.cornchipss.cosmos.systems.blocksystems;

import java.util.ArrayList;
import java.util.List;

import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;

import com.cornchipss.cosmos.blocks.StructureBlock;
import com.cornchipss.cosmos.blocks.individual.ShipCoreBlock;
import com.cornchipss.cosmos.client.game.ClientGame;
import com.cornchipss.cosmos.netty.NettySide;
import com.cornchipss.cosmos.structures.Ship;
import com.cornchipss.cosmos.structures.Structure;
import com.cornchipss.cosmos.systems.BlockSystem;
import com.cornchipss.cosmos.systems.BlockSystemIDs;
import com.cornchipss.cosmos.utils.io.Input;

public class CameraBlockSystem extends BlockSystem
{
	private List<StructureBlock> cameras;

	private int selectedCamera;

	public CameraBlockSystem(Structure s)
	{
		super(s);

		selectedCamera = 0;
		cameras = new ArrayList<>();
	}

	@Override
	public void addBlock(StructureBlock added)
	{
		if (added.block() instanceof ShipCoreBlock)
		{
			if (cameras.size() == 0)
				cameras.add(added);
			else
				cameras.add(0, added); // Ship core should always be 0
		}
		else
			cameras.add(added);
	}

	@Override
	public void removeBlock(StructureBlock removed)
	{
		cameras.remove(removed);
		if (selectedCamera > cameras.size())
			selectedCamera = 0;
	}

	@Override
	public void update(float delta)
	{
		if (!(structure() instanceof Ship))
			return;

		if (NettySide.side() == NettySide.CLIENT && ClientGame.instance()
			.player().isPilotingShip() && ClientGame.instance()
			.player().shipPiloting().equals((Ship) structure()))
		{
			if (Input.isKeyJustDown(GLFW.GLFW_KEY_RIGHT))
			{
				selectedCamera++;
				selectedCamera %= cameras.size();
			}
			else if (Input.isKeyJustDown(GLFW.GLFW_KEY_LEFT))
			{
				selectedCamera--;
				if (selectedCamera < 0)
					selectedCamera = cameras.size() - 1;
			}
		}
	}

	@Override
	public String id()
	{
		return BlockSystemIDs.CAMERA_ID;
	}

	/**
	 * May be a camera, may be the ship core. This is where the player wants to
	 * view the world from
	 * 
	 * @return The selected block to view the world from
	 */
	public StructureBlock selectedViewportBlock()
	{
		return cameras.get(selectedCamera);
	}

	public Vector3f selectedViewportWorldPosition(Vector3f out)
	{
		structure().blockCoordsToWorldCoords(selectedViewportBlock().position(), out);
		return out;
	}
}

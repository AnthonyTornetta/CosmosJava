package com.cornchipss.cosmos.blocks.individual;

import com.cornchipss.cosmos.blocks.ShipBlock;
import com.cornchipss.cosmos.blocks.StructureBlock;
import com.cornchipss.cosmos.blocks.data.BlockData;
import com.cornchipss.cosmos.blocks.data.ShipCoreData;
import com.cornchipss.cosmos.blocks.modifiers.IInteractable;
import com.cornchipss.cosmos.blocks.modifiers.ISystemBlock;
import com.cornchipss.cosmos.models.blocks.ShipCoreModel;
import com.cornchipss.cosmos.structures.Ship;
import com.cornchipss.cosmos.structures.Structure;
import com.cornchipss.cosmos.systems.BlockSystemIDs;
import com.cornchipss.cosmos.world.entities.player.Player;

/**
 * <p>
 * The core block of any ship
 * </p>
 * <p>
 * If this is removed, then there is no ship
 * </p>
 */
public class ShipCoreBlock extends ShipBlock implements IInteractable, ISystemBlock
{
	private static final String[] systems = new String[] { BlockSystemIDs.CAMERA_ID };
	
	/**
	 * <p>
	 * The core block of any ship
	 * </p>
	 * <p>
	 * If this is removed, then there is no ship
	 * </p>
	 */
	public ShipCoreBlock()
	{
		super(new ShipCoreModel(), "ship_core", 10000, 100);
	}

	@Override
	public boolean canAddTo(Structure s)
	{
		return false; // The player cannot place this without creating a ship -
						// where the block is
						// automatically placed.
	}

	@Override
	public BlockData generateData(Structure s, int x, int y, int z)
	{
		ShipCoreData data = new ShipCoreData((Ship) s);
		return data;
	}

	@Override
	public void onInteract(StructureBlock s, Player p)
	{
		Ship ship = (Ship) s.structure(); // this will always be on a ship

		ship.setPilot(p);
	}

	@Override
	public String[] systemIds()
	{
		return systems;
	}
}

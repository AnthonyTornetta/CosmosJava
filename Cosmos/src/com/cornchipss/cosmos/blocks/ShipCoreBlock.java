package com.cornchipss.cosmos.blocks;

import com.cornchipss.cosmos.Player;
import com.cornchipss.cosmos.blocks.data.BlockData;
import com.cornchipss.cosmos.models.ShipCoreModel;
import com.cornchipss.cosmos.structures.Structure;
import com.cornchipss.cosmos.utils.Utils;

/**
 * <p>The core block of any ship</p>
 * <p>If this is removed, then there is no ship</p>
 */
public class ShipCoreBlock extends ShipBlock implements IHasData, IInteractable
{
	/**
	 * <p>The core block of any ship</p>
	 * <p>If this is removed, then there is no ship</p>
	 */
	public ShipCoreBlock()
	{
		super(new ShipCoreModel(), "ship_core");
	}

	@Override
	public BlockData generateData(Structure s, int x, int y, int z)
	{
		BlockData data = new BlockData();
		data.data("ship", s);
		return data;
	}

	@Override
	public void onInteract(Structure s, Player p)
	{
		Utils.println("Ship Core Used!");
	}
}

package com.cornchipss.cosmos.blocks.individual;

import com.cornchipss.cosmos.blocks.ShipBlock;
import com.cornchipss.cosmos.blocks.modifiers.ISystemBlock;
import com.cornchipss.cosmos.models.blocks.CameraModel;
import com.cornchipss.cosmos.systems.BlockSystemIDs;

public class ShipCameraBlock extends ShipBlock implements ISystemBlock
{
	private static final String[] systems = new String[] { BlockSystemIDs.CAMERA_ID };
	
	public ShipCameraBlock()
	{
		super(new CameraModel(), "camera", 2000, 100);
	}

	@Override
	public String[] systemIds()
	{
		return systems;
	}
}

package com.cornchipss.cosmos.server.game.world;

import com.cornchipss.cosmos.physx.PhysicalObject;
import com.cornchipss.cosmos.structures.ServerStructureObserver;
import com.cornchipss.cosmos.structures.Structure;
import com.cornchipss.cosmos.world.World;

public class ServerWorld extends World
{
	protected void addObjectDuringUnlock(PhysicalObject obj)
	{
		super.addObjectDuringUnlock(obj);

		if (obj instanceof Structure)
			((Structure)obj).addObserver(new ServerStructureObserver());
	}
}

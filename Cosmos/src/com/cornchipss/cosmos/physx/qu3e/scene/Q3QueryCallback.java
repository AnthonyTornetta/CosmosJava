package com.cornchipss.cosmos.physx.qu3e.scene;

import com.cornchipss.cosmos.physx.qu3e.collision.Q3Box;

public interface Q3QueryCallback
{
	boolean reportShape(Q3Box box);
}

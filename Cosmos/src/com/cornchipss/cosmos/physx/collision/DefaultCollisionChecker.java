package com.cornchipss.cosmos.physx.collision;

import org.joml.Vector3f;

import com.cornchipss.cosmos.physx.PhysicalObject;
import com.cornchipss.cosmos.physx.shapes.StructureShape;
import com.cornchipss.cosmos.structures.Structure;

public class DefaultCollisionChecker implements ICollisionChecker
{
	@Override
	public boolean colliding(PhysicalObject a, PhysicalObject b, Vector3f normal)
	{
		if(a instanceof Structure)
		{
			StructureShape shpA = ((Structure)a).shape();
			
			
		}
		return false;
	}
}

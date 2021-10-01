package com.cornchipss.cosmos.physx.collision;

import org.joml.AABBf;
import org.joml.Vector3f;
import org.joml.Vector3i;

import com.cornchipss.cosmos.physx.PhysicalObject;
import com.cornchipss.cosmos.physx.shapes.StructureShape;

public class IntersectionCollisionChecker implements ICollisionChecker
{
	@Override
	public boolean colliding(PhysicalObject a, PhysicalObject b, Vector3f normal)
	{
		AABBf boxA = a.aabb(a.position(), new AABBf());
		AABBf boxB = b.aabb(b.position(), new AABBf());
		
		// Step 1: 2 closest blocks
		// 			1a: 
		// Step 2: Check if colliding
		
		if(boxA.testAABB(boxB))
		{
			if(a.shape() instanceof StructureShape && b.shape() instanceof StructureShape)
			{
				StructureShape bShape = (StructureShape)b.shape();
				StructureShape aShape = (StructureShape)a.shape();
				
				for(int z = 0; z < aShape.structure().length(); z++)
				{
					for(int y = 0; y < aShape.structure().height(); y++)
					{
						for(int x = 0; x < aShape.structure().width(); x++)
						{
							if(aShape.structure().hasBlock(x, y, z))
							{
								Vector3f pt = aShape.structure().localCoordsToWorldCoords(new Vector3f(x, y, z));
								Vector3i pt2 = bShape.structure().worldCoordsToStructureCoords(pt);
								Vector3f actualPt = bShape.structure().localCoordsToWorldCoords(pt2.x, pt2.y, pt2.z);
								
								if(bShape.structure().hasBlock(pt2.x, pt2.y, pt2.z))
								{
									pt.sub(actualPt, normal);
									
									normal.normalize();
									
									return true;
								}
							}
						}
					}
				}
			}
		}
		
		return false;
	}
}

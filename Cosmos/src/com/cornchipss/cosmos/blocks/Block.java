package com.cornchipss.cosmos.blocks;

import javax.vecmath.Vector3f;

import com.bulletphysics.collision.shapes.BoxShape;
import com.bulletphysics.collision.shapes.CollisionShape;
import com.cornchipss.cosmos.models.CubeModel;
import com.cornchipss.cosmos.models.IHasModel;

public class Block implements IHasModel
{
	private CubeModel model;
	private static final BoxShape defaultCollisionShape = new BoxShape(new Vector3f(0.5f, 0.5f, 0.5f));
	
	public Block(CubeModel m)
	{
		this.model = m;
	}
	
	@Override
	public CubeModel model()
	{
		return model;
	}

	public CollisionShape collisionShape()
	{
		return defaultCollisionShape;
	}
}

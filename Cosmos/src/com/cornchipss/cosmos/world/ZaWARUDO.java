package com.cornchipss.cosmos.world;

import java.util.LinkedList;
import java.util.List;

import org.joml.Vector3f;

import com.cornchipss.cosmos.physx.RigidBody;

public class ZaWARUDO
{
	private List<RigidBody> bodies;
	
	public ZaWARUDO()
	{
		bodies = new LinkedList<>();
	}
	
	public void addRigidBody(RigidBody bdy)
	{
		bodies.add(bdy);
	}
	
	public void update(float delta)
	{
		Vector3f temp = new Vector3f();
		
		for(RigidBody b : bodies)
		{
			temp.set(b.velocity()).mul(delta);
			
			b.transform().position(temp.add(b.transform().position()));
		}
	}
}

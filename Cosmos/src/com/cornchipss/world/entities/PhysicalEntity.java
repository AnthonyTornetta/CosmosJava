package com.cornchipss.world.entities;

import java.util.ArrayList;
import java.util.List;

import org.joml.Vector3f;

import com.cornchipss.Game;
import com.cornchipss.physics.Transform;
import com.cornchipss.physics.collision.hitbox.Hitbox;
import com.cornchipss.utils.Maths;
import com.cornchipss.world.Location;
import com.cornchipss.world.blocks.BlockFace;

public abstract class PhysicalEntity extends Entity
{
	private float elasticity;
	
	public PhysicalEntity(float x, float y, float z, Hitbox hitbox, float elasticity)
	{
		super(x, y, z, hitbox);
		
		if(elasticity < 0)
			throw new IllegalArgumentException("Elasticity cannot be < 0! " + elasticity + " given.");
		
		this.elasticity = 0;
	}
	
	public List<BlockFace> updatePhysics()
	{
		Transform t = getTransform();
		
//		Utils.println(t.velocity());
		
		Vector3f newPos = Maths.add(t.position(), Maths.mul(t.velocity(), Game.deltaTime()));
//		
//		Location[][][] locations = getUniverse().getBlocksWithin(t.position(), getHitbox().getBoundingBox());
//		
//		// For the collision check
//		Transform tempTransform = new Transform(newPos, t.rotation());
//		
		List<BlockFace> hits = new ArrayList<>(6);
//		
//		for(int z = 0; z < locations.length; z++)
//		{
//			for(int y = 0; y < locations[z].length; y++)
//			{
//				for(int x = 0; x < locations[z][y].length; x++)
//				{	
//					if(locations[z][y][x] != null && locations[z][y][x].getBlock().isInteractable())
//					{
//						Hitbox hb = locations[z][y][x].getBlock().getHitbox();
//						
////						locations[z][y][x].setBlock(Blocks.air);
//						
//						if(Hitbox.isColliding(hb, getHitbox(), locations[z][y][x].getTransform(), tempTransform))
//						{
//							Utils.println("Collision!");
//							
//							BlockFace f = BlockFace.getClosestFace(t.position(), locations[z][y][x].getPosition());
//							
//							newPos = onCollide(locations[z][y][x], f);
//							
//							hits.add(f);
//						}
//					}
//				}
//			}
//		}
		
//		Utils.println(newPos);
		
		getTransform().position(newPos);
		
		return hits;
	}
	
	public Vector3f onCollide(Location l, BlockFace face)
	{
		Transform t = getTransform();
		
		Vector3f direction = face.getDirection();
		float newX = t.x() + t.velocity().x(), newY = t.y() + t.velocity().y(), newZ = t.z() + t.velocity().z();
		
		Vector3f displacement = Maths.mul(face.getDirection(), Maths.div(getHitbox().getBoundingBox(), 2), Maths.negative());
		
		Vector3f newVel = Maths.zero();
		
		if(direction.x() != 0)
		{
			newVel.x = Math.abs(t.velocity().x()) * face.getDirection().x * elasticity;
			
			newX = newVel.x + displacement.x + l.getPosition().x() + face.getRelativePosition().x;
		}
		if(direction.y() != 0)
		{
			newVel.y = Math.abs(t.velocity().y()) * face.getDirection().y * elasticity;
			
			newY = newVel.y + displacement.y + l.getPosition().y() + face.getRelativePosition().y;
		}
		if(direction.z() != 0)
		{
			newVel.z = Math.abs(t.velocity().z()) * face.getDirection().z * elasticity;
			
			newZ = newVel.z + displacement.z + l.getPosition().z() + face.getRelativePosition().z;
		}
		
		return new Vector3f(newX, newY, newZ);
	}

	public float getElasticity() { return elasticity; }
	public void setElasticity(float elasticity) { this.elasticity = elasticity; }
}

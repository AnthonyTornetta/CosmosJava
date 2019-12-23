package com.cornchipss.world.entities;

import java.util.ArrayList;
import java.util.List;

import org.joml.Vector3f;

import com.cornchipss.physics.Transform;
import com.cornchipss.physics.collision.hitbox.Hitbox;
import com.cornchipss.registry.Blocks;
import com.cornchipss.utils.Maths;
import com.cornchipss.utils.Utils;
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
		Vector3f newPos = Maths.add(getPosition(), getVelocity());
		
		Transform absTrans = getAbsoluteTransform();
		
		Location[][][] locations = getUniverse().getBlocksWithin(absTrans.getPosition(), getHitbox().getBoundingBox());
		
		// For the collision check
		Transform tempTransform = new Transform(newPos, getRotation());
		
		List<BlockFace> hits = new ArrayList<>(6);
		
		for(int z = 0; z < locations.length; z++)
		{
			for(int y = 0; y < locations[z].length; y++)
			{
				for(int x = 0; x < locations[z][y].length; x++)
				{	
					if(locations[z][y][x] != null && locations[z][y][x].getBlock().isInteractable())
					{
						Hitbox hb = locations[z][y][x].getBlock().getHitbox();
						
						locations[z][y][x].setBlock(Blocks.air);
						
						if(Hitbox.isColliding(hb, getHitbox(), locations[z][y][x].getTransform(), tempTransform))
						{
							Utils.println("Collision!");
							
							BlockFace f = BlockFace.getClosestFace(getPosition(), locations[z][y][x].getPosition());
							
							newPos = onCollide(locations[z][y][x], f);
							
							hits.add(f);
						}
					}
				}
			}
		}
		
		getTransform().translate(Maths.invert(getPosition()));
		getTransform().translate(newPos);
		
		return hits;
	}
	
	public Vector3f onCollide(Location l, BlockFace face)
	{
		Vector3f direction = face.getDirection();
		float newX = getX() + getVelocityX(), newY = getY() + getVelocityY(), newZ = getZ() + getVelocityZ();
		
		Vector3f displacement = Maths.mul(face.getDirection(), Maths.div(getHitbox().getBoundingBox(), 2), Maths.negative());
		
		if(direction.x() != 0)
		{
			getVelocity().x = Math.abs(getVelocityX()) * face.getDirection().x * elasticity;
			
			newX = getVelocityX() + displacement.x + l.getPosition().x + face.getRelativePosition().x;
		}
		if(direction.y() != 0)
		{
			getVelocity().y = Math.abs(getVelocityY()) * face.getDirection().y * elasticity;
			
			newY = getVelocityY() + displacement.y + l.getPosition().y + face.getRelativePosition().y;
		}
		if(direction.z() != 0)
		{
			getVelocity().z = Math.abs(getVelocityZ()) * face.getDirection().z * elasticity;
			
			newZ = getVelocityZ() + displacement.z + l.getPosition().z + face.getRelativePosition().z;
		}
		
		return new Vector3f(newX, newY, newZ);
	}

	public float getElasticity() { return elasticity; }
	public void setElasticity(float elasticity) { this.elasticity = elasticity; }
}

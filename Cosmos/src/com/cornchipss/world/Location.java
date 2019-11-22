package com.cornchipss.world;

import org.joml.Vector3f;
import org.joml.Vector3fc;

import com.cornchipss.physics.Transform;
import com.cornchipss.utils.Maths;
import com.cornchipss.world.blocks.Block;

public class Location
{
	private Universe universe;
	private Transform trans;
	
	private Block block;
	
	public Location()
	{
		this(null, null);
	}
	
	public Location(Vector3f position, Universe universe)
	{
		this.universe = universe;
		trans = new Transform(position, Maths.zero()); // TODO: get rotation
		
		block = getUniverse().getBlockAt(position);
	}
	
	public Transform getTransform() { return trans; }
	
	public Vector3fc getRotation() { return trans.getRotation(); }

	public Vector3f getPosition() { return trans.getPosition(); }
	public void setPosition(Vector3f position)
	{
		trans.setPosition(position);
		this.block = getUniverse().getBlockAt(getPosition());
	}
	
	public Block getBlock() { return block; }
	public void setBlock(Block block)
	{
		getUniverse().setBlockAt(getPosition(), block);
		this.block = block;
	}
	
	public Universe getUniverse() { return universe; }
	public void setUniverse(Universe universe)
	{
		this.universe = universe;
		block = universe.getBlockAt(trans.getPosition());
	}
}

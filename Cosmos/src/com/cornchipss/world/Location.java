package com.cornchipss.world;

import org.joml.Vector3f;

import com.cornchipss.world.blocks.Block;

public class Location
{
	private Vector3f position;
	private Universe universe;
	
	private Block block;
	
	public Location()
	{
		this(null, null);
	}
	
	public Location(Vector3f position, Universe universe)
	{
		setUniverse(universe);
		setPosition(position);
	}
	
	public Vector3f getPosition()
	{
		return position;
	}
	public void setPosition(Vector3f position)
	{
		this.position = position;
		this.block = getUniverse().getBlockAt(getPosition());
	}
	
	public Block getBlock()
	{
		return block;
	}
	public void setBlock(Block block)
	{
		getUniverse().setBlockAt(getPosition(), block);
		this.block = block;
	}
	
	public Universe getUniverse() {
		return universe;
	}
	public void setUniverse(Universe universe) {
		this.universe = universe;
	}
	
	
}

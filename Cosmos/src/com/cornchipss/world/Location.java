package com.cornchipss.world;

import org.joml.Vector3f;

import com.cornchipss.utils.Utils;
import com.cornchipss.world.blocks.Block;

public class Location
{
	private Vector3f position;
	private Vector3f relativeRotation;
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
		setRotation(Utils.zero()); // TODO
	}
	
	private void setRotation(Vector3f rot)
	{
		this.relativeRotation = rot;
	}
	public Vector3f getRelativeRotation() { return relativeRotation; }

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
	
	public Universe getUniverse()
	{
		return universe;
	}
	public void setUniverse(Universe universe)
	{
		this.universe = universe;
	}
	
	
}

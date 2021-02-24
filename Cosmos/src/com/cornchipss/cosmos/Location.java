package com.cornchipss.cosmos;

import org.joml.Vector3fc;

public class Location
{
	private Vector3fc position;
	private Structure struct;
	
	public Location(Vector3fc position, Structure struct)
	{
		this.position = position;
		this.struct = struct;
	}
	
	public Vector3fc position()
	{
		return position;
	}
	
	public Structure structure()
	{
		return struct;
	}
}

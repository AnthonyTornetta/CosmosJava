package com.cornchipss.cosmos;

public class Location
{
	private Vec3 position;
	private Structure struct;
	
	public Location(Vec3 position, Structure struct)
	{
		this.position = position;
		this.struct = struct;
	}
	
	public Vec3 position()
	{
		return position;
	}
	
	public Structure structure()
	{
		return struct;
	}
}

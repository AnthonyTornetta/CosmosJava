package com.cornchipss.world;

import com.cornchipss.world.sector.Sector;

public class Universe
{
	public static final int WIDTH = 10, HEIGHT = 10, LENGTH = 10;
	
	private Sector[][][] sectors;
	
	public Universe()
	{
		sectors = new Sector[LENGTH][HEIGHT][WIDTH];
	}
	
	public void setSector(int x, int y, int z, Sector sector)
	{
		sectors[z][y][x] = sector;
		sector.setUniverse(this);
		sector.setUniverseX(x);
		sector.setUniverseY(y);
		sector.setUniverseZ(z);
	}
	
	public Sector getSector(int x, int y, int z)
	{
		return sectors[z][y][x];
	}
}

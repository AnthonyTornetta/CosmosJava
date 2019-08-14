package com.cornchipss.world;

import org.joml.Vector3f;
import org.joml.Vector3i;

import com.cornchipss.world.sector.Sector;

public class Universe
{
	public static final int SECTORS_X = 10, SECTORS_Y = 10, SECTORS_Z = 10;
	
	public static final int WIDTH = Sector.WIDTH * SECTORS_X,
			HEIGHT = Sector.HEIGHT * SECTORS_Y,
			LENGTH = Sector.LENGTH * SECTORS_Z;
	
	private Sector[][][] sectors;
	
	public Universe()
	{
		sectors = new Sector[SECTORS_Z][SECTORS_Y][SECTORS_X];
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

	public Vector3i toSectorCoords(Vector3f c)
	{
		return new Vector3i((int)Math.round(c.x / (WIDTH / 2)),
				(int)Math.round(c.y / (HEIGHT / 2)),
				(int)Math.round(c.z / (WIDTH / 2)));
	}

	public Sector getSector(Vector3i sectorCoords)
	{
		return getSector(sectorCoords.x, sectorCoords.y, sectorCoords.z);
	}
}

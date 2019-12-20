package com.cornchipss.world;

import org.joml.Vector3f;
import org.joml.Vector3fc;
import org.joml.Vector3i;

import com.cornchipss.physics.shapes.Rectangle;
import com.cornchipss.registry.Blocks;
import com.cornchipss.utils.Maths;
import com.cornchipss.world.blocks.Block;
import com.cornchipss.world.planet.Planet;
import com.cornchipss.world.sector.Sector;

/**
 * A thing that holds things that hold things
 * @author Cornchip
 */
public class Universe
{
	public static final int SECTORS_X = 10, SECTORS_Y = 10, SECTORS_Z = 10;
	
	/**
	 * The total dimensions of a universe, in blocks
	 */
	public static final int WIDTH = Sector.DIMENSIONS * SECTORS_X,
			HEIGHT = Sector.DIMENSIONS * SECTORS_Y,
			LENGTH = Sector.DIMENSIONS * SECTORS_Z;
	
	private Sector[][][] sectors;
	
	/**
	 * A thing that holds things that hold things
	 */
	public Universe()
	{
		sectors = new Sector[SECTORS_Z][SECTORS_Y][SECTORS_X];
	}
	
	/**
	 * Sets the sector at given sector coordinates, relative to the center of the universe (we found it! It's 0, 0, 0!)
	 * @param x The x of the sector relative to the center of the universe
	 * @param y The y of the sector relative to the center of the universe
	 * @param z The z of the sector relative to the center of the universe
	 * @param sector The sector to set the universe of
	 */
	public void setSector(int x, int y, int z, Sector sector)
	{
		sectors[z + SECTORS_Z / 2][y + SECTORS_Y / 2][x + SECTORS_X / 2] = sector;
		sector.setUniverse(this);
		sector.setUniverseX(x);
		sector.setUniverseY(y);
		sector.setUniverseZ(z);
	}
	
	/**
	 * Gets the sector at given sector coordinates, relative to the center of the universe (we found it! It's 0, 0, 0!)
	 * @param x The x of the sector relative to the center of the universe
	 * @param y The y of the sector relative to the center of the universe
	 * @param z The z of the sector relative to the center of the universe
	 * @return The sector at the given coordinates
	 */
	public Sector getSector(int x, int y, int z)
	{
		return sectors[z + SECTORS_Z / 2][y + SECTORS_Y / 2][x + SECTORS_X / 2];
	}
	
	public Vector3i toSectorCoords(Vector3fc c)
	{
		return new Vector3i(
				(int)(c.x() / (WIDTH / 2) + .5),
				(int)(c.y() / (HEIGHT / 2) + .5),
				(int)(c.z() / (WIDTH / 2) + .5));
	}
	
	/**
	 * Gets the sector at given sector coordinates, relative to the center of the universe (we found it! It's 0, 0, 0!)
	 * @param sectorCoords The chords of the sector relative to the center of the universe
	 * @return The sector at the given coordinates
	 */
	public Sector getSector(Vector3i sectorCoords)
	{
		return getSector(sectorCoords.x, sectorCoords.y, sectorCoords.z);
	}
	
	/**
	 * Converts a coordinate (can be absolute, sector, or chunk) to a chunk coordinate
	 * @param coord The coordinate to convert
	 * @return The relative chunk coordinate
	 */
	public static int clampAbsoluteCoordToSectorCoord(float coord)
	{
		// half a sector since the sector's middle point is "0, 0, 0"
		final int hs = Sector.DIMENSIONS / 2;
		
		int c = Math.round(coord);
		int inverseSign = (int)-Math.signum(c);
		c = Math.abs(c);
		
		return inverseSign * (((c / hs) % 2 * hs) - c % hs);
	}
	
	public static Vector3f clampAbsoluteCoordsToSectorCoords(Vector3fc absolute)
	{
		return new Vector3f(
				clampAbsoluteCoordToSectorCoord(absolute.x()), 
				clampAbsoluteCoordToSectorCoord(absolute.y()), 
				clampAbsoluteCoordToSectorCoord(absolute.z()));
	}
	
	/**
	 * Converts a coordinate (can be absolute, sector, or chunk) to a chunk coordinate
	 * @param coord The coordinate to convert
	 * @return The relative chunk coordinate
	 */
	public static int clampSectorCoordsToChunkCoords(float coord)
	{
		// half a chunk since the chunk's middle point is "0, 0, 0"
		final int hc = Sector.CHUNK_DIMENSIONS / 2;
		
		int c = Math.round(coord);
		int inverseSign = c < 0 ? 1 : -1;
		c = Math.abs(c);
		
		return inverseSign * (((c / hc) % 2 * hc) - c % hc);
	}
	
	public static Vector3f clampSectorCoordsToChunkCoords(Vector3f sectorCoords)
	{
		return new Vector3f(
				clampSectorCoordsToChunkCoords(sectorCoords.x),
				clampSectorCoordsToChunkCoords(sectorCoords.y),
				clampSectorCoordsToChunkCoords(sectorCoords.z));
	}
	
	public Vector3f getClosestBlock(float x, float y, float z, int radius)
	{
		return getClosestBlock(new Vector3f(x, y, z), radius);
	}
	
	/**
	 * Gets the closest block to a given absolute position
	 * @param pos The absolute position
	 * @param radius The radius to search in
	 * @return The closest block's position in that radius, or null if none found
	 */
	public Vector3f getClosestBlock(Vector3f pos, int radius)
	{
		Vector3i coords = toSectorCoords(pos);		
		Vector3f sectorCoords = clampAbsoluteCoordsToSectorCoords(pos);
		
		Sector sector = getSector(coords);
		if(sector == null)
			return null;
		
		Planet planet = sector.getPlanet(Sector.chunkAtLocalCoords(sectorCoords));
		if(planet == null)
			return null;
		
		Vector3f chunkCoords = clampSectorCoordsToChunkCoords(sectorCoords);
		
		float dist = -1;
		Vector3f closestBlock = null;
		
		for(float z = chunkCoords.z - radius; z <= chunkCoords.z + radius; z++)
		{
			for(float y = chunkCoords.y - radius; y <= chunkCoords.y + radius; y++)
			{
				for(float x = chunkCoords.x - radius; x <= chunkCoords.x + radius; x++)
				{
					float dX = x - pos.x;
					float dY = y - pos.y;
					float dZ = z - pos.z;
					
					float distsqrd = (dX * dX + dY * dY + dZ * dZ);
					if(planet.hasBlockAt(x, y, z) && (dist == -1 || distsqrd < dist))
					{
						dist = distsqrd;
						closestBlock = new Vector3f(x, y, z);
					}
				}
			}
		}
		
		if(closestBlock != null)
			closestBlock.add(planet.getUniverseCoords());
		
		return closestBlock;
	}
	
	public Planet getPlanet(Vector3fc pos)
	{
		if(pos == null)
			throw new IllegalArgumentException("Position for the planet cannot be null!");
		
		Vector3i coords = toSectorCoords(pos);		
		Vector3f sectorCoords = clampAbsoluteCoordsToSectorCoords(pos);
		
		Sector sector = getSector(coords);
		return sector.getPlanet(Sector.chunkAtLocalCoords(sectorCoords));
	}
	
	/**
	 * Gets the block at a given absolute coordinate
	 * @param pos The position to search at
	 * @return The block at a given coordinate
	 */
	public Block getBlockAt(Vector3fc pos)
	{
		if(pos == null)
			throw new IllegalArgumentException("Position for the block cannot be null!");
		
		Planet planet = getPlanet(pos);
		
		if(planet == null)
			return null;
		
		Vector3f chunkCoords = clampSectorCoordsToChunkCoords(clampAbsoluteCoordsToSectorCoords(pos));
		
//		chunkCoords = Maths.rotatePoint(planet.getCombinedRotation(), chunkCoords);
		
//		Utils.println("CC");
//		Utils.println(chunkCoords);
		
		if(planet.hasBlockAt(chunkCoords))
			return planet.getBlock(new Vector3i((int)chunkCoords.x, (int)chunkCoords.y, (int)chunkCoords.z));
		
		return null;
	}
	
	/**
	 * Sets a block at the given absolute position
	 * @param pos The position to set
	 * @param block The block to set it to
	 */
	public void setBlockAt(Vector3f pos, Block block)
	{
		if(pos == null)
			throw new IllegalArgumentException("Position for the block cannot be null!");
		
		Vector3i coords = toSectorCoords(pos);
		Vector3f sectorCoords = clampAbsoluteCoordsToSectorCoords(pos);
		
		Sector sector = getSector(coords);
		Planet planet = sector.getPlanet(Sector.chunkAtLocalCoords(sectorCoords));
		Vector3f chunkCoords = clampSectorCoordsToChunkCoords(sectorCoords);
		
		if(planet.hasBlockAt(chunkCoords))
		{
			planet.setBlock((int)chunkCoords.x, (int)chunkCoords.y, (int)chunkCoords.z, block);
		}
	}

	public Location[][][] getBlocksWithin(Vector3fc position, Vector3fc dimensions)
	{
		int cZ = (int)(Math.ceil(Math.abs(dimensions.z()) / 2 + 1));
		int cY = (int)(Math.ceil(Math.abs(dimensions.y()) / 2 + 1));
		int cX = (int)(Math.ceil(Math.abs(dimensions.x()) / 2 + 1));
		
		Location[][][] locs = new Location[cZ * 2][cY * 2][cX * 2];
		
		Planet p = getPlanet(position);
		
		if(p != null)
		{
	  		Vector3f relativeToPlanetPos = Maths.rotatePoint(p.getCombinedRotation().invert(), position);
			
			for(int z = -cZ; z < cZ; z++)
			{
				for(int y = -cY; y < cY; y++)
				{
					for(int x = -cX; x < cX; x++)
					{
						Location loc = new Location(new Vector3f(
								x + (float)Math.round(relativeToPlanetPos.x()), 
								y + (float)Math.round(relativeToPlanetPos.y()), 
								z + (float)Math.round(relativeToPlanetPos.z())), this);
						
						if(loc.getBlock() != null)
						{
							locs[z + cZ][y + cY][x + cX] = loc;
						}
					}
				}
			}
		}
		
		return locs;
	}
	
	public Location[][][] getBlocksBetween(Vector3fc a, Vector3fc b)
	{		
		// Makes sure that corner1 has coordinate values smaller than corner2, and if not swaps them
		// This is perfectly fine to do because it just finds new corners of the square to use
		Vector3f corner1 = new Vector3f(), corner2 = new Vector3f();
		
		corner1.x = (float)Math.min(Math.floor(a.x()), Math.floor(b.x()));
		corner2.x = (float)Math.max(Math.ceil(a.x()), Math.floor(b.x()));
		
		corner1.y = (float)Math.min(Math.floor(a.y()), Math.floor(b.y()));
		corner2.y = (float)Math.max(Math.ceil(a.y()), Math.floor(b.y()));
		
		corner1.z = (float)Math.min(Math.floor(a.z()), Math.floor(b.z()));
		corner2.z = (float)Math.max(Math.ceil(a.z()), Math.floor(b.z()));
		
		int floorZ = (int)Math.floor(corner1.z),
			floorY = (int)Math.floor(corner1.y),
			floorX = (int)Math.floor(corner1.x);
		
		int ceilZ = (int)Math.ceil(corner2.z),
			ceilY = (int)Math.ceil(corner2.y),
			ceilX = (int)Math.ceil(corner2.x);
		
		// Allows for a and b to be swapped
		int difZ = Math.abs(ceilZ - floorZ);
		int difY = Math.abs(ceilY - floorY);
		int difX = Math.abs(ceilX - floorX);
		
		
		Location[][][] blocks = new Location[difZ + 1][difY + 1][difX + 1];
		
		for(int z = floorZ; z <= ceilZ; z++)
		{
			for(int y = floorY; y <= ceilY; y++)
			{
				for(int x = floorX; x <= ceilX; x++)
				{
					Location loc = new Location(new Vector3f(x, y, z), this);
										
					if(loc.getBlock() != null)
					{
						blocks[z - floorZ][y - floorY][x - floorX] = loc;
					}
				}
			}
		}
		
		return blocks;
	}

	public Location[][][] getBlocksWithin(Rectangle rect)
	{
		return getBlocksWithin(rect.getPosition(), rect.getDimensions());
	}

	public void explode(Location location, int explosionRadius)
	{
		for(int dz = -explosionRadius; dz <= explosionRadius; dz++)
		{
			for(int dy = -explosionRadius; dy <= explosionRadius; dy++)
			{
				for(int dx = -explosionRadius; dx <= explosionRadius; dx++)
				{
					if(dz * dz + dy * dy + dx * dx <= explosionRadius * explosionRadius)
					{
						setBlockAt(Maths.add(location.getPosition(), new Vector3f(dx, dy, dz)), Blocks.air);
					}
				}
			}
		}
	}
}

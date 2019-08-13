package com.cornchipss.world.sector;

import java.util.Random;

import com.cornchipss.rendering.PlanetRenderer;
import com.cornchipss.world.Universe;
import com.cornchipss.world.entities.Player;
import com.cornchipss.world.generation.PlanetGenerator;
import com.cornchipss.world.planet.Planet;

/**
 * A big thing that holds other smaller things (wow)
 */
public class Sector
{
	/**
	 * How many "chunks" of structures there are in each direction
	 */
	public static final int CHUNKS = 16;
	
	/**
	 * How long a chunk is in block in any direction from one side to the other
	 */
	public static final int CHUNK_DIMENSIONS = 500;
	
	/**
	 * The dimensions of a sector in blocks
	 */
	public static final int WIDTH = CHUNKS * CHUNK_DIMENSIONS, 
			HEIGHT = CHUNKS * CHUNK_DIMENSIONS, 
			LENGTH = CHUNKS * CHUNK_DIMENSIONS;
	
	public static final int CHUNK_OFFSET = CHUNKS / 2;
	
	/**
	 * The universe it is a part of
	 */
	private Universe universe;
	
	/**
	 * A temp replacement for "chunks", currently a sector can only contain planets.
	 * TODO: In future they can hold stars, asteroids, stations, etc.
	 */
	private Planet[][][] planets; // TODO: Make chunk thingy
	
	/**
	 * The relative coordinates to the center of the universe (we finally found the center!), not in units of sectors
	 */
	private int universeX, universeY, universeZ;
	
	/**
	 * Used to generate planets within the sector
	 */
	private PlanetGenerator planetGenerator;
	
	/**
	 * A big thing that holds other smaller things
	 * @param x How many sectors away from center of universe, x edition
	 * @param y How many sectors away from center of universe, y edition
	 * @param z How many sectors away from center of universe, z edition
	 */
	public Sector(int x, int y, int z)
	{
		planets = new Planet[CHUNKS][CHUNKS][CHUNKS];
		
		planetGenerator = new PlanetGenerator(System.nanoTime());
	}
	
	private boolean firstUpdate = true;
	private float lastX = 0, lastY = 0, lastZ = 0;
	
	public void update(Player player)
	{
		if(firstUpdate || player.getX() != lastX || player.getY() != lastY || player.getZ() != lastZ)
		{
			int lastSectorX = fromAbsoluteX(lastX);
			int lastSectorY = fromAbsoluteX(lastY);
			int lastSectorZ = fromAbsoluteX(lastZ);
			
			lastX = player.getX();
			lastY = player.getY();
			lastZ = player.getZ();
			
			int secX = fromAbsoluteX(lastX);
			int secY = fromAbsoluteX(lastY);
			int secZ = fromAbsoluteX(lastZ);
			
			if(firstUpdate || lastSectorX != secX || lastSectorY != secY || lastSectorZ != secZ)
			{
				System.out.println("called @ " + secX + ", " + secY + ", " + secZ);
				generatePlanetsWithin(secX, secY, secZ, 1);
				System.out.println("DONE!");
				
				firstUpdate = false;
			}
		}
	}
	
	/**
	 * Sets a planet at a given coordinate relative to the sector's center, and is in units of chunks
	 * @param x The x coordinate within -CHUNK_OFFSET to CHUNK_OFFSET - 1
	 * @param y The x coordinate within -CHUNK_OFFSET to CHUNK_OFFSET - 1
	 * @param z The x coordinate within -CHUNK_OFFSET to CHUNK_OFFSET - 1
	 * @param planet The planet to set this chunk to
	 */
	public void setPlanet(int x, int y, int z, Planet planet)
	{
		planets[z + CHUNK_OFFSET][y + CHUNK_OFFSET][x + CHUNK_OFFSET] = planet;
		planet.setSector(this);
		planet.setPlanetX(x);
		planet.setPlanetY(y);
		planet.setPlanetZ(z);
	}
	
	/**
	 * Gets the planet at the given point
	 * @param x The x coordinate within -CHUNK_OFFSET to CHUNK_OFFSET - 1
	 * @param y The x coordinate within -CHUNK_OFFSET to CHUNK_OFFSET - 1
	 * @param z The x coordinate within -CHUNK_OFFSET to CHUNK_OFFSET - 1
	 * @return The planet at the given point
	 */
	public Planet getPlanet(int x, int y, int z)
	{
		return planets[z + CHUNK_OFFSET][y + CHUNK_OFFSET][x + CHUNK_OFFSET];
	}
	
	/**
	 * Gets the array that contains every planet in the sector - modifying this will change the actual sector, so be cautious
	 * @return The array that contains every planet in the sector
	 */
	public Planet[][][] getPlanets()
	{
		return planets;
	}

	/**
	 * The center of the sector, in absolute position terms
	 * @return The center of the sector, in absolute position terms
	 */
	public int getAbsoluteX()
	{
		return getUniverseX() * WIDTH;
	}
	
	/**
	 * The center of the sector, in absolute position terms
	 * @return The center of the sector, in absolute position terms
	 */
	public int getAbsoluteY()
	{
		return getUniverseY() * HEIGHT;
	}
	
	/**
	 * The center of the sector, in absolute position terms
	 * @return The center of the sector, in absolute position terms
	 */
	public int getAbsoluteZ()
	{
		return getUniverseZ() * LENGTH;
	}
	
	/**
	 * The sector's location in the universe, based on universal terms
	 * @return The sector's location in the universe, based on universal terms
	 */
	public int getUniverseX()
	{
		return universeX;
	}
	
	/**
	 * Sets the sector's location in the universe, based on universal terms
	 * @param universeZ The sector's location in the universe, based on universal terms
	 */
	public void setUniverseX(int universeX)
	{
		this.universeX = universeX;
	}

	/**
	 * The sector's location in the universe, based on universal terms
	 * @return The sector's location in the universe, based on universal terms
	 */
	public int getUniverseY()
	{
		return universeY;
	}

	/**
	 * Sets the sector's location in the universe, based on universal terms
	 * @param universeZ The sector's location in the universe, based on universal terms
	 */
	public void setUniverseY(int universeY)
	{
		this.universeY = universeY;
	}

	/**
	 * The sector's location in the universe, based on universal terms
	 * @return The sector's location in the universe, based on universal terms
	 */
	public int getUniverseZ()
	{
		return universeZ;
	}
	
	/**
	 * Sets the sector's location in the universe, based on universal terms
	 * @param universeZ The sector's location in the universe, based on universal terms
	 */
	public void setUniverseZ(int universeZ)
	{
		this.universeZ = universeZ;
	}
	
	/**
	 * Sets the universe this sector is a part of. Does not update any coordinates, so make sure to do those if needed
	 * @param universe The universe to set it to
	 */
	public void setUniverse(Universe universe) { this.universe = universe; }
	
	/**
	 * Gets the universe this sector is a part of
	 * @return The universe this sector is a part of
	 */
	public Universe getUniverse() { return universe; }
	
	/**
	 * Builds the terrain on a planet at the given coordinates - if no planet is found an illegal argument exception will be thrown
	 * @param x The x coordinate of the planet within -CHUNK_OFFSET to CHUNK_OFFSET - 1
	 * @param y The y coordinate of the planet within -CHUNK_OFFSET to CHUNK_OFFSET - 1
	 * @param z The z coordinate of the planet within -CHUNK_OFFSET to CHUNK_OFFSET - 1
	 */
	public void generatePlanet(int x, int y, int z)
	{
		generatePlanet(x, y, z, true);
	}
	
	/**
	 * Builds the terrain on a planet at the given coordinates - if no planet is found an illegal argument exception will be thrown
	 * @param x The x coordinate of the planet within -CHUNK_OFFSET to CHUNK_OFFSET - 1
	 * @param y The y coordinate of the planet within -CHUNK_OFFSET to CHUNK_OFFSET - 1
	 * @param z The z coordinate of the planet within -CHUNK_OFFSET to CHUNK_OFFSET - 1
	 * @param render Whether or not to render the model
	 */
	public void generatePlanet(int x, int y, int z, boolean render)
	{
		Thread thread = new Thread(new Runnable()
		{
			@Override
			public void run()
			{
				if(getPlanet(x, y, z) == null)
					throw new IllegalArgumentException("No planet found at " + x + ", " + y + ", " + z + ".");
				
				planetGenerator.generatePlanet(getPlanet(x, y, z), render, 20);
			}
		});
		
		thread.start();
	}
	
	/**
	 * Generates planets within a radius around a given point
	 * @param centerX The coordinate of the center within -CHUNK_OFFSET to CHUNK_OFFSET - 1
	 * @param centerY The coordinate of the center within -CHUNK_OFFSET to CHUNK_OFFSET - 1
	 * @param centerZ The coordinate of the center within -CHUNK_OFFSET to CHUNK_OFFSET - 1
	 * @param radius How far it should generate
	 */
	public void generatePlanetsWithin(int centerX, int centerY, int centerZ, int radius)
	{
		System.out.println("Called. " + centerX + ", " + centerY + ", " + centerZ + ", [" + radius + "]");
		for(int z = centerZ - radius >= -CHUNK_OFFSET ? centerZ - radius : -CHUNK_OFFSET; z <= (radius < CHUNK_OFFSET ? radius : CHUNK_OFFSET - 1); z++)
		{
			for(int y = centerY - radius >= -CHUNK_OFFSET ? centerY - radius : -CHUNK_OFFSET; y <= (radius < CHUNK_OFFSET ? radius : CHUNK_OFFSET - 1); y++)
			{
				for(int x = centerX - radius >= -CHUNK_OFFSET ? centerX - radius : -CHUNK_OFFSET; x <= (radius < CHUNK_OFFSET ? radius : CHUNK_OFFSET - 1); x++)
				{
					if(getPlanet(x, y, z) != null)
					{
						System.out.println("Non null: " + getPlanet(x, y, z).isGenerated());
						if(!getPlanet(x, y, z).isGenerated())
						{
							generatePlanet(x, y, z, false);
						}
					}
				}
			}
		}
	}
	
	/**
	 * Renders every planet within a given radius
	 * @param centerX The coordinate of the center within -CHUNK_OFFSET to CHUNK_OFFSET - 1
	 * @param centerY The coordinate of the center within -CHUNK_OFFSET to CHUNK_OFFSET - 1
	 * @param centerZ The coordinate of the center within -CHUNK_OFFSET to CHUNK_OFFSET - 1
	 * @param radius The radius to render
	 * @param renderer The renderer to use to render them
	 * @param player The player to render them all around
	 */
	public void renderPlanetsWithin(int centerX, int centerY, int centerZ, int radius, PlanetRenderer renderer, Player player)
	{
		renderer.setupRender(player);
		
		for(int z = centerZ - radius >= -CHUNK_OFFSET ? centerZ - radius : -CHUNK_OFFSET; z <= (radius < CHUNK_OFFSET ? radius : CHUNK_OFFSET - 1); z++)
		{
			for(int y = centerY - radius >= -CHUNK_OFFSET ? centerY - radius : -CHUNK_OFFSET; y <= (radius < CHUNK_OFFSET ? radius : CHUNK_OFFSET - 1); y++)
			{
				for(int x = centerX - radius >= -CHUNK_OFFSET ? centerX - radius : -CHUNK_OFFSET; x <= (radius < CHUNK_OFFSET ? radius : CHUNK_OFFSET - 1); x++)
				{
					if(getPlanet(x, y, z) != null)
					{
						long mils = System.currentTimeMillis();
						
						renderer.render(getPlanet(x, y, z));
						
						long time = System.currentTimeMillis() - mils;
						if(time > 16)
							System.out.println("Longer render time @ chunk " + x + ", " + y + ", " + z + ": " + time + "ms");
					}
				}
			}
		}
		
		renderer.stopRender();
	}
	
	public void renderPlanetsWithin(int radius, PlanetRenderer renderer, Player player)
	{
		renderPlanetsWithin(fromAbsoluteX(player.getX()), fromAbsoluteY(player.getY()), fromAbsoluteZ(player.getZ()), radius, renderer, player);
	}
	
	public static int fromAbsoluteX(float x)
	{
		return (int) Math.round(x / CHUNK_DIMENSIONS);
	}
	
	public static int fromAbsoluteY(float y)
	{
		return (int) Math.round(y / CHUNK_DIMENSIONS);
	}
	
	public static int fromAbsoluteZ(float z)
	{
		return (int) Math.round(z / CHUNK_DIMENSIONS);
	}
	
	/**
	 * Generates the sector with empty planets, and does not build any terrain
	 */
	public void generate()
	{
		Random random = new Random();
		
		System.out.println("Generate");
		for(int z = -CHUNK_OFFSET; z < CHUNK_OFFSET; z++)
		{
			for(int y = -CHUNK_OFFSET; y < CHUNK_OFFSET; y++)
			{
				for(int x = -CHUNK_OFFSET; x < CHUNK_OFFSET; x++)
				{
					if(random.nextInt(3) == 0 || x == 0 && y == 0 && z == 0 || true)
					{
						int xz = random.nextInt(100) + 100;
						if(xz % 2 != 0)
							xz++;
						setPlanet(x, y, z, new Planet(xz, 256, xz));
						
						System.out.println("Planet created @ " + x + "," + y + "," + z + "!");
					}
				}
			}
		}
	}
}
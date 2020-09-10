package com.cornchipss.world.sector;

import java.util.HashSet;
import java.util.Random;

import org.joml.Vector3f;
import org.joml.Vector3i;
import org.lwjgl.glfw.GLFW;

import com.cornchipss.Cosmos;
import com.cornchipss.physics.Transform;
import com.cornchipss.registry.Biospheres;
import com.cornchipss.registry.Blocks;
import com.cornchipss.rendering.BlockStructureRenderer;
import com.cornchipss.utils.Input;
import com.cornchipss.utils.Maths;
import com.cornchipss.utils.Utils;
import com.cornchipss.world.Universe;
import com.cornchipss.world.biospheres.Biosphere;
import com.cornchipss.world.entities.Player;
import com.cornchipss.world.planet.Planet;
import com.cornchipss.world.structures.BlockStructure;

import libs.noise.SimplexNoise;

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
	public static final int DIMENSIONS = CHUNKS * CHUNK_DIMENSIONS;

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
	private SimplexNoise noiseMaker;

	/**
	 * A big thing that holds other smaller things
	 */
	public Sector()
	{
		planets = new Planet[CHUNKS][CHUNKS][CHUNKS];

		noiseMaker = new SimplexNoise(System.nanoTime());
	}

	private boolean firstUpdate = true;
	private float lastX = 0, lastY = 0, lastZ = 0;

	private BlockStructure sa, sb, sc;

	public void update(Player player)
	{
		float r = Maths.PI / 180 * 0.1f;
		
		for(int z = 0; z < planets.length; z++)
		{
			for(int y = 0; y < planets[z].length; y++)
			{
				for(int x = 0; x < planets[z][y].length; x++)
				{
					if(planets[z][y][x] != null)
					{
						planets[z][y][x].transform().rotateY(r);
					}
				}
			}
		}
		
		float x = player.transform().x(),
				y = player.transform().y(),
				z = player.transform().z();

		if(firstUpdate)
		{
			sa = new BlockStructure(1, 1, 1, 0, 0, 0)
			{
				@Override
				public boolean createsGravity()
				{
					return false;
				}
			};

			sb = new BlockStructure(1, 1, 1, 0, 0, 0)
			{
				@Override
				public boolean createsGravity()
				{
					return false;
				}
			};

			sc = new BlockStructure(1, 1, 1, 0, 0, 0)
			{
				@Override
				public boolean createsGravity()
				{
					return false;
				}
			};

			sa.setBlock(0, 0, 0, Blocks.snowstone);
			sb.setBlock(0, 0, 0, Blocks.grass);
			sc.setBlock(0, 0, 0, Blocks.sandstone);

			sa.setGenerated(true);
			sb.setGenerated(true);
			sc.setGenerated(true);

			sa.render();
			sb.render();
			sc.render();

			sa.setSector(this);
			sb.setSector(this);
			sc.setSector(this);

			sa.setSectorCoords(0, 0, 0);
			sb.setSectorCoords(0, 0, 0);
			sc.setSectorCoords(0, 0, 0);

			sb.transform().parent(sa.transform());
			sc.transform().parent(sb.transform());

			sa.transform().position(0, 128, 0);
			sb.transform().localPosition(0, 3, 0);
			sc.transform().localPosition(2, 0, 0);
		}

		if(firstUpdate || x != lastX || y != lastY || z != lastZ)
		{
			int lastSectorX = chunkAtLocalX(lastX);
			int lastSectorY = chunkAtLocalX(lastY);
			int lastSectorZ = chunkAtLocalX(lastZ);

			lastX = x;
			lastY = y;
			lastZ = z;

			int secX = chunkAtLocalX(lastX);
			int secY = chunkAtLocalX(lastY);
			int secZ = chunkAtLocalX(lastZ);

			if(firstUpdate || lastSectorX != secX || lastSectorY != secY || lastSectorZ != secZ)
			{
				generatePlanetsWithin(secX, secY, secZ, 1);

				firstUpdate = false;
			}
		}

		if(Input.isMouseBtnJustDown(GLFW.GLFW_MOUSE_BUTTON_LEFT))
		{
			if(sb.transform().hasParent())
				sb.transform().removeParent();
			else
				sb.transform().parent(sa.transform());
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

		if(planet != null)
		{
			planet.setSector(this);
			planet.setSectorCoords(x, y, z);
		}
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
	 * Gets the planet at the given point
	 * @param c The relative coordinates, each within -CHUNK_OFFSET to CHUNK_OFFSET - 1
	 * @return The planet at the given coordinates
	 */
	public Planet getPlanet(Vector3i c)
	{
		return getPlanet(c.x, c.y, c.z);
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
		return getUniverseX() * DIMENSIONS;
	}

	/**
	 * The center of the sector, in absolute position terms
	 * @return The center of the sector, in absolute position terms
	 */
	public int getAbsoluteY()
	{
		return getUniverseY() * DIMENSIONS;
	}

	/**
	 * The center of the sector, in absolute position terms
	 * @return The center of the sector, in absolute position terms
	 */
	public int getAbsoluteZ()
	{
		return getUniverseZ() * DIMENSIONS;
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

				Biosphere bio = Biospheres.newInstance(Biospheres.getBiosphereIds().get((int)(Math.random() * Biospheres.getBiosphereIds().size())));
				bio.setPlanet(getPlanet(x, y, z));
				bio.generate(render, 30, noiseMaker);
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
		int lowZ, highZ;
		lowZ = (int)Math.max(centerZ - radius, -CHUNK_OFFSET);
		highZ = (int)Math.min(centerZ + radius, CHUNK_OFFSET - 1);

		int lowY, highY;
		lowY = (int)Math.max(centerY - radius, -CHUNK_OFFSET);
		highY = (int)Math.min(centerY + radius, CHUNK_OFFSET - 1);

		int lowX, highX;
		lowX = (int)Math.max(centerX - radius, -CHUNK_OFFSET);
		highX = (int)Math.min(centerX + radius, CHUNK_OFFSET - 1);

		for(int z = lowZ; z <= highZ; z++)
		{
			for(int y = lowY; y <= highY; y++)
			{
				for(int x = lowX; x <= highX; x++)
				{
					if(getPlanet(x, y, z) != null)
					{
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
	 * Handles the planets that are currently rendering
	 * TODO: make this system more clear
	 */
	private volatile HashSet<Planet> renderers = new HashSet<>();

	/**
	 * Renders every planet within a given radius
	 * @param centerX The coordinate of the center within -CHUNK_OFFSET to CHUNK_OFFSET - 1
	 * @param centerY The coordinate of the center within -CHUNK_OFFSET to CHUNK_OFFSET - 1
	 * @param centerZ The coordinate of the center within -CHUNK_OFFSET to CHUNK_OFFSET - 1
	 * @param radius The radius to render
	 * @param renderer The renderer to use to render them
	 * @param player The player to render them all around
	 */
	public void renderPlanetsWithin(int centerX, int centerY, int centerZ, int radius, BlockStructureRenderer renderer, Player player)
	{
		renderer.setupRender(player);

		int lowZ, highZ;
		lowZ = (int)Math.max(centerZ - radius, -CHUNK_OFFSET);
		highZ = (int)Math.min(centerZ + radius, CHUNK_OFFSET - 1);

		int lowY, highY;
		lowY = (int)Math.max(centerY - radius, -CHUNK_OFFSET);
		highY = (int)Math.min(centerY + radius, CHUNK_OFFSET - 1);

		int lowX, highX;
		lowX = (int)Math.max(centerX - radius, -CHUNK_OFFSET);
		highX = (int)Math.min(centerX + radius, CHUNK_OFFSET - 1);



		for(int z = lowZ; z <= highZ; z++)
		{
			for(int y = lowY; y <= highY; y++)
			{
				for(int x = lowX; x <= highX; x++)
				{
					Planet p = getPlanet(x, y, z);

					if(p != null)
					{
						long mils = System.currentTimeMillis();

						if(!p.isRendered() && !renderers.contains(p))
						{
							// If need be, the planet's models are initialized & stored in memory
							renderers.add(p);
							new Thread(() ->
							{
								p.render();
								renderers.remove(p);
							}).start();
						}

						// Once it's models are created & ready, draw it to the screen
						if(p.isRendered())
						{
							renderer.render(p, player);
						}

						long time = System.currentTimeMillis() - mils;
						if(time > 16)
							Utils.println("Longer render time @ chunk " + x + ", " + y + ", " + z + ": " + time + "ms");
					}
				}
			}
		}

		if(sa != null && sb != null)
		{
			renderer.render(sa, player);
			renderer.render(sb, player);
			renderer.render(sc, player);

			sa.transform().rotateX(1 / 8.0f * Maths.TAU * Cosmos.deltaTime());
		}

		renderer.stopRender();
	}

	public void renderPlanetsWithin(int radius, BlockStructureRenderer renderer, Player player)
	{
		Transform t = player.transform();
		renderPlanetsWithin(chunkAtLocalX(t.x()), chunkAtLocalY(t.y()), chunkAtLocalZ(t.z()), radius, renderer, player);
	}

	public static int chunkAtLocalX(float x)
	{
		return (int) Math.round(x / CHUNK_DIMENSIONS);
	}

	public static int chunkAtLocalY(float y)
	{
		return (int) Math.round(y / CHUNK_DIMENSIONS);
	}

	public static int chunkAtLocalZ(float z)
	{
		return (int) Math.round(z / CHUNK_DIMENSIONS);
	}

	public static Vector3i chunkAtLocalCoords(Vector3f c)
	{
		return new Vector3i(chunkAtLocalX(c.x), chunkAtLocalY(c.y), chunkAtLocalZ(c.z));
	}

	/**
	 * Generates the sector with empty planets, and does not build any terrain
	 */
	public void generate()
	{
		Random random = new Random();

		for(int z = -CHUNK_OFFSET; z < CHUNK_OFFSET; z++)
		{
			for(int y = -CHUNK_OFFSET; y < CHUNK_OFFSET; y++)
			{
				for(int x = -CHUNK_OFFSET; x < CHUNK_OFFSET; x++)
				{
					if(x == 0 && y == 0 && z == 0 || random.nextInt(7) == 0)
					{
						int xz = random.nextInt(100) + 100;
						if(xz % 2 != 0)
							xz++;
						String id = Biospheres.getBiosphereIds().get((int)(Math.random() * Biospheres.getBiosphereIds().size()));
						setPlanet(x, y, z, new Planet(xz, 256, xz, Biospheres.newInstance(id)));
					}
				}
			}
		}
	}

	public BlockStructure sa()
	{
		return sa;
	}
}

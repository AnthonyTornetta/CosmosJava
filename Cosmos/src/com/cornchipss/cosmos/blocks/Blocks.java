package com.cornchipss.cosmos.blocks;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.cornchipss.cosmos.blocks.individual.ShipCameraBlock;
import com.cornchipss.cosmos.blocks.individual.EnergyStorageBlock;
import com.cornchipss.cosmos.blocks.individual.LaserCannonBlock;
import com.cornchipss.cosmos.blocks.individual.ReactorBlock;
import com.cornchipss.cosmos.blocks.individual.ShipCoreBlock;
import com.cornchipss.cosmos.blocks.individual.ThrusterBlock;
import com.cornchipss.cosmos.lights.LightSource;
import com.cornchipss.cosmos.models.blocks.CactusModel;
import com.cornchipss.cosmos.models.blocks.DirtModel;
import com.cornchipss.cosmos.models.blocks.GrassModel;
import com.cornchipss.cosmos.models.blocks.LeafModel;
import com.cornchipss.cosmos.models.blocks.LightModel;
import com.cornchipss.cosmos.models.blocks.LogModel;
import com.cornchipss.cosmos.models.blocks.SandModel;
import com.cornchipss.cosmos.models.blocks.SandStoneModel;
import com.cornchipss.cosmos.models.blocks.ShipHullModel;
import com.cornchipss.cosmos.models.blocks.StoneModel;

/**
 * The instance of every block in the base game
 */
public class Blocks
{
	private static List<Block> allBlocks;

	public static final Block GRASS = new Block(new GrassModel(), "grass", 10000, 100),
		DIRT = new Block(new DirtModel(), "dirt", 10000, 100),
		STONE = new Block(new StoneModel(), "stone", 20000, 150),
		LIGHT = new LitBlock(new LightModel(),
			new LightSource(16, 1.0f, 1.0f, 1.0f), "light", 5000, 100),
		LOG = new Block(new LogModel(), "log", 5000, 100),
		LEAF = new Block(new LeafModel(), "leaf", 500, 20),
		SHIP_CORE = new ShipCoreBlock(),
		SHIP_HULL = new Block(new ShipHullModel(), "ship_hull", 20000, 250),
		THRUSTER = new ThrusterBlock(),
		SAND = new Block(new SandModel(), "sand", 10000, 100),
		SAND_STONE = new Block(new SandStoneModel(), "sand_stone", 20000, 150),
		CACTUS = new Block(new CactusModel(), "cactus", 3000, 100),
		REACTOR = new ReactorBlock(), ENERGY_STORAGE = new EnergyStorageBlock(),
		LASER_CANNON = new LaserCannonBlock(), CAMERA = new ShipCameraBlock();

	/**
	 * Adds all the blocks to a list
	 */
	public static void init()
	{
		allBlocks = Collections.unmodifiableList(Arrays.asList(STONE, GRASS,
			DIRT, LIGHT, LOG, LEAF, SHIP_CORE, SHIP_HULL, SAND, SAND_STONE,
			CACTUS, THRUSTER, REACTOR, ENERGY_STORAGE, LASER_CANNON, CAMERA));

		for (short i = 0; i < allBlocks.size(); i++)
			allBlocks.get(i).blockId((short) (i + 1));
	}

	public static Block fromNumericId(short id)
	{
		if (id == 0)
			return null;
		return allBlocks.get(id - 1);
	}

	/**
	 * Returns a list of all the blocks in the game - this cannot be modified.
	 * 
	 * @return a list of all the blocks in the game - this cannot be modified.
	 */
	public static List<Block> all()
	{
		return allBlocks;
	}
}

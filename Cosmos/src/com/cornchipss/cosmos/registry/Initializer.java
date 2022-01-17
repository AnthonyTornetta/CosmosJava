package com.cornchipss.cosmos.registry;

import com.cornchipss.cosmos.biospheres.DesertBiosphere;
import com.cornchipss.cosmos.biospheres.GrassBiosphere;
import com.cornchipss.cosmos.blocks.Blocks;
import com.cornchipss.cosmos.blocks.modifiers.BlockSystemFactories;
import com.cornchipss.cosmos.material.Materials;
import com.cornchipss.cosmos.systems.BlockSystemIDs;
import com.cornchipss.cosmos.systems.factories.EnergyGenerationSystemFactory;
import com.cornchipss.cosmos.systems.factories.EnergyStorageSystemFactory;
import com.cornchipss.cosmos.systems.factories.LaserCannonSystemFactory;
import com.cornchipss.cosmos.systems.factories.ThrusterSystemFactory;
import com.cornchipss.cosmos.utils.Logger;

public class Initializer
{
	protected void initBlockSystemFactories()
	{
		BlockSystemFactories.register(new EnergyGenerationSystemFactory(),
			BlockSystemIDs.POWER_GENERATOR_ID);
		BlockSystemFactories.register(new EnergyStorageSystemFactory(),
			BlockSystemIDs.POWER_STORAGE_ID);
		BlockSystemFactories.register(new ThrusterSystemFactory(),
			BlockSystemIDs.THRUSTER_ID);
		BlockSystemFactories.register(new LaserCannonSystemFactory(),
			BlockSystemIDs.LASER_CANNON_ID);
	}

	protected void initTerrainGeneration()
	{
		Biospheres.registerBiosphere(GrassBiosphere.class, "cosmos:grass");
		Biospheres.registerBiosphere(DesertBiosphere.class, "cosmos:desert");
	}

	protected void initBlocks()
	{
		Blocks.init();
	}

	protected void initMaterials()
	{
		Materials.initMaterials();
	}

	public void init()
	{
		Logger.LOGGER.info("Initializing...");

		initBlockSystemFactories();

		initBlocks();

		initTerrainGeneration();

		initMaterials();

		Logger.LOGGER.info("Initialization Complete");
	}
}

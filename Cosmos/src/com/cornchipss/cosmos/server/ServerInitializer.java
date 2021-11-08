package com.cornchipss.cosmos.server;

import com.cornchipss.cosmos.registry.Initializer;
import com.cornchipss.cosmos.utils.Logger;

public class ServerInitializer extends Initializer
{
	@Override
	public void init()
	{
		Logger.LOGGER.info("Initializing...");

		initBlockSystemFactories();

		initBlocks();

		initTerrainGeneration();

		Logger.LOGGER.info("Initialization Complete");
	}
}

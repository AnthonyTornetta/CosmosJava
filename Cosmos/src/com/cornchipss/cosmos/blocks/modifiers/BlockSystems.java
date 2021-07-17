package com.cornchipss.cosmos.blocks.modifiers;

import com.cornchipss.cosmos.systems.BlockSystem;
import com.cornchipss.cosmos.systems.EnergyGenerationSystem;
import com.cornchipss.cosmos.systems.EnergyStorageSystem;
import com.cornchipss.cosmos.systems.ThrusterSystem;

public class BlockSystems
{
	public static final BlockSystem
		POWER_GENERATOR = new EnergyGenerationSystem(),
		POWER_STORAGE = new EnergyStorageSystem(),
		THRUSTER = new ThrusterSystem();
}

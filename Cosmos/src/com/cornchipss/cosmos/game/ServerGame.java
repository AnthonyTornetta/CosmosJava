package com.cornchipss.cosmos.game;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import com.cornchipss.cosmos.biospheres.Biosphere;
import com.cornchipss.cosmos.blocks.Blocks;
import com.cornchipss.cosmos.physx.Transform;
import com.cornchipss.cosmos.registry.Biospheres;
import com.cornchipss.cosmos.structures.Planet;
import com.cornchipss.cosmos.structures.Ship;

public class ServerGame extends Game
{
	private Ship ship;
	private Planet mainPlanet;
	
	public ServerGame()
	{
		mainPlanet = new Planet(world(), 16*10, 16*6, 16*10, 1);
		mainPlanet.init();
		Biosphere def = Biospheres.newInstance("cosmos:desert");
		def.generatePlanet(mainPlanet);
		
		ship = new Ship(world(), 2);
		ship.init();
		
		try(DataInputStream shipStr = new DataInputStream(new FileInputStream(new File("assets/structures/ships/test.struct"))))
		{
			ship.read(shipStr);
		}
		catch(IOException ex)
		{
			ex.printStackTrace();
			ship.block(ship.width() / 2, ship.height() / 2, ship.length() / 2, Blocks.SHIP_CORE);
		}
		
		ship.addToWorld(new Transform());
		mainPlanet.addToWorld(new Transform(0, -mainPlanet.height(), 0));
	}
}

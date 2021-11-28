package com.cornchipss.cosmos.server.game;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import com.cornchipss.cosmos.biospheres.Biosphere;
import com.cornchipss.cosmos.blocks.Blocks;
import com.cornchipss.cosmos.game.Game;
import com.cornchipss.cosmos.netty.packets.StructureStatusPacket;
import com.cornchipss.cosmos.physx.Transform;
import com.cornchipss.cosmos.registry.Biospheres;
import com.cornchipss.cosmos.server.CosmosServer;
import com.cornchipss.cosmos.structures.Planet;
import com.cornchipss.cosmos.structures.Ship;
import com.cornchipss.cosmos.structures.Structure;
import com.cornchipss.cosmos.world.World;

public class ServerGame extends Game
{
	private Ship ship;
	private Planet mainPlanet;

	private static ServerGame instance;

	public ServerGame()
	{
		super(new World());
		
		if (instance != null)
			throw new IllegalStateException("ServerGame already created! Cannot have multiple games already running!");
		
		instance = this;

		mainPlanet = new Planet(world(), 16 * 8, 16 * 4, 16 * 8, 1);
		mainPlanet.init();

		Biosphere def = Biospheres.newInstance("cosmos:grass");
		def.generatePlanet(mainPlanet);

		ship = new Ship(world(), 2);
		ship.init();

		try (DataInputStream shipStr = new DataInputStream(
			new FileInputStream(new File("assets/structures/ships/gunship.struct"))))
		{
			ship.read(shipStr);
		}
		catch (IOException ex)
		{
			ex.printStackTrace();
			ship.block(ship.width() / 2, ship.height() / 2, ship.length() / 2, Blocks.SHIP_CORE);
		}

		ship.addToWorld(new Transform());
		mainPlanet.addToWorld(new Transform(0, -mainPlanet.height(), 0));
	}

	@Override
	public void update(float delta)
	{
		super.update(delta);

		for (Structure s : world().structures())
		{
			StructureStatusPacket smp = new StructureStatusPacket(s);

			CosmosServer.nettyServer().sendToAllUDP(smp);
		}
	}

	public static ServerGame instance()
	{
		return instance;
	}
}

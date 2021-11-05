package com.cornchipss.cosmos.game;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import com.cornchipss.cosmos.biospheres.Biosphere;
import com.cornchipss.cosmos.blocks.Blocks;
import com.cornchipss.cosmos.netty.packets.ShipMovementPacket;
import com.cornchipss.cosmos.physx.Transform;
import com.cornchipss.cosmos.registry.Biospheres;
import com.cornchipss.cosmos.server.Server;
import com.cornchipss.cosmos.structures.Planet;
import com.cornchipss.cosmos.structures.Ship;
import com.cornchipss.cosmos.structures.Structure;

public class ServerGame extends Game
{
	private Ship ship;
	private Planet mainPlanet;

	private static ServerGame instance;

	public ServerGame()
	{
		instance = this;

		mainPlanet = new Planet(world(), 16 * 8, 16 * 4, 16 * 8, 1);
		mainPlanet.init();

		Biosphere def = Biospheres.newInstance("cosmos:grass");
		def.generatePlanet(mainPlanet);

		ship = new Ship(world(), 2);
		ship.init();

		try (DataInputStream shipStr = new DataInputStream(
			new FileInputStream(new File("assets/structures/ships/test.struct"))))
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

	private byte[] buffer = new byte[128];

	@Override
	public void update(float delta)
	{
		super.update(delta);

		for (Structure s : world().structures())
		{
			if (s instanceof Ship)
			{
				Ship ship = (Ship) s;

				ShipMovementPacket smp = new ShipMovementPacket(buffer, 0, ship);
				smp.init();

				Server.nettyServer().sendToAllUDP(smp);
			}
		}
	}

	public static ServerGame instance()
	{
		return instance;
	}
}

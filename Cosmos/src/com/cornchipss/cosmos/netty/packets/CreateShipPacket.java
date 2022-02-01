package com.cornchipss.cosmos.netty.packets;

import org.joml.Vector3f;

import com.cornchipss.cosmos.blocks.Blocks;
import com.cornchipss.cosmos.client.CosmosNettyClient;
import com.cornchipss.cosmos.client.game.ClientGame;
import com.cornchipss.cosmos.server.CosmosNettyServer;
import com.cornchipss.cosmos.server.game.ServerGame;
import com.cornchipss.cosmos.server.kyros.ClientConnection;
import com.cornchipss.cosmos.structures.Ship;
import com.cornchipss.cosmos.utils.Logger;
import com.cornchipss.cosmos.world.entities.player.Player;

public class CreateShipPacket extends Packet
{
	private String pname;
	private int sid;

	public CreateShipPacket()
	{
	}

	private void createShip(Player p)
	{
		Ship s = new Ship(p.world(), sid);
		s.block(s.shipCoreBlockPosition(), Blocks.SHIP_CORE);

		s.addToWorld(p.body().transform().clone());

		s.body().transform()
			.position(s.body().transform().position().add(
				p.camera().forward().mul(5, new Vector3f()),
				new Vector3f()));
	}

	@Override
	public void receiveClient(CosmosNettyClient client, ClientGame game)
	{
		Player p = client.players().player(pname);

		createShip(p);
	}

	@Override
	public void receiveServer(CosmosNettyServer server, ServerGame game,
		ClientConnection c)
	{
		Player p = c.player();

		sid = p.world().nextStructureId();
		pname = c.player().name();

		createShip(p);

		Logger.LOGGER.info("Ship with ID " + sid + " created by " + p.name());
		
		server.sendToAllTCP(this);
	}
}

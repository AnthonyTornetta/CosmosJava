package com.cornchipss.cosmos.netty.packets;

import com.cornchipss.cosmos.client.CosmosNettyClient;
import com.cornchipss.cosmos.game.ClientGame;
import com.cornchipss.cosmos.game.ServerGame;
import com.cornchipss.cosmos.server.CosmosNettyServer;
import com.cornchipss.cosmos.server.kyros.ClientConnection;
import com.cornchipss.cosmos.structures.Ship;

public class ExitShipPacket extends Packet
{
	private int sid;

	public ExitShipPacket()
	{

	}

	@Override
	public void receiveClient(CosmosNettyClient client, ClientGame game)
	{
		((Ship) game.world().structureFromID(sid)).setPilot(null);
	}

	@Override
	public void receiveServer(CosmosNettyServer server, ServerGame game, ClientConnection c)
	{
		if (!c.player().isPilotingShip())
			return;

		sid = c.player().shipPiloting().id();
		c.player().shipPiloting().setPilot(null);

		server.sendToAllTCP(this);
	}
}

package com.cornchipss.cosmos.netty.packets;

import com.cornchipss.cosmos.client.CosmosNettyClient;
import com.cornchipss.cosmos.game.ClientGame;
import com.cornchipss.cosmos.game.ServerGame;
import com.cornchipss.cosmos.server.CosmosNettyServer;
import com.cornchipss.cosmos.server.kyros.ClientConnection;

public abstract class Packet
{
	public abstract void receiveClient(CosmosNettyClient client, ClientGame game);
	
	public abstract void receiveServer(CosmosNettyServer server, ServerGame game, ClientConnection c);
}

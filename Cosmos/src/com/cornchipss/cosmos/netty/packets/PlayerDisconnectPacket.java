package com.cornchipss.cosmos.netty.packets;

import com.cornchipss.cosmos.client.CosmosNettyClient;
import com.cornchipss.cosmos.client.game.ClientGame;
import com.cornchipss.cosmos.server.CosmosNettyServer;
import com.cornchipss.cosmos.server.game.ServerGame;
import com.cornchipss.cosmos.server.kyros.ClientConnection;
import com.cornchipss.cosmos.world.entities.player.Player;

public class PlayerDisconnectPacket extends Packet
{
	private String name;

	public PlayerDisconnectPacket()
	{

	}

	public PlayerDisconnectPacket(Player p)
	{
		this.name = p.name();
	}

	@Override
	public void receiveClient(CosmosNettyClient client, ClientGame game)
	{
		client.players().removePlayer(client.players().player(name));
	}

	@Override
	public void receiveServer(CosmosNettyServer server, ServerGame game,
		ClientConnection c)
	{
		c.close();

	}
}

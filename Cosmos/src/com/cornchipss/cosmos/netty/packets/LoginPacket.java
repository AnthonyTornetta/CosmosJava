package com.cornchipss.cosmos.netty.packets;

import com.cornchipss.cosmos.client.CosmosNettyClient;
import com.cornchipss.cosmos.game.ClientGame;
import com.cornchipss.cosmos.game.ServerGame;
import com.cornchipss.cosmos.server.CosmosNettyServer;
import com.cornchipss.cosmos.server.DummyPlayer;
import com.cornchipss.cosmos.server.kyros.ClientConnection;
import com.cornchipss.cosmos.server.kyros.types.StatusResponse;

public class LoginPacket extends Packet
{
	private String name;
	
	public LoginPacket()
	{
		
	}
	
	public LoginPacket(String name)
	{
		this.name = name;
	}
	
	@Override
	public void receiveClient(CosmosNettyClient client, ClientGame game)
	{
		client.players().addPlayer(new DummyPlayer(game.world(), name));
	}

	@Override
	public void receiveServer(CosmosNettyServer server, ServerGame game, ClientConnection c)
	{
		if(server.players().nameTaken(name))
		{
			c.sendTCP(new StatusResponse(400, "Name Taken"));
		}
		else if(server.players().connectionRegistered(c))
		{
			c.sendTCP(new StatusResponse(400, "Already Connected!"));
		}
		else
		{
			c.player(server.players().createPlayer(game.world(), c, name));
			
			c.sendTCP(new StatusResponse(200, "Logged In"));
			
			server.sendToAllExceptTCP(this, c.player());
		}
	}
}

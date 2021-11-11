package com.cornchipss.cosmos.netty.packets;

import com.cornchipss.cosmos.client.CosmosNettyClient;
import com.cornchipss.cosmos.game.ClientGame;
import com.cornchipss.cosmos.game.ServerGame;
import com.cornchipss.cosmos.server.CosmosNettyServer;
import com.cornchipss.cosmos.server.DummyPlayer;
import com.cornchipss.cosmos.server.kyros.ClientConnection;

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
			c.sendTCP(new StatusPacket(400, "Name Taken"));
		}
		else if(server.players().connectionRegistered(c))
		{
			c.sendTCP(new StatusPacket(400, "Already Connected!"));
		}
		else
		{
			c.player(server.players().createPlayer(game.world(), c, name));
			
			c.sendTCP(new StatusPacket(200, "Logged In"));
			
			server.sendToAllExceptTCP(this, c.player());
			
			c.sendTCP(new StructurePacket(game.world().structureFromID(1)));
		}
	}
}

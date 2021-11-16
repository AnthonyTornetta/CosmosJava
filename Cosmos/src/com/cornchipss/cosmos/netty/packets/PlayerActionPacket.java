package com.cornchipss.cosmos.netty.packets;

import com.cornchipss.cosmos.client.CosmosNettyClient;
import com.cornchipss.cosmos.game.ClientGame;
import com.cornchipss.cosmos.game.ServerGame;
import com.cornchipss.cosmos.netty.action.PlayerAction;
import com.cornchipss.cosmos.server.CosmosNettyServer;
import com.cornchipss.cosmos.server.ServerPlayer;
import com.cornchipss.cosmos.server.kyros.ClientConnection;
import com.cornchipss.cosmos.world.entities.player.Player;

public class PlayerActionPacket extends Packet
{
	private PlayerAction a;
	private String name;
	
	public PlayerActionPacket()
	{
		
	}
	
	public PlayerActionPacket(PlayerAction a)
	{
		this.a = a;
	}

	@Override
	public void receiveClient(CosmosNettyClient client, ClientGame game)
	{
		Player p;
		if((p = client.players().player(name)).isPilotingShip())
		{
			p.shipPiloting().sendAction(a);
		}
	}

	@Override
	public void receiveServer(CosmosNettyServer server, ServerGame game, ClientConnection c)
	{
		Player p = c.player();
		if(p.isPilotingShip())
			p.shipPiloting().sendAction(a);
		
		name = p.name();
		
		for(ServerPlayer player : server.players())
		{
			player.connection().sendTCP(this);
		}
	}
}

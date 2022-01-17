package com.cornchipss.cosmos.netty.packets;

import com.cornchipss.cosmos.client.CosmosNettyClient;
import com.cornchipss.cosmos.client.game.ClientGame;
import com.cornchipss.cosmos.netty.action.PlayerAction;
import com.cornchipss.cosmos.server.CosmosNettyServer;
import com.cornchipss.cosmos.server.game.ServerGame;
import com.cornchipss.cosmos.server.kyros.ClientConnection;
import com.cornchipss.cosmos.world.entities.player.Player;

public class PlayerActionPacket extends Packet
{
	private int code;
	private String name;

	public PlayerActionPacket()
	{

	}

	public PlayerActionPacket(PlayerAction a)
	{
		this.code = a.code();
	}

	@Override
	public void receiveClient(CosmosNettyClient client, ClientGame game)
	{
		Player p;
		if ((p = client.players().player(name)).isPilotingShip())
		{
			p.shipPiloting().sendAction(new PlayerAction(code));
		}
	}

	@Override
	public void receiveServer(CosmosNettyServer server, ServerGame game,
		ClientConnection c)
	{
		Player p = c.player();
		if (p.isPilotingShip())
			p.shipPiloting().sendAction(new PlayerAction(code));

		name = p.name();

		server.sendToAllTCP(this);
	}
}

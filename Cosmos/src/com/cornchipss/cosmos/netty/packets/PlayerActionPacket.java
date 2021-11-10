package com.cornchipss.cosmos.netty.packets;

import java.io.IOException;

import com.cornchipss.cosmos.client.CosmosNettyClient;
import com.cornchipss.cosmos.client.ServerConnection;
import com.cornchipss.cosmos.netty.action.PlayerAction;
import com.cornchipss.cosmos.server.ClientConnection;
import com.cornchipss.cosmos.server.CosmosNettyServer;
import com.cornchipss.cosmos.server.ServerPlayer;
import com.cornchipss.cosmos.world.entities.player.Player;

public class PlayerActionPacket extends Packet
{
	private PlayerAction action;
	private Player p;

	public PlayerActionPacket()
	{

	}

	private PlayerActionPacket(byte[] buffer, int bufferOffset)
	{
		super(buffer, bufferOffset);
	}

	public PlayerActionPacket(byte[] buffer, int bufferOffset, PlayerAction action)
	{
		super(buffer, bufferOffset);

		this.action = action;
	}

	private PlayerActionPacket(byte[] buffer, int bufferOffset, PlayerAction action, Player p)
	{
		super(buffer, bufferOffset);

		this.action = action;
		this.p = p;
	}

	@Override
	public void init()
	{
		super.init();

		writeInt(action.code());

		if (p != null)
			writeString(p.name());
	}

	private byte[] nextBuffer = new byte[1024];	
	
	@Override
	public void onReceiveServer(byte[] data, int len, int offset, ClientConnection client, CosmosNettyServer server)
	{
		PlayerActionPacket packet = new PlayerActionPacket(data, offset);

		PlayerAction action = new PlayerAction(packet.readInt());

		Player p = server.players().player(client);

		if (p.isPilotingShip())
		{
			p.shipPiloting().sendAction(action);
		}
		
		PlayerActionPacket response = new PlayerActionPacket(nextBuffer, 0, action, p);
		
		response.init();

		for (ServerPlayer c : server.players().players())
		{
			try
			{
				c.client().sendTCP(response.buffer(), response.bufferLength());
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
	}

	@Override
	public void onReceiveClient(byte[] data, int len, int offset, ServerConnection server, CosmosNettyClient client)
	{
		PlayerActionPacket packet = new PlayerActionPacket(data, offset);
		PlayerAction action = new PlayerAction(packet.readInt());
		Player p = client.players().player(packet.readString());

		if (p.isPilotingShip())
		{
			p.shipPiloting().sendAction(action);
		}
	}
}

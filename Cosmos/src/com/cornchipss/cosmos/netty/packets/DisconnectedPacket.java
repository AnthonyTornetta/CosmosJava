package com.cornchipss.cosmos.netty.packets;

import com.cornchipss.cosmos.client.Client;
import com.cornchipss.cosmos.client.ServerConnection;
import com.cornchipss.cosmos.client.CosmosNettyClient;
import com.cornchipss.cosmos.server.CosmosNettyServer;
import com.cornchipss.cosmos.server.ClientConnection;

public class DisconnectedPacket extends Packet
{
	public DisconnectedPacket()
	{
		
	}
	
	public DisconnectedPacket(byte[] buffer, int bufferOffset)
	{
		super(buffer, bufferOffset);
	}
	
	@Override
	public void onReceiveServer(byte[] data, int len, int offset, ClientConnection client, CosmosNettyServer server)
	{
		server.players().removePlayer(client);
	}

	@Override
	public byte marker()
	{
		return 10;
	}

	@Override
	public void onReceiveClient(byte[] data, int len, int offset, ServerConnection server, CosmosNettyClient client)
	{
		server.socket().close();
		
		Client.instance().running(false);
	}
}

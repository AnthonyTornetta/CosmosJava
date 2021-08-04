package com.cornchipss.cosmos.netty.packets;

import com.cornchipss.cosmos.client.CosmosNettyClient;
import com.cornchipss.cosmos.client.ServerConnection;
import com.cornchipss.cosmos.server.ClientConnection;
import com.cornchipss.cosmos.server.CosmosNettyServer;

public class JoinFinishPacket extends Packet
{
	public JoinFinishPacket()
	{
		
	}
	
	public JoinFinishPacket(byte[] buffer, int bufferOffset)
	{
		super(buffer, bufferOffset);
	}
	
	@Override
	public void init()
	{
		super.init();
	}
	
	@Override
	public void onReceiveServer(byte[] data, int len, int offset,
			ClientConnection client, CosmosNettyServer server)
	{
		// not going to happen
	}

	@Override
	public void onReceiveClient(byte[] data, int len, int offset, ServerConnection server, CosmosNettyClient client)
	{
		client.ready(true);
	}
}

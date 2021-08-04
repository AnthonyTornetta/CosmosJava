package com.cornchipss.cosmos.netty.packets;

import com.cornchipss.cosmos.client.CosmosNettyClient;
import com.cornchipss.cosmos.client.ServerConnection;
import com.cornchipss.cosmos.server.ClientConnection;
import com.cornchipss.cosmos.server.CosmosNettyServer;
import com.cornchipss.cosmos.utils.Logger;

public class DebugPacket extends Packet
{
	private String message;
	
	public DebugPacket()
	{
		
	}
	
	public DebugPacket(byte[] buffer, int bufferOffset)
	{
		super(buffer, bufferOffset);
	}

	public DebugPacket(byte[] buffer, int bufferOffset, String message)
	{
		super(buffer, bufferOffset);
		
		this.message = message;
	}
	
	@Override
	public void init()
	{
		super.init();
		writeString(message);
	}
	
	@Override
	public void onReceiveServer(byte[] data, int len, int offset, ClientConnection client, CosmosNettyServer server)
	{
		DebugPacket p = new DebugPacket(data, offset);
	
		String msg = p.readString();
		
		Logger.LOGGER.debug("DEBUG PACKET> " + msg);
	}

	@Override
	public void onReceiveClient(byte[] data, int len, int offset, ServerConnection server, CosmosNettyClient client)
	{
		DebugPacket p = new DebugPacket(data, offset);
		
		String msg = p.readString();
		
		Logger.LOGGER.debug("DEBUG PACKET> " + msg);
	}
}

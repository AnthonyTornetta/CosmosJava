package com.cornchipss.cosmos.netty.packets;

import com.cornchipss.cosmos.client.Client;
import com.cornchipss.cosmos.client.ServerConnection;
import com.cornchipss.cosmos.client.CosmosNettyClient;
import com.cornchipss.cosmos.server.CosmosNettyServer;
import com.cornchipss.cosmos.utils.Logger;
import com.cornchipss.cosmos.server.ClientConnection;

public class DisconnectedPacket extends Packet
{
	private String reason;
	
	public DisconnectedPacket()
	{
		
	}
	
	public DisconnectedPacket(byte[] buffer, int bufferOffset)
	{
		super(buffer, bufferOffset);
	}
	
	public DisconnectedPacket(byte[] buffer, int bufferOffset, String reason)
	{
		super(buffer, bufferOffset);
		
		this.reason = reason;
	}
	
	@Override
	public void init()
	{
		super.init();
		
		writeString(reason);
	}
	
	@Override
	public void onReceiveServer(byte[] data, int len, int offset, ClientConnection client, CosmosNettyServer server)
	{
		DisconnectedPacket p = new DisconnectedPacket(data, offset);
		Logger.LOGGER.info("CLIENT DISCONNECTED - " + p.readString());
		
		server.players().removePlayer(client);
		client.terminateConnection();
	}

	@Override
	public byte marker()
	{
		return 10;
	}

	@Override
	public void onReceiveClient(byte[] data, int len, int offset, ServerConnection server, CosmosNettyClient client)
	{
		DisconnectedPacket p = new DisconnectedPacket(data, offset);
		Logger.LOGGER.info("DISCONNECTED - " + p.readString());
		
		server.socket().close();
		
		Client.instance().running(false);
	}
}

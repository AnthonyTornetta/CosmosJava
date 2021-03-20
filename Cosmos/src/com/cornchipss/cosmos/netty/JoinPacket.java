package com.cornchipss.cosmos.netty;

import java.io.IOException;

import com.cornchipss.cosmos.server.CosmosNettyServer;
import com.cornchipss.cosmos.server.ServerClient;

public class JoinPacket extends Packet
{
	public JoinPacket()
	{
		
	}
	
	public JoinPacket(byte[] buffer, int bufferOffset, String name)
	{
		super(buffer, bufferOffset);
		
		write(name);
	}
	
	@Override
	public void onReceive(byte[] data, int len, int offset,
			ServerClient client, CosmosNettyServer server)
	{
		String name = new String(data, offset, len);
		
		if(server.players().createPlayer(client, name))
			data[0] = 1;
		else
			data[0] = 0;
		
		try
		{
			client.send(data, 1, server);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	
	@Override
	public byte marker()
	{
		return 1;
	}
}

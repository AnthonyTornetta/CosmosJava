package com.cornchipss.cosmos.netty;

import java.io.IOException;

import com.cornchipss.cosmos.server.CosmosNettyServer;
import com.cornchipss.cosmos.server.ServerClient;
import com.cornchipss.cosmos.utils.Utils;

public class PlayerPacket extends Packet
{
	public PlayerPacket()
	{
		
	}
	
	public PlayerPacket(byte[] buffer, int bufferOffset)
	{
		super(buffer, bufferOffset);
	}
	
	@Override
	public void onReceive(byte[] data, int len, int offset, 
			ServerClient client, CosmosNettyServer server)
	{
		String message = new String(data, offset, len);
		
		data[0] = 1;
		
		Utils.println(server.players().player(client).name() + "> " + message);
		
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
		return 2;
	}
}

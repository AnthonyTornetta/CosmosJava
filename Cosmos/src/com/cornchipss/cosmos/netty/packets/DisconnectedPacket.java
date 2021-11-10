package com.cornchipss.cosmos.netty.packets;

import com.cornchipss.cosmos.client.CosmosClient;
import com.cornchipss.cosmos.client.CosmosNettyClient;
import com.cornchipss.cosmos.client.ServerConnection;
import com.cornchipss.cosmos.netty.NettySide;
import com.cornchipss.cosmos.server.ClientConnection;
import com.cornchipss.cosmos.server.CosmosNettyServer;
import com.cornchipss.cosmos.utils.Logger;
import com.cornchipss.cosmos.world.entities.player.Player;

public class DisconnectedPacket extends Packet
{
	private String reason;
	private String name;
	
	public DisconnectedPacket()
	{
		
	}
	
	public DisconnectedPacket(byte[] buffer, int bufferOffset)
	{
		super(buffer, bufferOffset);
	}
	
	public DisconnectedPacket(byte[] buffer, int bufferOffset, String name, String reason)
	{
		super(buffer, bufferOffset);
		
		this.reason = reason;
		this.name = name;
	}
	
	@Override
	public void init()
	{
		super.init();
		
		if(NettySide.side() == NettySide.SERVER)
			writeString(name);
		
		writeString(reason);
	}
	
	@Override
	public void onReceiveServer(byte[] data, int len, int offset, ClientConnection client, CosmosNettyServer server)
	{
		DisconnectedPacket p = new DisconnectedPacket(data, offset);
		Logger.LOGGER.info(server.players().player(client).name() + " disconnected (" + p.readString() + ")");
		
		server.players().removePlayer(client);
		client.terminateConnection();
	}

	@Override
	public void onReceiveClient(byte[] data, int len, int offset, ServerConnection server, CosmosNettyClient client)
	{
		DisconnectedPacket p = new DisconnectedPacket(data, offset);
		String name = p.readString();
		String reason = p.readString();
		Logger.LOGGER.info(name + " disconnected - " + reason);
		
		Player removedPlayer = CosmosClient.instance().nettyClient().players().player(name);
		
		if(CosmosClient.instance().nettyClient().game().player().equals(removedPlayer))
		{
			if(server.socket() != null)
				server.socket().close();
			
			CosmosClient.instance().quit();
		}
		else
		{
			CosmosClient.instance().nettyClient().players().removePlayer(removedPlayer);
		}
	}
}

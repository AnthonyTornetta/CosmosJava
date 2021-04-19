package com.cornchipss.cosmos.netty.packets;

import java.io.IOException;

import com.cornchipss.cosmos.client.ClientServer;
import com.cornchipss.cosmos.client.CosmosNettyClient;
import com.cornchipss.cosmos.physx.Transform;
import com.cornchipss.cosmos.server.CosmosNettyServer;
import com.cornchipss.cosmos.server.ServerClient;
import com.cornchipss.cosmos.server.ServerPlayer;
import com.cornchipss.cosmos.world.entities.player.ClientPlayer;

public class JoinPacket extends Packet
{
	private String name;
	
	public JoinPacket()
	{
		
	}
	
	private JoinPacket(byte[] buffer, int bufferOffset)
	{
		super(buffer, bufferOffset);
	}
	
	public JoinPacket(byte[] buffer, int bufferOffset, String name)
	{
		super(buffer, bufferOffset);
		
		this.name = name;
	}
	
	@Override
	public void init()
	{
		super.init();
		
		write(name);
	}
	
	@Override
	public void onReceiveServer(byte[] data, int len, int offset,
			ServerClient client, CosmosNettyServer server)
	{
		JoinPacket packet = new JoinPacket(data, offset);
		
		String name = packet.readString();
		
		if(!server.players().playerExists(client) && !server.players().nameTaken(name))
		{
			ServerPlayer p = server.players().createPlayer(server.game().world(), client, name);
			p.addToWorld(new Transform());
			
			JoinPacket jp = new JoinPacket(data, 0, name);
			jp.init();
			
			try
			{
				client.send(jp.buffer(), jp.bufferLength(), server);
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
		else
		{
			DisconnectedPacket dc = new DisconnectedPacket(data, 0);
			dc.init();
			
			try
			{
				client.send(dc.buffer(), dc.bufferLength(), server);
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
	}
	
	@Override
	public byte marker()
	{
		return 11;
	}

	@Override
	public void onReceiveClient(byte[] data, int len, int offset, ClientServer server, CosmosNettyClient client)
	{
		JoinPacket packet = new JoinPacket(data, offset);
		
		ClientPlayer p = new ClientPlayer(client.game().world(), packet.readString());
		client.game().player(p);
		client.players().addPlayer(p);
	}
}

package com.cornchipss.cosmos.netty.packets;

import java.io.IOException;

import com.cornchipss.cosmos.client.CosmosNettyClient;
import com.cornchipss.cosmos.client.ServerConnection;
import com.cornchipss.cosmos.game.ServerGame;
import com.cornchipss.cosmos.server.ClientConnection;
import com.cornchipss.cosmos.server.CosmosNettyServer;
import com.cornchipss.cosmos.server.ServerPlayerList;
import com.cornchipss.cosmos.structures.Structure;
import com.cornchipss.cosmos.utils.Logger;
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
		
		writeString(name);
	}
	
	@Override
	public void onReceiveServer(byte[] data, int len, int offset,
			ClientConnection client, CosmosNettyServer server)
	{
		JoinPacket packet = new JoinPacket(data, offset);
		
		String name = packet.readString();
		
		ServerPlayerList list = server.players();
		
		if(!server.players().nameTaken(name))
		{
			ClientConnection c = list.getPendingConnection(name);
			
			if(c == null)
			{
				if(client.hasUDP() && client.hasTCP())
				{
					// they have already joined since both connections were setup but there was no in progress join phase
					return;
				}
				
				list.beginJoin(client, name);
			}
			else
			{
				if(client.hasUDP())
				{
					c.initUDP(client.address(), client.port());
				}
				
				if(client.hasTCP())
				{
					c.initTCP(client.tcpConnection());
				}
				
				if(c.hasUDP() && c.hasTCP())
				{
					list.finishJoin(c, ServerGame.instance().world(), name);
					
					successJoin(server, data, name, c);
				}
			}
		}
		else
		{
			DisconnectedPacket dc = new DisconnectedPacket(data, 0);
			dc.init();
			
			try
			{
				client.sendTCP(dc.buffer(), dc.bufferLength());
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
	}
	
	private void successJoin(CosmosNettyServer server, byte[] data, String name, ClientConnection c)
	{
		JoinPacket jp = new JoinPacket(data, 0, name);
		jp.init();
		
		Logger.LOGGER.info("PLAYER CONNECTED - " + name);
		
		try
		{
			c.sendTCP(jp.buffer(), jp.bufferLength());
		}
		catch (IOException ex)
		{
			ex.printStackTrace();
		}
		
		for(Structure s : server.game().world().structures())
		{
			FullStructurePacket sp = new FullStructurePacket(s);
			sp.init();
			
			try
			{
				c.sendTCP(sp.buffer(), sp.bufferLength());
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
		
		JoinFinishPacket jfp = new JoinFinishPacket(data, 0);
		jfp.init();
		
		try
		{
			c.sendTCP(jfp.buffer(), jfp.bufferLength());
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		
		try
		{
			DebugPacket dbgP = new DebugPacket(data, 0, "TCP RECEIVED");
			dbgP.init();
			c.sendTCP(dbgP.buffer(), dbgP.bufferLength());
			
			dbgP = new DebugPacket(data, 0, "UDP RECEIVED");
			dbgP.init();
			c.sendUDP(dbgP.buffer(), dbgP.bufferLength(), server);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	
	@Override
	public byte marker()
	{
		return 11;
	}

	@Override
	public void onReceiveClient(byte[] data, int len, int offset, ServerConnection server, CosmosNettyClient client)
	{
		JoinPacket packet = new JoinPacket(data, offset);
		
		ClientPlayer p = new ClientPlayer(client.game().world(), packet.readString());
		client.game().player(p);
		client.players().addPlayer(p);
	}
}

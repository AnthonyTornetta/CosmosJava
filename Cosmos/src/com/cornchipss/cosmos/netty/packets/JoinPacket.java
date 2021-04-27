package com.cornchipss.cosmos.netty.packets;

import java.io.IOException;

import com.cornchipss.cosmos.client.CosmosNettyClient;
import com.cornchipss.cosmos.client.ServerConnection;
import com.cornchipss.cosmos.game.ServerGame;
import com.cornchipss.cosmos.physx.Transform;
import com.cornchipss.cosmos.server.ClientConnection;
import com.cornchipss.cosmos.server.CosmosNettyServer;
import com.cornchipss.cosmos.server.ServerPlayer;
import com.cornchipss.cosmos.server.ServerPlayerList;
import com.cornchipss.cosmos.structures.Structure;
import com.cornchipss.cosmos.utils.Utils;
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
		Utils.println("NAME: " + name);
		
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
				}
			}
			
			JoinPacket jp = new JoinPacket(data, 0, name);
			jp.init();
			
			try
			{
				client.sendTCP(jp.buffer(), jp.bufferLength());
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
			
			for(Structure s : server.game().world().structures())
			{
				byte[] buf = new byte[s.width() * s.height() * s.length() * 2 + 64];
				FullStructurePacket sp = new FullStructurePacket(buf, 0, s);
				sp.init();
				try
				{
					client.sendTCP(sp.buffer(), sp.bufferLength());
				}
				catch (IOException e)
				{
					e.printStackTrace();
				}
			}
			
			try
			{
				byte[] buf = new byte[1000];
				DebugPacket dbgP = new DebugPacket(buf, 0, "TCP RECEIVED");
				dbgP.init();
				client.sendTCP(dbgP.buffer(), dbgP.bufferLength());
				
				dbgP = new DebugPacket(buf, 0, "UDP RECEIVED");
				dbgP.init();
				client.sendUDP(dbgP.buffer(), dbgP.bufferLength(), server);
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
				client.sendTCP(dc.buffer(), dc.bufferLength());
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
	public void onReceiveClient(byte[] data, int len, int offset, ServerConnection server, CosmosNettyClient client)
	{
		JoinPacket packet = new JoinPacket(data, offset);
		
		ClientPlayer p = new ClientPlayer(client.game().world(), packet.readString());
		client.game().player(p);
		client.players().addPlayer(p);
	}
}

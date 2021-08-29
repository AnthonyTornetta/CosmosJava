package com.cornchipss.cosmos.netty.packets;

import com.cornchipss.cosmos.client.CosmosNettyClient;
import com.cornchipss.cosmos.client.ServerConnection;
import com.cornchipss.cosmos.server.ClientConnection;
import com.cornchipss.cosmos.server.CosmosNettyServer;
import com.cornchipss.cosmos.server.Server;
import com.cornchipss.cosmos.structures.Ship;
import com.cornchipss.cosmos.structures.Structure;

public class ExitShipPacket extends Packet
{
	private Ship s;
	
	public ExitShipPacket()
	{
		
	}
	
	public ExitShipPacket(byte[] buffer, int bufferOffset)
	{
		super(buffer, bufferOffset);
	}
	
	/**
	 * 
	 * @param buffer
	 * @param bufferOffset
	 * @param s 
	 */
	public ExitShipPacket(byte[] buffer, int bufferOffset, Ship s)
	{
		super(buffer, bufferOffset);
		
		this.s = s;
	}
	
	@Override
	public void init()
	{
		super.init();
		
		if(s != null)
			writeInt(s.id());
	}
	
	@Override
	public void onReceiveServer(byte[] data, int len, int offset, ClientConnection client, CosmosNettyServer server)
	{
		Ship s = server.players().player(client).shipPiloting();
		
		if(s != null && s.pilot() != null)
		{
			s.setPilot(null);
			
			ExitShipPacket send = new ExitShipPacket(new byte[32], 0, s);
			send.init();
			Server.nettyServer().sendToAllTCP(send);
		}
	}

	@Override
	public void onReceiveClient(byte[] data, int len, int offset, ServerConnection server, CosmosNettyClient client)
	{
		ExitShipPacket esp = new ExitShipPacket(data, offset);
		
		int id = esp.readInt();
		
		Structure s = client.game().world().structureFromID(id);
		
		if(s instanceof Ship)
		{
			Ship ship = (Ship)s;
			
			ship.setPilot(null);
		}
	}
}

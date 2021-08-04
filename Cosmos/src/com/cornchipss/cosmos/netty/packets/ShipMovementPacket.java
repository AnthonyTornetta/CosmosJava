package com.cornchipss.cosmos.netty.packets;

import org.joml.Vector3f;
import org.joml.Vector3fc;

import com.cornchipss.cosmos.client.CosmosNettyClient;
import com.cornchipss.cosmos.client.ServerConnection;
import com.cornchipss.cosmos.server.ClientConnection;
import com.cornchipss.cosmos.server.CosmosNettyServer;
import com.cornchipss.cosmos.structures.Ship;
import com.cornchipss.cosmos.structures.Structure;

public class ShipMovementPacket extends Packet
{
	private Ship s;
	
	// for client only
	private Vector3fc vec;
	
	public ShipMovementPacket()
	{
		
	}
	
	public ShipMovementPacket(byte[] buffer, int bufferOffset)
	{
		super(buffer, bufferOffset);
	}
	
	/**
	 * 
	 * @param buffer
	 * @param bufferOffset
	 * @param s 
	 * @param vec If it's set client side it's the thrust directions, otherwise it's the ship's position
	 */
	public ShipMovementPacket(byte[] buffer, int bufferOffset, Ship s, Vector3fc vec)
	{
		super(buffer, bufferOffset);
		
		this.s = s;
		this.vec = vec;
	}
	
	@Override
	public void init()
	{
		super.init();
		
		writeInt(s.id());
		writeVector3fc(vec);
	}
	
	@Override
	public void onReceiveServer(byte[] data, int len, int offset, ClientConnection client, CosmosNettyServer server)
	{
//		ShipMovementPacket packet = new ShipMovementPacket(data, offset);
//		
//		Structure s = server.game().world().structureFromID(packet.readInt());
//		Vector3f directions = readVector3f(new Vector3f());
//		
//		s.body().transform().position(directions);
//		
//		packet.reset();
//		
//		Server.nettyServer().sendToAllUDP(packet);
	}

	@Override
	public void onReceiveClient(byte[] data, int len, int offset, ServerConnection server, CosmosNettyClient client)
	{
		ShipMovementPacket packet = new ShipMovementPacket(data, offset);
		
		int id = packet.readInt();
		Structure s = client.game().world().structureFromID(id);
		Vector3f vec = packet.readVector3f(new Vector3f());
		
		if(s != null) // may not have loaded yet
			s.body().navigateTowards(vec);
	}
}

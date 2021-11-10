package com.cornchipss.cosmos.netty.packets;

import com.cornchipss.cosmos.client.CosmosNettyClient;
import com.cornchipss.cosmos.client.ServerConnection;
import com.cornchipss.cosmos.physx.PhysicalObject;
import com.cornchipss.cosmos.server.ClientConnection;
import com.cornchipss.cosmos.server.CosmosNettyServer;
import com.cornchipss.cosmos.structures.Ship;

public class EntityPacket extends Packet
{
	private PhysicalObject obj;
	
	public EntityPacket()
	{
		
	}
	
	public EntityPacket(byte[] buffer, int bufferOffset)
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
	public EntityPacket(byte[] buffer, int bufferOffset, PhysicalObject obj)
	{
		super(buffer, bufferOffset);
		
		this.obj = obj;
	}
	
	@Override
	public void init()
	{
		super.init();
		
		writeVector3fc(obj.position());
		writeVector3fc(obj.body().velocity());
		writeQuaternionfc(obj.body().transform().orientation().quaternion());
		
		
	}
	
	@Override
	public void onReceiveServer(byte[] data, int len, int offset, ClientConnection client, CosmosNettyServer server)
	{
		
	}

	@Override
	public void onReceiveClient(byte[] data, int len, int offset, ServerConnection server, CosmosNettyClient client)
	{
		
	}
}

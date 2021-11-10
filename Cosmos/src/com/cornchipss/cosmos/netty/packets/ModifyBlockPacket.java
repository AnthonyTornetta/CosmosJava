package com.cornchipss.cosmos.netty.packets;

import com.cornchipss.cosmos.blocks.Block;
import com.cornchipss.cosmos.blocks.Blocks;
import com.cornchipss.cosmos.client.CosmosNettyClient;
import com.cornchipss.cosmos.client.ServerConnection;
import com.cornchipss.cosmos.server.ClientConnection;
import com.cornchipss.cosmos.server.CosmosNettyServer;
import com.cornchipss.cosmos.server.CosmosServer;
import com.cornchipss.cosmos.structures.Structure;

public class ModifyBlockPacket extends Packet
{
	private Structure s;
	private int x, y, z;
	private Block to;
	
	public ModifyBlockPacket()
	{
		
	}
	
	public ModifyBlockPacket(byte[] buffer, int bufferOffset)
	{
		super(buffer, bufferOffset);
	}
	
	public ModifyBlockPacket(byte[] buffer, int bufferOffset, Structure s, int x, int y, int z, Block to)
	{
		super(buffer, bufferOffset);
		
		this.s = s;
		this.x = x;
		this.y = y;
		this.z = z;
		this.to = to;
	}
	
	@Override
	public void init()
	{
		super.init();
		
		writeInt(s.id());
		writeInt(x);
		writeInt(y);
		writeInt(z);
		writeShort(to == null ? 0 : to.numericId());
	}
	
	@Override
	public void onReceiveServer(byte[] data, int len, int offset, ClientConnection client, CosmosNettyServer server)
	{
		ModifyBlockPacket packet = new ModifyBlockPacket(data, offset);
		
		Structure s = server.game().world().structureFromID(packet.readInt());
		int x, y, z;
		x = packet.readInt();
		y = packet.readInt();
		z = packet.readInt();
		
		Block block = Blocks.fromNumericId(packet.readShort());
		
		s.block(x, y, z, block);
		
		packet.reset();
		
		CosmosServer.nettyServer().sendToAllTCP(packet);
	}

	@Override
	public void onReceiveClient(byte[] data, int len, int offset, ServerConnection server, CosmosNettyClient client)
	{
		ModifyBlockPacket packet = new ModifyBlockPacket(data, offset);
		
		Structure s = client.game().world().structureFromID(packet.readInt());
		int x, y, z;
		x = packet.readInt();
		y = packet.readInt();
		z = packet.readInt();
		
		Block block = Blocks.fromNumericId(packet.readShort());
		
		s.block(x, y, z, block);
	}
}

package com.cornchipss.cosmos.netty.packets;

import com.cornchipss.cosmos.blocks.StructureBlock;
import com.cornchipss.cosmos.blocks.modifiers.IInteractable;
import com.cornchipss.cosmos.client.CosmosNettyClient;
import com.cornchipss.cosmos.client.ServerConnection;
import com.cornchipss.cosmos.server.ClientConnection;
import com.cornchipss.cosmos.server.CosmosNettyServer;
import com.cornchipss.cosmos.server.Server;
import com.cornchipss.cosmos.structures.Structure;
import com.cornchipss.cosmos.utils.Utils;

public class ClientInteractPacket extends Packet
{
	private StructureBlock block;
	
	public ClientInteractPacket()
	{
		
	}
	
	private ClientInteractPacket(byte[] buffer, int bufferOffset)
	{
		super(buffer, bufferOffset);
	}
	
	public ClientInteractPacket(byte[] buffer, int bufferOffset, StructureBlock block)
	{
		super(buffer, bufferOffset);
		
		this.block = block;
	}
	
	@Override
	public void init()
	{
		super.init();
		
		writeInt(block.structure().id());
		writeInt(block.structureX());
		writeInt(block.structureY());
		writeInt(block.structureZ());
	}
	
	@Override
	public void onReceiveServer(byte[] data, int len, int offset, 
			ClientConnection client, CosmosNettyServer server)
	{
		ClientInteractPacket packet = new ClientInteractPacket(data, offset);
				
		int id = packet.readInt();
		Structure s = Server.nettyServer().game().world().structureFromID(id);
		
		int x = packet.readInt();
		int y = packet.readInt();
		int z = packet.readInt();
		
		StructureBlock block = new StructureBlock(s, x, y, z);
		
		if(block.block() instanceof IInteractable)
		{
			Utils.println("INTERACTED!");
			((IInteractable)block.block()).
					onInteract(block, Server.nettyServer().players().player(client));
		}
		else
		{
			Utils.println(":(");
		}
	}
	
	@Override
	public void onReceiveClient(byte[] data, int len, int offset, ServerConnection server, CosmosNettyClient client)
	{
		// never will be
	}
}

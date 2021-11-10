package com.cornchipss.cosmos.netty.packets;

import com.cornchipss.cosmos.blocks.StructureBlock;
import com.cornchipss.cosmos.blocks.modifiers.IInteractable;
import com.cornchipss.cosmos.client.CosmosClient;
import com.cornchipss.cosmos.client.CosmosNettyClient;
import com.cornchipss.cosmos.client.ServerConnection;
import com.cornchipss.cosmos.server.ClientConnection;
import com.cornchipss.cosmos.server.CosmosNettyServer;
import com.cornchipss.cosmos.server.CosmosServer;
import com.cornchipss.cosmos.structures.Structure;
import com.cornchipss.cosmos.world.entities.player.Player;

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
		Structure s = server.game().world().structureFromID(id);
		
		int x = packet.readInt();
		int y = packet.readInt();
		int z = packet.readInt();
		
		StructureBlock block = new StructureBlock(s, x, y, z);
		
		if(block.block() instanceof IInteractable)
		{
			((IInteractable)block.block()).
					onInteract(block, CosmosServer.nettyServer().players().player(client));
			
			ClientInteractPacket cpt = new ClientInteractPacket(new byte[1024], 0, block);
			cpt.init();
			cpt.writeString(CosmosServer.nettyServer().players().player(client).name());
			
			CosmosServer.nettyServer().sendToAllTCP(cpt);
		}
	}
	
	@Override
	public void onReceiveClient(byte[] data, int len, int offset, ServerConnection server, CosmosNettyClient client)
	{
		ClientInteractPacket packet = new ClientInteractPacket(data, offset);
		
		int id = packet.readInt();
		Structure s = client.game().world().structureFromID(id);
		
		int x = packet.readInt();
		int y = packet.readInt();
		int z = packet.readInt();
		String name = packet.readString();

		StructureBlock block = new StructureBlock(s, x, y, z);
		
		Player player = CosmosClient.instance().nettyClient().players().player(name);
		
		((IInteractable)s.block(x, y, z))
			.onInteract(block, player);

	}
}

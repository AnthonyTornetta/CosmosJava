package com.cornchipss.cosmos.netty.packets;

import java.io.IOException;

import org.joml.Quaternionf;
import org.joml.Vector3f;

import com.cornchipss.cosmos.client.ServerConnection;
import com.cornchipss.cosmos.client.CosmosNettyClient;
import com.cornchipss.cosmos.physx.Transform;
import com.cornchipss.cosmos.server.CosmosNettyServer;
import com.cornchipss.cosmos.server.DummyPlayer;
import com.cornchipss.cosmos.server.ClientConnection;
import com.cornchipss.cosmos.structures.Structure;
import com.cornchipss.cosmos.world.Chunk;
import com.cornchipss.cosmos.world.entities.player.Player;

public class ChunkPacket extends Packet
{
	private Chunk c;
	
	public ChunkPacket()
	{
		
	}
	
	private ChunkPacket(byte[] buffer, int bufferOffset)
	{
		super(buffer, bufferOffset);
	}
	
	public ChunkPacket(byte[] buffer, int bufferOffset, Chunk c)
	{
		super(buffer, bufferOffset);
		
		this.c = c;
	}
	
	@Override
	public void init()
	{
		super.init();
		
		writeInt(c.structure().id());
		writeVector3ic(c.offset());
		for(int z = 0; z < c.length(); z++)
		{
			for(int y = 0; y < c.height(); y++)
			{
				for(int x = 0; x < c.width(); x++)
				{
					writeShort(c.block(x, y, z).numericId());					
				}	
			}	
		}
	}
	
	@Override
	public void onReceiveServer(byte[] data, int len, int offset, 
			ClientConnection client, CosmosNettyServer server)
	{
		//Structure s = server.game().world().structureFromID(readInt());
		
	}
	
	@Override
	public byte marker()
	{
		return 1;
	}

	@Override
	public void onReceiveClient(byte[] data, int len, int offset, ServerConnection server, CosmosNettyClient client)
	{
		
	}
}

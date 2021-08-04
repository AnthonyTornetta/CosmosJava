package com.cornchipss.cosmos.netty.packets;

import org.joml.Quaternionf;
import org.joml.Vector3f;

import com.cornchipss.cosmos.blocks.Block;
import com.cornchipss.cosmos.blocks.Blocks;
import com.cornchipss.cosmos.client.CosmosNettyClient;
import com.cornchipss.cosmos.client.ServerConnection;
import com.cornchipss.cosmos.physx.Orientation;
import com.cornchipss.cosmos.physx.Transform;
import com.cornchipss.cosmos.server.ClientConnection;
import com.cornchipss.cosmos.server.CosmosNettyServer;
import com.cornchipss.cosmos.structures.Planet;
import com.cornchipss.cosmos.structures.Ship;
import com.cornchipss.cosmos.structures.Structure;

public class FullStructurePacket extends Packet
{
	private Structure s;
	
	public FullStructurePacket()
	{
		
	}
	
	private FullStructurePacket(byte[] buffer, int bufferOffset)
	{
		super(buffer, bufferOffset);
	}
	
	public FullStructurePacket(Structure s)
	{
		super(new byte[s.width() * s.height() * s.length() * 2 + 64], 0);
		
		this.s = s;
	}
	
	@Override
	public void init()
	{
		int size = 1 + // the beginning marker bytes
				 4+1+12+16+12+ // the bytes listed below
				 s.width() * s.height() * s.length() * 2;
		
		// It's unlikely the given buffer is enough so we'll have to do this manually
		if(this.buffer().length < size)
			this.buffer(new byte[size]);
		
		super.init();

		// 4 bytes
		writeInt(s.id());
		
		// 1 byte
		if(s instanceof Planet)
			writeByte((byte)1);
		else
			writeByte((byte)2); // ship
		
		// 12 bytes
		writeVector3fc(s.body().transform().position());
		
		// 16 bytes
		writeQuaternionfc(s.body().transform().orientation().quaternion());
		
		// 12 bytes
		writeInt(s.width());
		writeInt(s.height());
		writeInt(s.length());
		
		// length * height * width * 2 bytes
		for(int z = 0; z < s.length(); z++)
		{
			for(int y = 0; y < s.height(); y++)
			{
				for(int x = 0; x < s.width(); x++)
				{
					Block b = s.block(x, y, z);
					if(b == null)
						writeShort((short)0);
					else
						writeShort(b.numericId());
				}	
			}	
		}
	}
	
	@Override
	public void onReceiveServer(byte[] data, int len, int offset, 
			ClientConnection client, CosmosNettyServer server)
	{
		// A server cannot receive this packet
	}

	@Override
	public void onReceiveClient(byte[] data, int len, int offset, ServerConnection server, CosmosNettyClient client)
	{
		FullStructurePacket p = new FullStructurePacket(data, offset);
		
		Structure s;
		
		int id = p.readInt();
		byte type = p.readByte();
		
		Vector3f pos = p.readVector3f(new Vector3f());
		Quaternionf rot = p.readQuaternionf(new Quaternionf());
		
		int w = p.readInt();
		int h = p.readInt();
		int l = p.readInt();
		
		if(type == 1)
			s = new Planet(client.game().world(), w, h, l, id);
		else
			s = new Ship(client.game().world(), id);
		
		s.init();
		
		for(int z = 0; z < s.length(); z++)
		{
			for(int y = 0; y < s.height(); y++)
			{
				for(int x = 0; x < s.width(); x++)
				{
					s.block(x, y, z, Blocks.fromNumericId(p.readShort()));
				}
			}	
		}
		
		Transform trans = new Transform();
		trans.position(pos);
		trans.orientation(new Orientation(rot));
		s.addToWorld(trans);
	}
}

package com.cornchipss.cosmos.netty.packets;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import org.joml.Quaternionf;
import org.joml.Vector3f;

import com.cornchipss.cosmos.client.CosmosNettyClient;
import com.cornchipss.cosmos.game.ClientGame;
import com.cornchipss.cosmos.game.ServerGame;
import com.cornchipss.cosmos.physx.Transform;
import com.cornchipss.cosmos.server.CosmosNettyServer;
import com.cornchipss.cosmos.server.kyros.ClientConnection;
import com.cornchipss.cosmos.structures.Structure;
import com.cornchipss.cosmos.utils.Utils;
import com.cornchipss.cosmos.world.World;

public class StructurePacket extends Packet
{
	private byte[] bytes;
	private Class<? extends Structure> clazz;
	
	private Vector3f pos;
	private Quaternionf rotation;
	
	private int id;
	
	public StructurePacket()
	{
		
	}
	
	public StructurePacket(Structure s)
	{		
		try
		{
			ByteArrayOutputStream bos = new ByteArrayOutputStream(s.width()*s.height()*s.length());
			s.write(new DataOutputStream(bos));
			this.bytes = bos.toByteArray();
			this.id = s.id();
			this.clazz = s.getClass();
			
			Utils.println(bytes.length);
			bytes = new byte[100];
		}
		catch (IOException e)
		{
			throw new RuntimeException(e);
		}
	}

	@Override
	public void receiveClient(CosmosNettyClient client, ClientGame game)
	{
		try
		{
			Structure s = clazz.getConstructor(World.class, Integer.class).newInstance(game.world(), id);
			s.read(new DataInputStream(new ByteArrayInputStream(bytes)));
			
			game.world().addStructure(s);
			
			s.addToWorld(new Transform(pos, rotation));
		}
		catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
			| NoSuchMethodException | IOException | SecurityException e)
		{
			throw new RuntimeException(e);
		}
	}

	@Override
	public void receiveServer(CosmosNettyServer server, ServerGame game, ClientConnection c)
	{
		// never going to happen
	}
}

package com.cornchipss.cosmos.netty.packets;

import java.io.IOException;

import org.joml.Quaternionf;
import org.joml.Vector3f;

import com.cornchipss.cosmos.client.ClientServer;
import com.cornchipss.cosmos.client.CosmosNettyClient;
import com.cornchipss.cosmos.physx.Transform;
import com.cornchipss.cosmos.server.CosmosNettyServer;
import com.cornchipss.cosmos.server.DummyPlayer;
import com.cornchipss.cosmos.server.ServerClient;
import com.cornchipss.cosmos.world.entities.player.Player;

public class PlayerPacket extends Packet
{
	private Player player;
	
	public PlayerPacket()
	{
		
	}
	
	private PlayerPacket(byte[] buffer, int bufferOffset)
	{
		super(buffer, bufferOffset);
	}
	
	public PlayerPacket(byte[] buffer, int bufferOffset, Player p)
	{
		super(buffer, bufferOffset);
		
		this.player = p;
	}
	
	@Override
	public void init()
	{
		super.init();
		
		write(player.name());
		write(player.body().transform().position());
		write(player.body().transform().rotation());
	}
	
	@Override
	public void onReceiveServer(byte[] data, int len, int offset, 
			ServerClient client, CosmosNettyServer server)
	{
		PlayerPacket packet = new PlayerPacket(data, offset);
		
		String name = packet.readString();
		
		Player p = server.players().player(name);
		
		if(p == null)
		{
			p = server.players().createPlayer(server.game().world(), client, name);
		}
		
		p.body().transform().position(packet.readVector3f(new Vector3f()));
		p.body().transform().rotation(packet.readQuaternionf(new Quaternionf()));
		
		PlayerPacket response = new PlayerPacket(data, 0, p);
		response.init();
		
		try
		{
			response.send(server.socket(), client.address(), client.port());
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	
	@Override
	public byte marker()
	{
		return 1;
	}

	@Override
	public void onReceiveClient(byte[] data, int len, int offset, ClientServer server, CosmosNettyClient client)
	{
		PlayerPacket packet = new PlayerPacket(data, offset);
		String name = packet.readString();
		
		Player p = client.players().player(name);
		if(p == null)
		{
			p = new DummyPlayer(client.game().world(), name);
			p.addToWorld(new Transform());
			client.players().addPlayer(p);
		}
		
		if(p.body() != null)
		{
			Vector3f newPos = packet.readVector3f(new Vector3f());

			Vector3f dPos = newPos.sub(p.body().transform().position(), new Vector3f());
			
			// if it's a small enough distance, move there smoothly
			if(dPos.x * dPos.x + dPos.y * dPos.y + dPos.z * dPos.z > 5)
			{
				p.body().transform().position(newPos);
			}
			else
			{
				float time = 1 / 20.0f; // approximate
				p.body().velocity().add(new Vector3f(dPos.x * time, dPos.y * time, dPos.z * time));
			}
			
			p.body().transform().rotation(packet.readQuaternionf(new Quaternionf()));
		}
	}
}

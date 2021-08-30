package com.cornchipss.cosmos.netty.packets;

import java.io.IOException;

import org.joml.Quaternionf;
import org.joml.Vector3f;

import com.cornchipss.cosmos.client.CosmosNettyClient;
import com.cornchipss.cosmos.client.ServerConnection;
import com.cornchipss.cosmos.physx.Transform;
import com.cornchipss.cosmos.server.ClientConnection;
import com.cornchipss.cosmos.server.CosmosNettyServer;
import com.cornchipss.cosmos.server.DummyPlayer;
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
		
		try
		{
			writeString(player.name());
			writeVector3fc(player.body().transform().position());
			writeQuaternionfc(player.body().transform().orientation().quaternion());
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
	}
	
	@Override
	public void onReceiveServer(byte[] data, int len, int offset, 
			ClientConnection client, CosmosNettyServer server)
	{
		PlayerPacket packet = new PlayerPacket(data, offset);
		
		String name = packet.readString();
		
		Player p = server.players().player(name);
		
		if(p == null)
		{
			p = server.players().createPlayer(server.game().world(), client, name);
		}
		
		p.body().transform().position(packet.readVector3f(new Vector3f()));
		p.body().transform().orientation().quaternion(packet.readQuaternionf(new Quaternionf()));
		
		PlayerPacket response = new PlayerPacket(data, 0, p);
		response.init();
		
		try
		{
			client.sendTCP(response.buffer(), response.bufferLength());
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	@Override
	public void onReceiveClient(byte[] data, int len, int offset, ServerConnection server, CosmosNettyClient client)
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

//			Vector3f dPos = newPos.sub(p.body().transform().position(), new Vector3f());
			
			// if it's a small enough distance, move there smoothly
//			if(dPos.x * dPos.x + dPos.y * dPos.y + dPos.z * dPos.z > 5)
//			{
				p.body().transform().position(newPos);
//				
//				Utils.println(name + " - " + Utils.toString(newPos));
				
//			}
//			else
//			{
//				float time = 1 / 20.0f; // approximate
//				p.body().velocity().add(new Vector3f(dPos.x * time, dPos.y * time, dPos.z * time));
//			}
			
			// this hurts my eyes.  I should prob sync this
//			p.body().transform().orientation().quaternion(packet.readQuaternionf(new Quaternionf()));
		}
	}
}

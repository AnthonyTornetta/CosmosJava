package com.cornchipss.cosmos.netty.packets;

import org.joml.Quaternionfc;
import org.joml.Vector3fc;

import com.bulletphysics.dynamics.RigidBody;
import com.cornchipss.cosmos.client.CosmosNettyClient;
import com.cornchipss.cosmos.client.game.ClientGame;
import com.cornchipss.cosmos.client.world.entities.DummyPlayer;
import com.cornchipss.cosmos.physx.RigidBodyProxy;
import com.cornchipss.cosmos.server.CosmosNettyServer;
import com.cornchipss.cosmos.server.game.ServerGame;
import com.cornchipss.cosmos.server.kyros.ClientConnection;
import com.cornchipss.cosmos.world.entities.player.Player;

public class PlayerPacket extends Packet
{
	private String name;
	private Vector3fc position;
	private Quaternionfc rotation;

	public PlayerPacket()
	{

	}

	public PlayerPacket(Player p)
	{
		this.name = p.name();
		this.position = p.position();
		this.rotation = p.body().orientation().quaternion();
	}

	@Override
	public void receiveClient(CosmosNettyClient client, ClientGame game)
	{
		Player p = client.players().player(name);
		if (p == null)
		{
			p = new DummyPlayer(game.world(), name);
			
			RigidBody rb = p.createRigidBody(position, rotation);
			p.addToWorld(new RigidBodyProxy(rb));
			client.players().addPlayer(p);
		}
		else if (p.body() != null)
		{
			p.body().position(position);
			p.body().orientation().quaternion(rotation);
		}
	}

	@Override
	public void receiveServer(CosmosNettyServer server, ServerGame game,
		ClientConnection c)
	{
		c.player().body().position(position);
		c.player().body().orientation().quaternion(rotation);

		name = c.player().name();

		server.sendToAllExceptUDP(this, c.player());
	}
}

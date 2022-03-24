package com.cornchipss.cosmos.netty.packets;

import org.joml.Quaternionf;
import org.joml.Vector3f;

import com.cornchipss.cosmos.client.CosmosNettyClient;
import com.cornchipss.cosmos.client.game.ClientGame;
import com.cornchipss.cosmos.client.world.entities.DummyPlayer;
import com.cornchipss.cosmos.physx.RigidBodyProxy;
import com.cornchipss.cosmos.server.CosmosNettyServer;
import com.cornchipss.cosmos.server.game.ServerGame;
import com.cornchipss.cosmos.server.kyros.ClientConnection;
import com.cornchipss.cosmos.structures.Structure;

public class LoginPacket extends Packet
{
	private String name;

	public LoginPacket()
	{

	}

	public LoginPacket(String name)
	{
		this.name = name;
	}

	@Override
	public void receiveClient(CosmosNettyClient client, ClientGame game)
	{
		DummyPlayer p;
		client.players().addPlayer(p = new DummyPlayer(game.world(), name));
		p.addToWorld(new RigidBodyProxy(p.createRigidBody(new Vector3f(), new Quaternionf())));
	}

	@Override
	public void receiveServer(CosmosNettyServer server, ServerGame game, ClientConnection c)
	{
		if (server.players().nameTaken(name))
		{
			c.sendTCP(new JoinPacket(false, "Name Taken"));
		}
		else if (server.players().connectionRegistered(c))
		{
			c.sendTCP(new JoinPacket(false, "Already Connected!"));
		}
		else
		{
			c.player(server.players().createPlayer(game.world(), c, name));

			c.sendTCP(new JoinPacket(true, name));

			server.sendToAllExceptTCP(this, c.player());

			for (Structure s : game.world().structures())
			{
				c.sendTCP(new StructurePacket(s));
			}
		}
	}
}

package com.cornchipss.cosmos.netty.packets;

import org.joml.Quaternionfc;
import org.joml.Vector3fc;

import com.cornchipss.cosmos.client.CosmosNettyClient;
import com.cornchipss.cosmos.game.ClientGame;
import com.cornchipss.cosmos.game.ServerGame;
import com.cornchipss.cosmos.physx.Transform;
import com.cornchipss.cosmos.server.CosmosNettyServer;
import com.cornchipss.cosmos.server.DummyPlayer;
import com.cornchipss.cosmos.server.kyros.ClientConnection;
import com.cornchipss.cosmos.world.entities.player.Player;

public class PlayerPacket extends Packet
{
	private String name;
	private Vector3fc position;
	private Quaternionfc rotation;

	public PlayerPacket(Player p)
	{
		this.name = p.name();
		this.position = p.position();
		this.rotation = p.body().transform().orientation().quaternion();
	}

	@Override
	public void receiveClient(CosmosNettyClient client, ClientGame game)
	{
		Player p = client.players().player(name);
		if (p == null)
		{
			p = new DummyPlayer(game.world(), name);
			p.addToWorld(new Transform(position, rotation));
			client.players().addPlayer(p);
		}
		else if (p.body() != null)
		{
			p.body().transform().position(position);
			p.body().transform().orientation().quaternion(rotation);
		}
	}

	@Override
	public void receiveServer(CosmosNettyServer server, ServerGame game, ClientConnection c)
	{
		c.player().body().transform().position(position);
		c.player().body().transform().orientation().quaternion(rotation);

		name = c.player().name();

		server.sendToAllExceptUDP(this, c.player());
	}
}

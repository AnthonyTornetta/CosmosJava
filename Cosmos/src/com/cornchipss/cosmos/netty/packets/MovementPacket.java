package com.cornchipss.cosmos.netty.packets;

import org.joml.Vector3fc;

import com.cornchipss.cosmos.client.CosmosNettyClient;
import com.cornchipss.cosmos.game.ClientGame;
import com.cornchipss.cosmos.game.ServerGame;
import com.cornchipss.cosmos.physx.Movement;
import com.cornchipss.cosmos.server.CosmosNettyServer;
import com.cornchipss.cosmos.server.ServerPlayer;
import com.cornchipss.cosmos.server.kyros.ClientConnection;
import com.cornchipss.cosmos.world.entities.player.Player;

public class MovementPacket extends Packet
{
	private int code;
	private Vector3fc dRot;

	private String name;

	public MovementPacket()
	{

	}

	public MovementPacket(Movement m)
	{
		code = m.code();
		dRot = m.deltaRotation();
	}

	@Override
	public void receiveClient(CosmosNettyClient client, ClientGame game)
	{
		Player p = client.players().player(name);
		Movement m = Movement.movementFromCode(code);
		m.addDeltaRotation(dRot);

		p.movement(m);
	}

	@Override
	public void receiveServer(CosmosNettyServer server, ServerGame game, ClientConnection c)
	{
		ServerPlayer p = c.player();
		name = p.name();
		Movement m = Movement.movementFromCode(code);
		m.addDeltaRotation(dRot);

		p.movement(m);

		server.sendToAllExceptTCP(this, p);
	}
}

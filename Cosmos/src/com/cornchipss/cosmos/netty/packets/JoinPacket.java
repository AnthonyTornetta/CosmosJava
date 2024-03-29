package com.cornchipss.cosmos.netty.packets;

import java.io.IOException;

import com.cornchipss.cosmos.client.CosmosNettyClient;
import com.cornchipss.cosmos.client.game.ClientGame;
import com.cornchipss.cosmos.client.world.entities.ClientPlayer;
import com.cornchipss.cosmos.physx.Transform;
import com.cornchipss.cosmos.server.CosmosNettyServer;
import com.cornchipss.cosmos.server.game.ServerGame;
import com.cornchipss.cosmos.server.kyros.ClientConnection;
import com.cornchipss.cosmos.utils.Logger;

public class JoinPacket extends Packet
{
	private boolean success;
	private String msg;

	public JoinPacket()
	{
	}

	public JoinPacket(boolean success, String msg)
	{
		this.success = success;
		this.msg = msg;
	}

	@Override
	public void receiveClient(CosmosNettyClient client, ClientGame game)
	{
		if (success)
		{
			Logger.LOGGER.info(this);
			game.player(new ClientPlayer(game.world(), name()));
			game.player().addToWorld(new Transform());
			client.players().addPlayer(game.player());
		}
		else
		{
			Logger.LOGGER.warning(this);
			try
			{
				client.disconnect();
			}
			catch (IOException e)
			{
				throw new RuntimeException(e);
			}
		}
	}

	@Override
	public void receiveServer(CosmosNettyServer server, ServerGame game,
		ClientConnection c)
	{
		// not happening
	}

	@Override
	public String toString()
	{
		return success ? "Name: " + msg
			: "Error: " + (msg != null ? " - " + msg : "");
	}

	public boolean success()
	{
		return success;
	}

	public String name()
	{
		return msg;
	}

	public String message()
	{
		return msg;
	}
}

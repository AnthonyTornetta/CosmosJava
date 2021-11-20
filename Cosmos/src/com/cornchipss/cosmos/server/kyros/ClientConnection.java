package com.cornchipss.cosmos.server.kyros;

import com.cornchipss.cosmos.server.ServerPlayer;
import com.esotericsoftware.kryonet.Connection;

public class ClientConnection extends Connection
{
	ServerPlayer player;

	public ClientConnection()
	{
	}

	public void player(ServerPlayer p)
	{
		player = p;
	}

	public ServerPlayer player()
	{
		return player;
	}
}

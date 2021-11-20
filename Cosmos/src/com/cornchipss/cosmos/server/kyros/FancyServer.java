package com.cornchipss.cosmos.server.kyros;

import com.cornchipss.cosmos.netty.NetworkRegistry;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Server;

public class FancyServer extends Server
{
	public FancyServer()
	{
		super(NetworkRegistry.BUFFER_SIZE, NetworkRegistry.BUFFER_SIZE);
	}

	protected Connection newConnection()
	{
		return new ClientConnection();
	}
}

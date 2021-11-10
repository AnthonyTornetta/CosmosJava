package com.cornchipss.cosmos.server.kyros;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Server;

public class FancyServer extends Server
{
	protected Connection newConnection()
	{
		return new ClientConnection();
	}
}

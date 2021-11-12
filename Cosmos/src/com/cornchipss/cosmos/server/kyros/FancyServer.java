package com.cornchipss.cosmos.server.kyros;

import com.cornchipss.cosmos.server.kyros.register.Network;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Server;

public class FancyServer extends Server
{
	public FancyServer()
	{
		super(Network.BUFFER_SIZE, Network.BUFFER_SIZE);
	}
	
	protected Connection newConnection()
	{
		return new ClientConnection();
	}
}

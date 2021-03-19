package com.cornchipss.cosmos.server.command;

import com.cornchipss.cosmos.server.CosmosNettyServer;

public interface CommandHandler
{
	public boolean processInput(CosmosNettyServer server, String command);
}

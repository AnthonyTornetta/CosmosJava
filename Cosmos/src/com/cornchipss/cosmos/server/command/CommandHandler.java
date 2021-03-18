package com.cornchipss.cosmos.server.command;

import com.cornchipss.cosmos.server.CosmosServer;

public interface CommandHandler
{
	public boolean processInput(CosmosServer server, String command);
}

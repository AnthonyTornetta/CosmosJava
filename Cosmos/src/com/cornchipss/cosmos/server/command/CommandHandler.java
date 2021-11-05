package com.cornchipss.cosmos.server.command;

import com.cornchipss.cosmos.server.CosmosNettyServer;

public interface CommandHandler extends Iterable<Command>
{
	public boolean processInput(CosmosNettyServer server, String command);

	public Command command(String name);
}

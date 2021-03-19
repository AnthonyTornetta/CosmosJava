package com.cornchipss.cosmos.server.command;

import java.util.List;

import com.cornchipss.cosmos.server.CosmosNettyServer;

public interface Command
{
	public String name();
	
	public boolean call(CosmosNettyServer server, List<String> arguments, String rawCommand);
}

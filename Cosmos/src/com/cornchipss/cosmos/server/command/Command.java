package com.cornchipss.cosmos.server.command;

import java.util.List;

import com.cornchipss.cosmos.server.CosmosServer;

public interface Command
{
	public String name();
	
	public boolean call(CosmosServer server, List<String> arguments, String rawCommand);
}

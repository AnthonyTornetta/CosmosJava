package com.cornchipss.cosmos.server.command.commands;

import java.util.List;

import com.cornchipss.cosmos.server.CosmosNettyServer;
import com.cornchipss.cosmos.server.command.Command;

public class StopCommand implements Command
{
	@Override
	public String name()
	{
		return "stop";
	}

	@Override
	public boolean call(CosmosNettyServer server, List<String> arguments, String rawCommand)
	{
		server.running(false);
		return false;
	}

	@Override
	public String argumentsHelp()
	{
		return "";
	}

	@Override
	public String description()
	{
		return "Stops the server gracefully";
	}
}

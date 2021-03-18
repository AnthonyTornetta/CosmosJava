package com.cornchipss.cosmos.server.command.commands;

import java.util.List;

import com.cornchipss.cosmos.server.CosmosServer;
import com.cornchipss.cosmos.server.command.Command;

public class StopCommand implements Command
{
	@Override
	public String name()
	{
		return "stop";
	}

	@Override
	public boolean call(CosmosServer server, List<String> arguments, String rawCommand)
	{
		return false;
	}
}

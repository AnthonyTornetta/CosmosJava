package com.cornchipss.cosmos.server.command.commands;

import java.util.List;

import com.cornchipss.cosmos.server.CosmosServer;
import com.cornchipss.cosmos.server.command.Command;

public class PingCommand implements Command
{
	@Override
	public String name()
	{
		return "ping";
	}

	@Override
	public boolean call(CosmosServer server, List<String> arguments, String rawCommand)
	{
		System.out.println("Pong");
		return true;
	}
}

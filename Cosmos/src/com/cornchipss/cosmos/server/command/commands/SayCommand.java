package com.cornchipss.cosmos.server.command.commands;

import java.util.List;

import com.cornchipss.cosmos.server.CosmosNettyServer;
import com.cornchipss.cosmos.server.command.Command;

public class SayCommand implements Command
{
	@Override
	public String name()
	{
		return "say";
	}

	@Override
	public boolean call(CosmosNettyServer server, List<String> arguments,
		String rawCommand)
	{
		if (arguments.size() == 0)
		{
			System.out.println("I have nothing to say :(");
			return true;
		}
		rawCommand = rawCommand.trim();
		System.out.println(
			"SAY: " + rawCommand.substring(rawCommand.indexOf(' ') + 1));

		return true;
	}

	@Override
	public String argumentsHelp()
	{
		return "{What to say}";
	}

	@Override
	public String description()
	{
		return "Says what you tell it to.";
	}
}

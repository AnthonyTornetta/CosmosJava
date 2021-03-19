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
	public boolean call(CosmosNettyServer server, List<String> arguments, String rawCommand)
	{
		rawCommand = rawCommand.trim();
		System.out.println("SAY: " + rawCommand.substring(rawCommand.indexOf(' ') + 1));
		
		return true;
	}
}

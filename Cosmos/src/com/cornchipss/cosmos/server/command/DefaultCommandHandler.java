package com.cornchipss.cosmos.server.command;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.cornchipss.cosmos.server.CosmosNettyServer;

public class DefaultCommandHandler implements CommandHandler
{
	private Map<String, Command> commands = new HashMap<>();
	
	public void addCommand(Command cmd)
	{
		commands.put(cmd.name(), cmd);
	}
	
	@Override
	public boolean processInput(CosmosNettyServer server, String command)
	{
		String[] split = command.trim().split(" ");
		String cmdName = split[0].toLowerCase();
		
		if(cmdName.length() == 0)
			return true;
		
		List<String> arguments = new LinkedList<>();
		for(int i = 1; i < split.length; i++)
		{
			split[i] = split[i].trim();
			if(split[i].length() != 0)
				arguments.add(split[i]);
		}
		
		Command cmdObj = command(cmdName);
		
		if(cmdObj == null)
		{
			System.out.println("Unknown command - type help for a list of commands.");
			return true;
		}
		else
			return cmdObj.call(server, arguments, command);
	}

	@Override
	public Iterator<Command> iterator()
	{
		return commands.values().iterator();
	}

	@Override
	public Command command(String name)
	{
		return commands.get(name);
	}
}

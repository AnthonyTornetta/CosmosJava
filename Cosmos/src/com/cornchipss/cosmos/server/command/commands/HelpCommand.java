package com.cornchipss.cosmos.server.command.commands;

import java.util.List;

import com.cornchipss.cosmos.server.CosmosNettyServer;
import com.cornchipss.cosmos.server.command.Command;
import com.cornchipss.cosmos.server.command.CommandHandler;

public class HelpCommand implements Command
{
	private CommandHandler handler;

	public HelpCommand(CommandHandler handler)
	{
		this.handler = handler;
	}

	@Override
	public String name()
	{
		return "help";
	}

	@Override
	public String argumentsHelp()
	{
		return "[command-name] [command-name2] ... [command-nameN]";
	}

	@Override
	public String description()
	{
		return "Displays all available commands given no arguments or specific help for one or more given commands";
	}

	@Override
	public boolean call(CosmosNettyServer server, List<String> arguments, String rawCommand)
	{
		if (arguments.size() == 0)
		{
			System.out.println("== HELP ==");
			for (Command cmd : handler)
			{
				System.out.println(cmd.name());
			}
			System.out.println("Use help [command] to get a detailed description.");
		}
		else
		{
			for (String s : arguments)
			{
				Command cmd = handler.command(s);
				System.out.println("Usage: " + cmd.name() + " " + cmd.argumentsHelp());
				System.out.println("\t" + cmd.description());
			}
		}
		return true;
	}

}

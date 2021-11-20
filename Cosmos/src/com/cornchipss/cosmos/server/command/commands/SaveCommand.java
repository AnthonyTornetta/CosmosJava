package com.cornchipss.cosmos.server.command.commands;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import com.cornchipss.cosmos.server.CosmosNettyServer;
import com.cornchipss.cosmos.server.command.Command;
import com.cornchipss.cosmos.structures.Ship;
import com.cornchipss.cosmos.structures.Structure;

public class SaveCommand implements Command
{
	@Override
	public String name()
	{
		return "save";
	}

	private void save(Structure s, String name)
	{
		new File("assets/structures/saves/").mkdirs();

		try (DataOutputStream str = new DataOutputStream(
			new FileOutputStream(new File("assets/structures/saves/" + name + ".struct"))))
		{
			s.write(str);
			System.out.println("Saved structure " + s.id() + " as " + name);
		}
		catch (IOException ex)
		{
			ex.printStackTrace();
		}
	}

	@Override
	public boolean call(CosmosNettyServer server, List<String> arguments, String rawCommand)
	{
		boolean isId = arguments.size() != 0;
		boolean saved = false;

		if (isId)
		{
			try
			{
				int id = Integer.parseInt(arguments.get(0));
				Structure str = server.game().world().structureFromID(id);
				if (str != null)
				{
					if (arguments.size() == 2)
						save(str, arguments.get(1));
					else
						save(str, str.id() + "");

					saved = true;
				}
				else
				{
					System.out.println("!! No structure with ID " + id + " !!");
				}
			}
			catch (NumberFormatException ex)
			{
				System.out.println(arguments.get(0) + " is not a valid integer ID!");
			}
		}
		else
		{
			for (Structure s : server.game().world().structures())
			{
				if (s instanceof Ship)
				{
					save(s, s.id() + "");

					return true;
				}
			}
		}

		if (!saved)
			System.out.println("No savable candidates found!");
		return true;
	}

	@Override
	public String argumentsHelp()
	{
		return "[id] [name]";
	}

	@Override
	public String description()
	{
		return "Saves a structure in \"assets/structures/ships/{0}.struct\". "
			+ "If no arguments it saves the first ship found - otherwise it saves the structure with that ID."
			+ "The {0} is either the ID of the structure OR the name if provided.";
	}
}

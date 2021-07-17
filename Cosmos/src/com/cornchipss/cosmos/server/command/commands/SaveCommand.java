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

	@Override
	public boolean call(CosmosNettyServer server, List<String> arguments, String rawCommand)
	{
		for(Structure s : server.game().world().structures())
		{
			if(s instanceof Ship)
			{
				try
				{
					s.write(
						new DataOutputStream(
								new FileOutputStream(
										new File("assets/structures/ships/test.struct"))));
				}
				catch(IOException ex)
				{
					ex.printStackTrace();
				}
				
				return true;
			}
		}
		
		return true;
	}
}

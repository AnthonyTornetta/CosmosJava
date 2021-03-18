package com.cornchipss.cosmos.server;

import java.util.Scanner;

public class ServerConsole
{
	private Scanner scan;
	
	public ServerConsole()
	{
		scan = new Scanner(System.in);
	}
	
	public boolean readCommand(CosmosServer server)
	{
		System.out.print("> ");
		return server.commandHandler().processInput(server, scan.nextLine());
	}
	
	@Override
	public void finalize()
	{
		scan.close();
	}
}

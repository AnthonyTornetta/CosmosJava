package com.cornchipss.cosmos.server;

import com.cornchipss.cosmos.game.ServerGame;
import com.cornchipss.cosmos.server.command.CommandHandler;

public class CosmosServer
{
	private volatile boolean running;
	
	private final ServerGame game;
	
	private CommandHandler cmdHandler;
	
	public CosmosServer(ServerGame game, CommandHandler cmdHandler)
	{
		running = true;
		this.game = game;
		
		this.cmdHandler = cmdHandler;
	}
	
	public ServerGame game()
	{
		return game;
	}
	
	public boolean running() { return running; }
	public void running(boolean r) { running = r; }
	
	public CommandHandler commandHandler() { return cmdHandler; }
	public void commandHandler(CommandHandler h) { this.cmdHandler = h; }
}

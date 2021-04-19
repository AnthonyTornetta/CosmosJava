package com.cornchipss.cosmos.server;

import com.cornchipss.cosmos.game.ServerGame;
import com.cornchipss.cosmos.netty.PacketTypes;
import com.cornchipss.cosmos.registry.Initializer;
import com.cornchipss.cosmos.server.command.DefaultCommandHandler;
import com.cornchipss.cosmos.server.command.commands.PingCommand;
import com.cornchipss.cosmos.server.command.commands.SayCommand;
import com.cornchipss.cosmos.server.command.commands.StopCommand;
import com.cornchipss.cosmos.utils.GameLoop;
import com.cornchipss.cosmos.utils.Logger;

public class Server
{
	public void run()
	{
		Logger.LOGGER.setLevel(Logger.LogLevel.DEBUG);
		
		Initializer loader = new ServerInitializer();
		loader.init();
		
		ServerGame game = new ServerGame();
		
		DefaultCommandHandler defaultCmd = new DefaultCommandHandler();
		
		defaultCmd.addCommand(new StopCommand());
		defaultCmd.addCommand(new PingCommand());
		defaultCmd.addCommand(new SayCommand());
		
		PacketTypes.registerAll();
		final CosmosNettyServer server = new CosmosNettyServer(game, defaultCmd);
		
		Thread serverThread = new Thread(server);
		serverThread.start();
		
		ServerConsole cmd = new ServerConsole();
		
		GameLoop loop = new GameLoop((float delta) ->
		{
			server.game().update(delta);
			
			return server.running();
		}, 10);
		
		Thread gameThread = new Thread(loop);
		
		gameThread.start();
		
		while(cmd.readCommand(server));
		
		server.running(false);
		
		try
		{
			serverThread.join();
			gameThread.join();
		}
		catch (InterruptedException e)
		{
			e.printStackTrace();
		}
		
		Logger.LOGGER.info("Successfully closed.");
	}
}

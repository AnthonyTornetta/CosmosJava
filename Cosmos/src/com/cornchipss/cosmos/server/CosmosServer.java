package com.cornchipss.cosmos.server;

import com.cornchipss.cosmos.game.ServerGame;
import com.cornchipss.cosmos.netty.NettySide;
import com.cornchipss.cosmos.registry.Initializer;
import com.cornchipss.cosmos.server.command.DefaultCommandHandler;
import com.cornchipss.cosmos.server.command.commands.HelpCommand;
import com.cornchipss.cosmos.server.command.commands.PingCommand;
import com.cornchipss.cosmos.server.command.commands.SaveCommand;
import com.cornchipss.cosmos.server.command.commands.SayCommand;
import com.cornchipss.cosmos.server.command.commands.StopCommand;
import com.cornchipss.cosmos.utils.GameLoop;
import com.cornchipss.cosmos.utils.Logger;

public class CosmosServer implements Runnable
{
	private static CosmosNettyServer server;

	public static CosmosNettyServer nettyServer()
	{
		return server;
	}

	public CosmosServer()
	{
		NettySide.initNettySide(NettySide.SERVER);
	}

	@Override
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
		defaultCmd.addCommand(new SaveCommand());
		defaultCmd.addCommand(new HelpCommand(defaultCmd));

		server = new CosmosNettyServer(game, defaultCmd);

//		PacketTypes.registerAll();

		Thread serverThread = new Thread(server);
		serverThread.start();

		ServerConsole cmd = new ServerConsole();

		GameLoop loop = new GameLoop((float delta) ->
		{
			server.game().update(delta);

//			for (ServerPlayer p : server.players().players())
//			{
//				PlayerPacket packet = new PlayerPacket(playerBuffer, 0, p);
//				packet.init();
//
//				server.sendToAllExceptUDP(packet, p);
//			}

			return server.running();
		}, 1000 / 50); // 20 tps

		Thread gameThread = new Thread(loop);

		gameThread.start();

		while (server.running())
		{
			cmd.readCommand(server);
		}

		Logger.LOGGER.info("Terminating server...");

		try
		{
			gameThread.join();
			// I cannot join() the udp thread because it forever waits for a UDP connection
			// that will never happen
		}
		catch (InterruptedException e)
		{
			e.printStackTrace();
		}

		Logger.LOGGER.info("Server terminated.");
		System.exit(0);
	}
}

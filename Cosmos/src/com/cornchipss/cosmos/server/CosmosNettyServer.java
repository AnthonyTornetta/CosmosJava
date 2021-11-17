package com.cornchipss.cosmos.server;

import java.io.IOException;

import com.cornchipss.cosmos.game.ServerGame;
import com.cornchipss.cosmos.netty.NetworkRegistry;
import com.cornchipss.cosmos.netty.packets.Packet;
import com.cornchipss.cosmos.netty.packets.PlayerDisconnectPacket;
import com.cornchipss.cosmos.server.command.CommandHandler;
import com.cornchipss.cosmos.server.kyros.ClientConnection;
import com.cornchipss.cosmos.server.kyros.FancyServer;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;

public class CosmosNettyServer implements Runnable
{
	private volatile boolean running;

	private final ServerGame game;

	private CommandHandler cmdHandler;

	private ServerPlayerList players = new ServerPlayerList();

	private Server server;

	public CosmosNettyServer(ServerGame game, CommandHandler cmdHandler)
	{
		server = new FancyServer();

		running = true;
		this.game = game;

		this.cmdHandler = cmdHandler;
	}

	private void check(Object o)
	{
		if(!NetworkRegistry.check(o.getClass()))
			throw new RuntimeException("Attempted to send non-registed type - " + o.getClass());
	}
	
	public void sendToAllUDP(Object o)
	{
		check(o);
		server.sendToAllUDP(o);
	}
	
	public void sendToAllTCP(Object o)
	{
		check(o);
		server.sendToAllTCP(o);
	}

	public void sendToAllExceptUDP(Object o, ServerPlayer exception)
	{
		check(o);
		server.sendToAllExceptUDP(exception.connection().getID(), o);
	}

	public void sendToAllExceptTCP(Object o, ServerPlayer exception)
	{
		check(o);
		server.sendToAllExceptTCP(exception.connection().getID(), o);
	}

	@Override
	public void run()
	{
		server.start();

		NetworkRegistry.register(server);

		final CosmosNettyServer instance = this;

		server.addListener(new Listener()
		{
			public void received(Connection connection, Object object)
			{
				ClientConnection c = (ClientConnection) connection;

				if (object instanceof Packet)
				{
					((Packet) object).receiveServer(instance, game, c);
				}
			}

			public void disconnected(Connection c)
			{
				ClientConnection connection = (ClientConnection) c;

				sendToAllExceptTCP(new PlayerDisconnectPacket(connection.player()), connection.player());
				players.removePlayer(connection.player());
			}
		});

		try
		{
			server.bind(NetworkRegistry.TCP_PORT, NetworkRegistry.UDP_PORT);
		}
		catch (IOException e)
		{
			throw new RuntimeException(e);
		}

	}

	public boolean running()
	{
		return running;
	}

	public void running(boolean r)
	{
		running = r;

		if (!r)
		{
			server.stop();
		}
	}

	public CommandHandler commandHandler()
	{
		return cmdHandler;
	}

	public void commandHandler(CommandHandler h)
	{
		this.cmdHandler = h;
	}

	public ServerGame game()
	{
		return game;
	}

	public ServerPlayerList players()
	{
		return players;
	}
}

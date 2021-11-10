package com.cornchipss.cosmos.server;

import java.io.IOException;

import com.cornchipss.cosmos.game.ServerGame;
import com.cornchipss.cosmos.netty.packets.Packet;
import com.cornchipss.cosmos.server.command.CommandHandler;
import com.cornchipss.cosmos.server.kyros.FancyServer;
import com.cornchipss.cosmos.server.kyros.register.Network;
import com.cornchipss.cosmos.server.kyros.types.Login;
import com.cornchipss.cosmos.server.kyros.types.StatusResponse;
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
		Network.register(server);
		
		running = true;
		this.game = game;

		this.cmdHandler = cmdHandler;
	}

	public void sendToAllUDP(Packet packet)
	{
		for (ServerPlayer p : players.players())
		{
			try
			{
				p.client().sendUDP(packet.buffer(), packet.bufferLength(), this);
			}
			catch (IOException ex)
			{

			}
		}
	}

	public void sendToAllTCP(Packet packet)
	{
		for (ServerPlayer p : players.players())
		{
			try
			{
				p.client().sendTCP(packet.buffer(), packet.bufferLength());
			}
			catch (IOException ex)
			{

			}
		}
	}

	public void sendToAllExceptUDP(Packet packet, ServerPlayer exception)
	{
		for (ServerPlayer p : players.players())
		{
			if (!p.equals(exception))
			{
				try
				{
					p.client().sendUDP(packet.buffer(), packet.bufferLength(), this);
				}
				catch (IOException ex)
				{

				}
			}
		}
	}

	public void sendToAllExceptTCP(Packet packet, ServerPlayer exception)
	{
		for (ServerPlayer p : players.players())
		{
			if (!p.equals(exception))
			{
				try
				{
					p.client().sendTCP(packet.buffer(), packet.bufferLength());
				}
				catch (IOException ex)
				{

				}
			}
		}
	}
	
	@Override
	public void run()
	{
		server.start();
		try
		{
			server.bind(Network.TCP_PORT, Network.UDP_PORT);
		}
		catch (IOException e)
		{
			throw new RuntimeException(e);
		}

		server.addListener(new Listener()
		{
			public void received(Connection connection, Object object)
			{
				if (object instanceof Login)
				{
					Login obj = ((Login)object);
					System.out.println(obj.name());
					
					if(Math.random() < 0.05f)
					{
						connection.close();
						return;
					}
					
					StatusResponse response = new StatusResponse(200);
					connection.sendTCP(response);
				}
			}
		});
	}

	public boolean running()
	{
		return running;
	}

	public void running(boolean r)
	{
		running = r;
		
		if(!r)
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

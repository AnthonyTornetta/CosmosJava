package com.cornchipss.cosmos.client;

import java.io.IOException;
import java.net.InetAddress;
import java.util.LinkedList;
import java.util.List;

import com.cornchipss.cosmos.game.ClientGame;
import com.cornchipss.cosmos.netty.NetworkRegistry;
import com.cornchipss.cosmos.netty.packets.LoginPacket;
import com.cornchipss.cosmos.netty.packets.Packet;
import com.cornchipss.cosmos.server.kyros.NettyClientObserver;
import com.cornchipss.cosmos.utils.Logger;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Listener.ThreadedListener;

public class CosmosNettyClient implements Runnable
{
	private ClientPlayerList players;

	private List<NettyClientObserver> observers = new LinkedList<>();

	private boolean ready = false;

	private ClientGame game;

	private Client client;

	public CosmosNettyClient()
	{
		players = new ClientPlayerList();

		client = new Client(NetworkRegistry.BUFFER_SIZE, NetworkRegistry.BUFFER_SIZE);
	}

	public void createConnection(String ip, int port, String name) throws IOException
	{
		client.start();

		NetworkRegistry.register(client);

		final CosmosNettyClient instance = this;

		client.addListener(new ThreadedListener(new Listener()
		{
			public void received(Connection connection, Object object)
			{
				for (NettyClientObserver o : observers)
				{
					if (o.onReceiveObject(connection, object))
					{
						return;
					}
				}

				if (object instanceof Packet)
				{
					((Packet) object).receiveClient(instance, game);
				}
				else
					Logger.LOGGER.info(object);
			}

			public void disconnected(Connection connection)
			{
				for (NettyClientObserver o : observers)
				{
					o.onDisconnect(connection);
				}

				Logger.LOGGER.info("Disconnected From Server!");
			}
		}));

		client.connect(NetworkRegistry.TIMEOUT_MS, InetAddress.getByName(ip), NetworkRegistry.TCP_PORT,
			NetworkRegistry.UDP_PORT);

		for (NettyClientObserver o : observers)
		{
			o.onConnect();
		}

		sendTCP(new LoginPacket(name));
	}

	public void sendUDP(Object o)
	{
		client.sendUDP(o);
	}

	public void sendTCP(Object o) throws IOException
	{
		client.sendTCP(o);
	}

	public void disconnect() throws IOException
	{
		client.close();
	}

	@Override
	public void run()
	{
		game = new ClientGame(this);

		ready = true;
	}

	public ClientPlayerList players()
	{
		return players;
	}

	public ClientGame game()
	{
		return game;
	}

	/**
	 * If the world is ready to be updated on the client side
	 * 
	 * @return If the world is ready to be updated on the client side
	 */
	public boolean ready()
	{
		return ready;
	}

	/**
	 * If the world is ready to be updated on the client side
	 * 
	 * @param b If the world is ready to be updated on the client side
	 */
	public void ready(boolean b)
	{
		ready = b;
	}

	public void addObserver(NettyClientObserver o)
	{
		observers.add(o);
	}

	public void removeObserver(NettyClientObserver o)
	{
		observers.remove(o);
	}
}

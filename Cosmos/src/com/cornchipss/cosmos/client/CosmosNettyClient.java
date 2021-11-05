package com.cornchipss.cosmos.client;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.util.Scanner;

import com.cornchipss.cosmos.game.ClientGame;
import com.cornchipss.cosmos.netty.PacketTypes;
import com.cornchipss.cosmos.netty.packets.DisconnectedPacket;
import com.cornchipss.cosmos.netty.packets.JoinPacket;
import com.cornchipss.cosmos.netty.packets.Packet;
import com.cornchipss.cosmos.netty.packets.PlayerPacket;
import com.cornchipss.cosmos.utils.Logger;

public class CosmosNettyClient implements Runnable
{
	private ServerConnection server;
	private ClientPlayerList players;

	private boolean ready = false;

	private boolean running = true;

	private String name;

	private ClientGame game;

	public CosmosNettyClient()
	{
		players = new ClientPlayerList();
	}

	public void createConnection(String ip, int port, String name) throws IOException
	{
		this.name = name;

		TCPServerConnection tcpConnection = new TCPServerConnection(this, ip, port);

		server = new ServerConnection(InetAddress.getByName(ip), port, tcpConnection);

		server.initUDPSocket();

		game = new ClientGame(this);
	}

	public void sendUDP(Packet p)
	{
		server.sendUDP(p.buffer(), p.bufferLength(), this);
	}

	public void sendTCP(Packet p) throws IOException
	{
		server.sendTCP(p.buffer(), p.bufferLength(), this);
	}

	public void disconnect() throws IOException
	{
		running = false;
		server.tcpConnection().endConnection();
	}

	@Override
	public void run()
	{
		try (Scanner scan = new Scanner(System.in))
		{
			Thread tcpThread = new Thread(server.tcpConnection());
			tcpThread.start();

			byte[] buffer = new byte[1024];

			ready = true;

			JoinPacket joinP = new JoinPacket(buffer, 0, name);
			joinP.init();

			sendTCP(joinP);
			sendUDP(joinP);

			// udp stuff
			while (running && Client.instance().running())
			{
				try
				{
					DatagramPacket recieved = new DatagramPacket(buffer, buffer.length);

					server.socket().setSoTimeout(1000);
					server.socket().receive(recieved);

					byte marker = Packet.findMarker(buffer, recieved.getOffset(), recieved.getLength());

					Packet p = PacketTypes.packet(marker);
					if (p == null)
						Logger.LOGGER.error("WARNING: Invalid packet type (" + marker + ") received from server");
					else
					{
						try
						{
							p.onReceiveClient(
								recieved.getData(), recieved.getLength(), recieved.getOffset() + Packet
									.additionalOffset(recieved.getData(), recieved.getOffset(), recieved.getLength()),
								server, this);
						}
						catch (Exception ex)
						{
							ex.printStackTrace();
						}
					}
				}
				catch (SocketTimeoutException ex)
				{
				}

				try
				{
					Thread.sleep(10);
				}
				catch (InterruptedException e)
				{
					e.printStackTrace();
				}

				// send player info - TODO: move this
				if (game().player() != null)
				{
					PlayerPacket pp = new PlayerPacket(buffer, 0, game().player());
					pp.init();

					server.sendUDP(pp.buffer(), pp.bufferLength(), this);
				}
			}

			Logger.LOGGER.debug("Sending disconnect packet");

			DisconnectedPacket dcp = new DisconnectedPacket(buffer, 0, game().player().name(), "Disconnected");
			dcp.init();

			try
			{
				sendTCP(dcp);
			}
			catch (IOException ex)
			{
				Logger.LOGGER.info("Could not send disconnect packet - already lost connection");
				// the connection was already closed
			}

			Logger.LOGGER.info("TCP thread joining");
			tcpThread.join();
			Logger.LOGGER.info("TCP thread exited gracefully");
		}
		catch (IOException | InterruptedException ex)
		{
			ex.printStackTrace();
		}
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
}

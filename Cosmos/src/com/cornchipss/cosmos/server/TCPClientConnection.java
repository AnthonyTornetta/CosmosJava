package com.cornchipss.cosmos.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.Socket;

import com.cornchipss.cosmos.netty.PacketTypes;
import com.cornchipss.cosmos.netty.packets.DisconnectedPacket;
import com.cornchipss.cosmos.netty.packets.JoinPacket;
import com.cornchipss.cosmos.netty.packets.Packet;
import com.cornchipss.cosmos.utils.Logger;
import com.cornchipss.cosmos.utils.Utils;

public class TCPClientConnection implements Runnable
{
	private CosmosNettyServer server;
	private boolean active;
	private Socket clientSocket;
	private DataOutputStream writer;

	public TCPClientConnection(Socket clientSocket, CosmosNettyServer serverInstance)
	{
		this.server = serverInstance;
		this.clientSocket = clientSocket;
		active = true;

		try
		{
			writer = new DataOutputStream(clientSocket.getOutputStream());
		}
		catch (IOException ex)
		{
			throw new RuntimeException(ex);
		}
	}

	public void sendData(byte[] buffer, int offset, int length) throws IOException
	{
		writer.writeInt(length);
		writer.write(buffer, offset, length);
		writer.flush();
	}

	@Override
	public void run()
	{
		DataInputStream in;
		try
		{
			in = new DataInputStream(clientSocket.getInputStream());
		}
		catch (IOException ex)
		{
			throw new RuntimeException(ex);
		}

		String name = null;

		while (server.running() && active())
		{
			try
			{
				int nextBufferSize = in.readInt();
				byte[] buffer = in.readNBytes(nextBufferSize);

				ServerPlayer player = server.players().player(this);

				byte marker = Packet.findMarker(buffer, 0, buffer.length);

				Packet p = PacketTypes.packet(marker);

				ClientConnection connection = null;

				if (player == null && p instanceof JoinPacket)
				{
					connection = new ClientConnection(null, 0, this);
				}
				else if (player != null)
					connection = player.client();
				else
					continue;

				if (player != null)
				{
					name = player.name();
				}

				if (p == null)
				{
					// TODO: dont do this - make it a packet
					Logger.LOGGER.error("INVALID PACKET TYPE");
					buffer[0] = -1; // we can reuse the same buffer
					connection.sendTCP(buffer, 1);
					return;
				}

				int off = Packet.additionalOffset(buffer, 0, buffer.length);

				p.onReceiveServer(buffer, buffer.length - off, off, connection, server);
			}
			catch (EOFException ex)
			{
				name = server.players().player(this).name();

				Logger.LOGGER.info("Player " + name + " disconnected.");
				server.players().removePlayer(this);
				active(false);
			}
			catch (Exception ex)
			{
				name = server.players().player(this).name();

				ex.printStackTrace();

				Logger.LOGGER.info("Player " + name + " made an invalid packet - removing them.");
				server.players().removePlayer(this);
				active(false);
			}
		}

		try
		{
			writer.close();

			if (!clientSocket.isClosed())
				clientSocket.close();
		}
		catch (IOException ex)
		{

		}

		if (name != null)
			announceClosingToOthers(name);
		else
			Logger.LOGGER.error("!!! NO NAME DICONNECTED !!!");

		Logger.LOGGER.info("TCP connection closed.");
	}

	private void announceClosingToOthers(String name)
	{
		byte[] buf = new byte[256];
		DisconnectedPacket packet = new DisconnectedPacket(buf, 0, name, "Disconnected by User");
		packet.init();

		for (ServerPlayer pl : Server.nettyServer().players().players())
		{
			try
			{
				pl.client().sendTCP(packet.buffer(), packet.bufferLength());
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
	}

	@Override
	public boolean equals(Object o)
	{
		if (o instanceof TCPClientConnection)
		{
			TCPClientConnection other = (TCPClientConnection) o;
			return Utils.equals(clientSocket, other.clientSocket);
		}
		return false;
	}

	@Override
	public int hashCode()
	{
		return clientSocket.hashCode();
	}

	public boolean active()
	{
		return active;
	}

	public void active(boolean b)
	{
		active = b;
	}
}

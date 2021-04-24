package com.cornchipss.cosmos.client;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

import com.cornchipss.cosmos.server.ClientConnection;
import com.cornchipss.cosmos.utils.Utils;

/**
 * Client -> Server
 */
public class ServerConnection
{
	private InetAddress addr;
	private int port;
	private DatagramSocket socket;
	private TCPServerConnection tcpServerConnection;
	
	public ServerConnection(InetAddress addr, int port)
	{
		this.addr = addr;
		this.port = port;
	}
	
	public ServerConnection(TCPServerConnection tcpServer)
	{
		this.tcpServerConnection = tcpServer;
	}
	
	public void createConnection() throws SocketException
	{
		socket = new DatagramSocket();
	}
	
	public InetAddress address() { return addr; }
	public int port() { return port; }
	
	@Override
	public int hashCode()
	{
		if(tcp())
			return tcpServerConnection.hashCode();
		else
			return addr.hashCode() + port;
	}
	
	@Override
	public boolean equals(Object o)
	{
		if(o instanceof ServerConnection)
		{
			if(tcp())
				return Utils.equals(tcpServerConnection, ((ServerConnection)o).tcpServerConnection);
			else
				return Utils.equals(addr, ((ServerConnection)o).addr) && port == ((ServerConnection)o).port;
		}
		return false;
	}
	
//
//	public void send(byte[] buffer, int length, int offset) throws IOException
//	{
//		DatagramPacket response = new DatagramPacket(buffer, offset, length, address(), port());
//		socket.send(response);
//	}
//
//	public void send(Packet p) throws IOException
//	{
//		send(p.buffer(), p.bufferLength(), p.bufferOffset());
//	}

	public DatagramSocket socket()
	{
		return socket;
	}

	public void send(byte[] buffer, int length, CosmosNettyClient client)
	{
		try
		{
			if(tcp())
			{
				tcpServerConnection.sendData(buffer, 0, length);
			}
			else
			{
				DatagramPacket response = new DatagramPacket(buffer, 0, length, 
						address(), port());
				socket.send(response);
			}
		}
		catch(IOException ex)
		{
			throw new RuntimeException(ex);
		}
	}
	
	public boolean tcp() { return tcpServerConnection != null; }
	public boolean udp() { return !tcp(); }
}

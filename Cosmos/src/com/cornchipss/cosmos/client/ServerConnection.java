package com.cornchipss.cosmos.client;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

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
	
	public ServerConnection(InetAddress addr, int port, TCPServerConnection tcp)
	{
		this.addr = addr;
		this.port = port;
		this.tcpServerConnection = tcp;
	}
	
	public ServerConnection(InetAddress addr, int port)
	{
		this.addr = addr;
		this.port = port;
	}
	
	public void initTCP(TCPServerConnection tcpServer)
	{
		this.tcpServerConnection = tcpServer;
	}
	
	public ServerConnection(TCPServerConnection tcpServer)
	{
		this.tcpServerConnection = tcpServer;
	}
	
	public void initUDP(InetAddress addr, int port)
	{
		this.addr = addr;
		this.port = port;
	}
	
	public void initUDPSocket() throws SocketException
	{
		socket = new DatagramSocket();
	}
	
	public InetAddress address() { return addr; }
	public int port() { return port; }
	
	@Override
	public int hashCode()
	{
		if(hasTCP())
			return tcpServerConnection.hashCode();
		else
			return addr.hashCode() + port;
	}
	
	@Override
	public boolean equals(Object o)
	{
		if(o instanceof ServerConnection)
		{
			if(hasTCP())
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

	public void sendTCP(byte[] buffer, int length, CosmosNettyClient client)
	{
		try
		{
			tcpServerConnection.sendData(buffer, 0, length);
		}
		catch(IOException ex)
		{
			throw new RuntimeException(ex);
		}
	}
	
	public void sendUDP(byte[] buffer, int length, CosmosNettyClient client)
	{
		DatagramPacket response = new DatagramPacket(buffer, 0, length, 
				address(), port());
		try
		{
			socket.send(response);
		}
		catch (IOException ex)
		{
			throw new RuntimeException(ex);
		}
	}
	
	public boolean hasTCP() { return tcpServerConnection != null; }
	public boolean hasUDP() { return addr != null; }
}

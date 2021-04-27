package com.cornchipss.cosmos.server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;

import com.cornchipss.cosmos.utils.Utils;

/**
 * Server -> Client
 */
public class ClientConnection
{
	private InetAddress addr;
	private int port;
	
	private TCPClientConnection tcpClientConnection;
	
	private long lastCommunicationTime;
	
	public ClientConnection(InetAddress addr, int port, TCPClientConnection clientConnection)
	{
		this.addr = addr;
		this.port = port;
		this.tcpClientConnection = clientConnection;
	}
	
	public void updateCommunicationTime()
	{
		lastCommunicationTime = System.currentTimeMillis();
	}
	
	public long lastCommunicationTime()
	{
		return lastCommunicationTime;
	}
	
	public InetAddress address() { return addr; }
	public int port() { return port; }
	
	@Override
	public int hashCode()
	{
		return tcpClientConnection.hashCode() + addr.hashCode() + port;
	}
	
	@Override
	public boolean equals(Object o)
	{
		if(o instanceof ClientConnection)
		{
			return Utils.equals(tcpClientConnection, ((ClientConnection)o).tcpClientConnection) && Utils.equals(addr, ((ClientConnection)o).addr) && port == ((ClientConnection)o).port;
		}
		return false;
	}

	public void sendUDP(byte[] buffer, int length, CosmosNettyServer server) throws IOException
	{
		DatagramPacket response = new DatagramPacket(buffer, 0, length, 
				address(), port());
		server.socket().send(response);
	}
	
	public void sendTCP(byte[] buffer, int length) throws IOException
	{
		tcpClientConnection.sendData(buffer, 0, length);
	}

	public boolean hasUDP()
	{
		return addr != null;
	}
	
	public boolean hasTCP()
	{
		return tcpClientConnection != null;
	}

	public void initUDP(InetAddress address, int port)
	{
		this.addr = address;
		this.port = port;
	}

	public TCPClientConnection tcpConnection()
	{
		return tcpClientConnection;
	}

	public void initTCP(TCPClientConnection tcpConnection)
	{
		this.tcpClientConnection = tcpConnection;
	}

	public void terminateConnection()
	{
		tcpClientConnection.active(false);
		addr = null;
		port = 0;
	}
}

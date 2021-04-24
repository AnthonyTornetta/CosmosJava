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
	
	public ClientConnection(InetAddress addr, int port)
	{
		this.addr = addr;
		this.port = port;
	}
	
	public ClientConnection(TCPClientConnection clientConnection)
	{
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
		if(tcp())
			return tcpClientConnection.hashCode();
		else
			return addr.hashCode() + port;
	}
	
	@Override
	public boolean equals(Object o)
	{
		if(o instanceof ClientConnection)
		{
			if(tcp())
				return Utils.equals(tcpClientConnection, ((ClientConnection)o).tcpClientConnection);
			else
				return Utils.equals(addr, ((ClientConnection)o).addr) && port == ((ClientConnection)o).port;
		}
		return false;
	}

	public void send(byte[] buffer, int length, CosmosNettyServer server) throws IOException
	{
		if(tcp())
		{
			tcpClientConnection.sendData(buffer, 0, length);
		}
		else
		{
			DatagramPacket response = new DatagramPacket(buffer, 0, length, 
					address(), port());
			server.socket().send(response);
		}
	}
	
	public boolean tcp() { return tcpClientConnection != null; }
	public boolean udp() { return !tcp(); }
}

package com.cornchipss.cosmos.server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;

import com.cornchipss.cosmos.utils.Utils;

public class ServerClient
{
	private InetAddress addr;
	private int port;
	
	private long lastCommunicationTime;
	
	public ServerClient(InetAddress addr, int port)
	{
		this.addr = addr;
		this.port = port;
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
		return addr.hashCode() + port;
	}
	
	@Override
	public boolean equals(Object o)
	{
		if(o instanceof ServerClient)
		{
			return Utils.equals(addr, ((ServerClient)o).addr) && port == ((ServerClient)o).port;
		}
		return false;
	}

	public void send(byte[] buffer, int length, CosmosNettyServer server) throws IOException
	{
		DatagramPacket response = new DatagramPacket(buffer, length, address(), port());
		server.socket().send(response);
	}
}

package com.cornchipss.cosmos.client;

import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

import com.cornchipss.cosmos.server.ServerClient;
import com.cornchipss.cosmos.utils.Utils;

public class ClientServer
{
	private InetAddress addr;
	private int port;
	private DatagramSocket socket;
	
	public ClientServer(InetAddress addr, int port)
	{
		this.addr = addr;
		this.port = port;
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
		return addr.hashCode() + port;
	}
	
	@Override
	public boolean equals(Object o)
	{
		if(o instanceof ServerClient)
		{
			return Utils.equals(addr, ((ClientServer)o).addr) && port == ((ClientServer)o).port;
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
}

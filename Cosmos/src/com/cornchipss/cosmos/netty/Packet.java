package com.cornchipss.cosmos.netty;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import com.cornchipss.cosmos.server.CosmosNettyServer;
import com.cornchipss.cosmos.server.ServerClient;

public abstract class Packet
{
	private byte[] buffer;
	private int bufferOffset;
	private int bufferLength;
	
	private int writingAt;
	
	public Packet()
	{
		
	}
	
	public Packet(byte[] buffer, int bufferOffset)
	{
		this.buffer = buffer;
		writingAt = bufferOffset;
		bufferLength = 0;
		this.bufferOffset = bufferOffset;
		
		write(marker());
	}
	
	public void write(byte b)
	{
		buffer[writingAt++] = b;
		bufferLength++;
	}
	
	public void write(String s)
	{
		for(byte b : s.getBytes())
			write(b);
	}
	
	public void send(DatagramSocket socket, InetAddress address, int port) throws IOException
	{
		DatagramPacket packet = new DatagramPacket(
				buffer(), bufferOffset(), bufferLength(),
				address, port);
		socket.send(packet);
	}
	
	public abstract void onReceive(byte[] data, int len, int offset, 
			ServerClient client, CosmosNettyServer server);
	
	public abstract byte marker();
	
	public byte[] buffer() { return buffer; }
	public int bufferOffset() { return bufferOffset; }
	public int bufferLength() { return bufferLength; }

	public static byte findMarker(byte[] buffer, int offset, int length)
	{
		return buffer[offset];
	}

	public static int additionalOffset(byte[] buffer, int offset, int length)
	{
		return 1;
	}
}

package com.cornchipss.cosmos.netty.packets;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import org.joml.Quaternionf;
import org.joml.Quaternionfc;
import org.joml.Vector3f;
import org.joml.Vector3fc;

import com.cornchipss.cosmos.client.ClientServer;
import com.cornchipss.cosmos.client.CosmosNettyClient;
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
	}
	
	/**
	 * Initializes this packet to begin writing to
	 */
	public void init()
	{
		write(marker());
	}
	
	public void write(byte b)
	{
		buffer[writingAt++] = b;
		bufferLength++;
	}
	
	public void write(String s)
	{
		write(s.length());
		for(byte b : s.getBytes())
			write(b);
	}
	
	public void write(float f)
	{
		ByteBuffer.wrap(buffer, writingAt, 4).order(ByteOrder.BIG_ENDIAN)
			.putFloat(f);
		writingAt += 4;
		bufferLength += 4;
	}
	
	public void write(int value)
	{
		// Big endian
		ByteBuffer.wrap(buffer, writingAt, 4).order(ByteOrder.BIG_ENDIAN)
			.putInt(value);
		writingAt += 4;
		bufferLength += 4;
	}
	
	public void write(Vector3fc vec)
	{
		write(vec.x());
		write(vec.y());
		write(vec.z());
	}
	
	public void write(Quaternionfc q)
	{
		write(q.x());
		write(q.y());
		write(q.z());
		write(q.w());
	}
	
	public byte readByte()
	{
		return buffer[writingAt++];
	}
	
	public int readInt()
	{
		int val = ByteBuffer.wrap(buffer, writingAt, 4).getInt();
		writingAt += 4;
		return val;
	}
	
	public float readFloat()
	{
		float val = ByteBuffer.wrap(buffer, writingAt, 4).getFloat();
		writingAt += 4;
		return val;
	}
	
	public String readString()
	{
		int len = readInt();
		String s = new String(buffer, writingAt, len);
		writingAt += len;
		return s;
	}
	
	public Quaternionf readQuaternionf(Quaternionf into)
	{
		return into.set(readFloat(), readFloat(), readFloat(), readFloat());
	}

	public Vector3f readVector3f(Vector3f into)
	{
		return into.set(readFloat(), readFloat(), readFloat());
	}
	
	public void send(DatagramSocket socket, InetAddress address, int port) throws IOException
	{
		DatagramPacket packet = new DatagramPacket(
				buffer(), bufferOffset(), bufferLength(),
				address, port);
		socket.send(packet);
	}
	
	public abstract void onReceiveServer(byte[] data, int len, int offset, 
			ServerClient client, CosmosNettyServer server);
	
	public abstract void onReceiveClient(byte[] data, int len, int offset, 
			ClientServer server, CosmosNettyClient client);
	
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

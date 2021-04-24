package com.cornchipss.cosmos.netty.packets;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import org.joml.Quaternionf;
import org.joml.Quaternionfc;
import org.joml.Vector3f;
import org.joml.Vector3fc;
import org.joml.Vector3ic;

import com.cornchipss.cosmos.client.CosmosNettyClient;
import com.cornchipss.cosmos.client.ServerConnection;
import com.cornchipss.cosmos.server.ClientConnection;
import com.cornchipss.cosmos.server.CosmosNettyServer;

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
		writeByte(marker());
	}
	
	public void writeByte(byte b)
	{
		buffer[writingAt++] = b;
		bufferLength++;
	}
	
	public void writeString(String s)
	{
		writeInt(s.length());
		for(byte b : s.getBytes())
			writeByte(b);
	}
	
	public void writeFloat(float f)
	{
		ByteBuffer.wrap(buffer, writingAt, 4).order(ByteOrder.BIG_ENDIAN)
			.putFloat(f);
		writingAt += 4;
		bufferLength += 4;
	}
	
	public void writeInt(int value)
	{
		// Big endian
		ByteBuffer.wrap(buffer, writingAt, 4).order(ByteOrder.BIG_ENDIAN)
			.putInt(value);
		writingAt += 4;
		bufferLength += 4;
	}
	
	public void writeShort(short value)
	{
		// Big endian
		ByteBuffer.wrap(buffer, writingAt, 2).order(ByteOrder.BIG_ENDIAN)
			.putShort(value);
		writingAt += 2;
		bufferLength += 2;
	}
	
	public void writeVector3fc(Vector3fc vec)
	{
		writeFloat(vec.x());
		writeFloat(vec.y());
		writeFloat(vec.z());
	}
	
	public void writeVector3ic(Vector3ic vec)
	{
		writeInt(vec.x());
		writeInt(vec.y());
		writeInt(vec.z());
	}
	
	public void writeQuaternionfc(Quaternionfc q)
	{
		writeFloat(q.x());
		writeFloat(q.y());
		writeFloat(q.z());
		writeFloat(q.w());
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
	
	public short readShort()
	{
		short val = ByteBuffer.wrap(buffer, writingAt, 2).getShort();
		writingAt += 2;
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
	
	public abstract void onReceiveServer(byte[] data, int len, int offset, 
			ClientConnection client, CosmosNettyServer server);
	
	public abstract void onReceiveClient(byte[] data, int len, int offset, 
			ServerConnection server, CosmosNettyClient client);
	
	public abstract byte marker();
	
	public byte[] buffer() { return buffer; }
	public void buffer(byte[] buf) { buffer = buf; }
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

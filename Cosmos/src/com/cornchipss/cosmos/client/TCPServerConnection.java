package com.cornchipss.cosmos.client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

import com.cornchipss.cosmos.netty.PacketTypes;
import com.cornchipss.cosmos.netty.packets.Packet;
import com.cornchipss.cosmos.utils.Logger;
import com.cornchipss.cosmos.utils.Utils;

public class TCPServerConnection implements Runnable
{
	private Socket serverSocket;
	private DataOutputStream writer;
	private DataInputStream in;
	private CosmosNettyClient client;
	private boolean connected;
	
	public TCPServerConnection(CosmosNettyClient client, String ip, int port) throws UnknownHostException, IOException
	{
		this.client = client;
		serverSocket = new Socket(ip, port);
		
		connected = true;
		writer = new DataOutputStream(serverSocket.getOutputStream());
		in = new DataInputStream(serverSocket.getInputStream());
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
		while(active())
		{
			try
			{
				int nextBufferSize = in.readInt();
				byte[] buffer = in.readNBytes(nextBufferSize);
				
				ServerConnection server = new ServerConnection(this);
				
				byte marker = Packet.findMarker(buffer, 0, buffer.length);
				
				Packet p = PacketTypes.packet(marker);
				
				if(p == null)
				{
					Utils.println("INVALID PACKET TYPE - " + marker);
					buffer[0] = -1; // we can reuse the same buffer
					server.sendTCP(buffer, 1, client);
					return;
				}
				
				int off = Packet.additionalOffset(buffer, 0, buffer.length);
				
				p.onReceiveClient(buffer, buffer.length - off, off, server, client);
			}
			catch(EOFException ex)
			{
				connected = false;
			}
			catch(IOException ex)
			{
				throw new RuntimeException(ex);
			}
		}
		
		try
		{
			endConnection();
		}
		catch(IOException ex)
		{
			// already closed
		}
		
		Logger.LOGGER.info("TCP connection to server closed.");
	}
	
	public void endConnection() throws IOException
	{
		connected = false;
		writer.close();
		in.close();
		serverSocket.close();
    }
	
	@Override
	public boolean equals(Object o)
	{
		if(o instanceof TCPServerConnection)
		{
			TCPServerConnection other = (TCPServerConnection)o;
			return Utils.equals(serverSocket, other.serverSocket);
		}
		return false;
	}
	
	@Override
	public int hashCode()
	{
		return serverSocket.hashCode();
	}
	
	public boolean active() { return connected && (client.game() == null || client.game().running()); }
}

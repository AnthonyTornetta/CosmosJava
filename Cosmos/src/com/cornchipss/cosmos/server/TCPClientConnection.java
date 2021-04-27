package com.cornchipss.cosmos.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

import com.cornchipss.cosmos.netty.PacketTypes;
import com.cornchipss.cosmos.netty.packets.JoinPacket;
import com.cornchipss.cosmos.netty.packets.Packet;
import com.cornchipss.cosmos.utils.Utils;

public class TCPClientConnection implements Runnable
{
	private CosmosNettyServer server;
	private boolean active;
	private Socket clientSocket;
	private DataOutputStream writer;
	
	public TCPClientConnection(Socket clientSocket, CosmosNettyServer serverInstance)
	{
		this.server = serverInstance;
		this.clientSocket = clientSocket;
		active = true;
		
		try
		{
			writer = new DataOutputStream(clientSocket.getOutputStream());
		}
		catch(IOException ex)
		{
			throw new RuntimeException(ex);
		}
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
		DataInputStream in;
		try
		{
			in = new DataInputStream(clientSocket.getInputStream());
		}
		catch(IOException ex)
		{
			throw new RuntimeException(ex);
		}
		
		while(server.running() && active())
		{
			try
			{
				Utils.println("WAITING FOR DATA");
				int nextBufferSize = in.readInt();
				Utils.println("GOT DATA");	
				byte[] buffer = in.readNBytes(nextBufferSize);
				
				ServerPlayer player = server.players().player(this);
				
				byte marker = Packet.findMarker(buffer, 0, buffer.length);
				
				Packet p = PacketTypes.packet(marker);
				
				ClientConnection connection = null;
				
				if(player == null && p instanceof JoinPacket)
				{
					connection = new ClientConnection(null, 0, this);
				}
				else if(player != null)
					connection = player.client();
				else
					continue;
				
				if(p == null)
				{
					Utils.println("INVALID PACKET TYPE");
					buffer[0] = -1; // we can reuse the same buffer
					connection.sendTCP(buffer, 1);
					return;
				}
				
				int off = Packet.additionalOffset(buffer, 0, buffer.length);
				
				Utils.println(buffer.length - off);
				p.onReceiveServer(buffer, buffer.length - off, off, connection, server);
			}
			catch(IOException ex)
			{
				throw new RuntimeException(ex);
			}
		}
	}
	
	@Override
	public boolean equals(Object o)
	{
		if(o instanceof TCPClientConnection)
		{
			TCPClientConnection other = (TCPClientConnection)o;
			return Utils.equals(clientSocket, other.clientSocket);
		}
		return false;
	}
	
	@Override
	public int hashCode()
	{
		return clientSocket.hashCode();
	}
	
	public boolean active() { return active; }
	public void active(boolean b) { active = b; }
}

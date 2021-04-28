package com.cornchipss.cosmos.server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.ServerSocket;
import java.net.Socket;

import com.cornchipss.cosmos.game.ServerGame;
import com.cornchipss.cosmos.netty.PacketTypes;
import com.cornchipss.cosmos.netty.packets.JoinPacket;
import com.cornchipss.cosmos.netty.packets.Packet;
import com.cornchipss.cosmos.server.command.CommandHandler;
import com.cornchipss.cosmos.utils.Utils;

public class CosmosNettyServer implements Runnable
{
	private volatile boolean running;
	
	private final ServerGame game;
	
	private CommandHandler cmdHandler;
	
	private ServerPlayerList players = new ServerPlayerList();
	
	private Thread udpThread;//, tcpThread;
	
	private DatagramSocket udpSocket;
	private ServerSocket tcpSocket;
	
	public CosmosNettyServer(ServerGame game, CommandHandler cmdHandler)
	{
		running = true;
		this.game = game;
		
		this.cmdHandler = cmdHandler;
	}
	
	public void sendToAllUDP(Packet packet)
	{
		for(ServerPlayer p : players.players())
		{
			try
			{
				p.client().sendUDP(packet.buffer(), packet.bufferLength(), this);
			}
			catch(IOException ex)
			{
				
			}
		}
	}
	
	private synchronized void processUDP(DatagramPacket packet, DatagramSocket serverSocket) throws IOException
	{
		ServerPlayer player = players.player(packet.getAddress(), packet.getPort());
		
		byte[] buffer = packet.getData();
		
		byte marker = Packet.findMarker(buffer, packet.getOffset(), packet.getLength());
		
		Packet p = PacketTypes.packet(marker);
		
		if(p == null)
		{
			Utils.println("INVALID PACKET TYPE - " + marker);
			buffer[0] = -1; // we can reuse the same buffer
			player.client().sendUDP(buffer, 1, this);
			return;
		}
		
		ClientConnection connection = null;
		
		if(player == null && p instanceof JoinPacket)
		{
			connection = new ClientConnection(packet.getAddress(), packet.getPort(), null);
		}
		else if(player != null)
			connection = player.client();
		else
			return;
		
		int off = Packet.additionalOffset(buffer, packet.getOffset(), packet.getLength());
		
		p.onReceiveServer(buffer, packet.getLength() - off, packet.getOffset() + off, connection, this);
	}
	
	@Override
	public void run()
	{
		final CosmosNettyServer serverInstance = this;
		
		try
		{
			udpSocket = new DatagramSocket(1337);
			tcpSocket = new ServerSocket(1337);
		}
		catch(IOException ex)
		{
			System.err.println("Unable to setup server - throwing error.");
			throw new RuntimeException(ex);
		}
		
		udpThread = new Thread(() ->
		{
			byte[] buffer = new byte[1024];  // 1kb max data
			
			Utils.println("UDP server listening...");
			
			while(running)
			{
				try
				{
					DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
					udpSocket.receive(packet);
					
					processUDP(packet, udpSocket);
				}
				catch (IOException e)
				{
					throw new RuntimeException(e);
				}
			}			
			
			udpSocket.close();
		});
		udpThread.start();
		
//		tcpThread = new Thread(() ->
//		{
////			byte[] buffer = new byte[1024*1000];  // 1000kb max data
//			
//			while(running)
//			{
//				try
//				{
//					Socket clientSocket = tcpSocket.accept();
//					
//					TCPClientConnection connection = new TCPClientConnection(clientSocket, serverInstance);
//					
//					Thread connectionThread = new Thread(connection);
//					connectionThread.start();
//				}
//				catch (IOException e)
//				{
//					throw new RuntimeException(e);
//				}
//			}		
//		});
		
		while(running)
		{
			Utils.println("Waiting for next TCP connection...");
			try
			{
				Socket clientSocket = tcpSocket.accept();
				
				TCPClientConnection connection = new TCPClientConnection(clientSocket, serverInstance);
				
				Thread connectionThread = new Thread(connection);
				
				Utils.println("TCP connection created!");
				connectionThread.start();
			}
			catch (IOException e)
			{
				throw new RuntimeException(e);
			}
		}
		
		try
		{
			udpThread.join();
//			tcpThread.join();
		}
		catch (InterruptedException e)
		{
			e.printStackTrace();
		}
	}
	
	public boolean running() { return running; }
	public void running(boolean r) { running = r; }
	
	public CommandHandler commandHandler() { return cmdHandler; }
	public void commandHandler(CommandHandler h) { this.cmdHandler = h; }
	
	public ServerGame game() { return game; }

	public ServerPlayerList players() { return players; }

	public DatagramSocket socket() { return udpSocket; }
}

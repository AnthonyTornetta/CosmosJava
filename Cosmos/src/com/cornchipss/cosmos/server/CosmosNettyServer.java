package com.cornchipss.cosmos.server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

import com.cornchipss.cosmos.game.ServerGame;
import com.cornchipss.cosmos.server.command.CommandHandler;
import com.cornchipss.cosmos.utils.Utils;

public class CosmosNettyServer implements Runnable
{
	private volatile boolean running;
	
	private final ServerGame game;
	
	private CommandHandler cmdHandler;
	
	public CosmosNettyServer(ServerGame game, CommandHandler cmdHandler)
	{
		running = true;
		this.game = game;
		
		this.cmdHandler = cmdHandler;
	}
	
	private synchronized void process(DatagramPacket packet, DatagramSocket serverSocket) throws IOException
	{
		Utils.println(new String(packet.getData(), packet.getOffset(), packet.getLength()));
		
		byte[] buffer = packet.getData(); // we can reuse the same buffer
		
		if(buffer[0] == '0')
		{
			buffer[0] = 'O';
			buffer[1] = 'k';
		}
		else
		{
			buffer[0] = 'H';
			buffer[1] = 'i';
		}
		
		DatagramPacket response = new DatagramPacket(buffer, 2, packet.getAddress(), packet.getPort());
		
		serverSocket.send(response);
	}
	
	@Override
	public void run()
	{
		DatagramSocket sock;
		try
		{
			sock = new DatagramSocket(1337);
		
			byte[] buffer = new byte[1024];
			
			while(running)
			{
				DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
				sock.receive(packet);
				
				if(buffer[0] == '0')
				{
					running(false);
				}
				
				process(packet, sock);
			}
			
			sock.close();
		}
		catch (IOException e)
		{
			throw new RuntimeException(e);
		}

	}
	
	public ServerGame game()
	{
		return game;
	}
	
	public boolean running() { return running; }
	public void running(boolean r) { running = r; }
	
	public CommandHandler commandHandler() { return cmdHandler; }
	public void commandHandler(CommandHandler h) { this.cmdHandler = h; }
}

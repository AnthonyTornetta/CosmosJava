package com.cornchipss.cosmos.client;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.util.Scanner;

import com.cornchipss.cosmos.game.ClientGame;
import com.cornchipss.cosmos.netty.PacketTypes;
import com.cornchipss.cosmos.netty.packets.JoinPacket;
import com.cornchipss.cosmos.netty.packets.Packet;
import com.cornchipss.cosmos.netty.packets.PlayerPacket;
import com.cornchipss.cosmos.utils.Logger;
import com.cornchipss.cosmos.utils.Utils;

public class CosmosNettyClient implements Runnable
{
	private ClientServer server;
	private ClientPlayerList players;
	
	private ClientGame game;
	
	public CosmosNettyClient(ClientGame game)
	{
		players = new ClientPlayerList();
		this.game = game;
	}
	
	@Override
	public void run()
	{
		try(Scanner scan = new Scanner(System.in))
		{
			server = new ClientServer(InetAddress.getByName("localhost"), 1337);
			server.createConnection();
			
        	byte[] buffer = new byte[1024];
	        
	        Utils.println("name: ");
	        String name = scan.nextLine();
	        
	        JoinPacket joinP = new JoinPacket(buffer, 0, name);
	        joinP.init();
	        
	        joinP.send(server.socket(), server.address(), server.port());
	        
	        while(game.running())
	        {
	        	DatagramPacket recieved = new DatagramPacket(buffer, buffer.length);
	        	server.socket().receive(recieved);
	        	
	        	byte marker = Packet.findMarker(buffer, recieved.getOffset(), recieved.getLength());
	    		
	    		Packet p = PacketTypes.packet(marker);
	    		if(p == null)
	    			Logger.LOGGER.error("WARNING: Invalid packet type (" + marker + ") received from server");
	    		else
	    			p.onReceiveClient(recieved.getData(), recieved.getLength(), recieved.getOffset()
	    					+ Packet.additionalOffset(recieved.getData(), recieved.getOffset(), recieved.getLength()), server, this);
	        	
	    		try
	    		{
					Thread.sleep(10);
				}
	    		catch (InterruptedException e)
	    		{
					e.printStackTrace();
				}
	    		
	        	PlayerPacket pp = new PlayerPacket(buffer, 0, game().player());
	        	pp.init();
	        	
	        	pp.send(server.socket(), server.address(), server.port());
	        }
		}
		catch(IOException ex)
		{
			ex.printStackTrace();
		}
	}

	public ClientPlayerList players()
	{
		return players;
	}

	public ClientGame game()
	{
		return game;
	}
}

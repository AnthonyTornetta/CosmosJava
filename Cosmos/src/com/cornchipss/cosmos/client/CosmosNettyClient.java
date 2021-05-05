package com.cornchipss.cosmos.client;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.util.Scanner;

import com.cornchipss.cosmos.game.ClientGame;
import com.cornchipss.cosmos.netty.PacketTypes;
import com.cornchipss.cosmos.netty.packets.DisconnectedPacket;
import com.cornchipss.cosmos.netty.packets.JoinPacket;
import com.cornchipss.cosmos.netty.packets.Packet;
import com.cornchipss.cosmos.netty.packets.PlayerPacket;
import com.cornchipss.cosmos.utils.Logger;
import com.cornchipss.cosmos.utils.Utils;

public class CosmosNettyClient implements Runnable
{
	private ServerConnection server;
	private ClientPlayerList players;
	
	private boolean ready = false;
	
	public CosmosNettyClient()
	{
		players = new ClientPlayerList();
	}
	
	public void sendUDP(Packet p)
	{
		server.sendUDP(p.buffer(), p.bufferLength(), this);
	}
	
	public void sendTCP(Packet p)
	{
		server.sendTCP(p.buffer(), p.bufferLength(), this);
	}
	
	@Override
	public void run()
	{
		try(Scanner scan = new Scanner(System.in))
		{
			TCPServerConnection tcpConnection = 
					new TCPServerConnection(this, "localhost", 1337);
			
			server = new ServerConnection(
					InetAddress.getByName("localhost"), 1337,
					tcpConnection);
			
			server.initUDPSocket();
			
			Thread tcpThread = new Thread(tcpConnection);
			tcpThread.start();
			
        	byte[] buffer = new byte[1024];
	        
	        Utils.println("name: ");
	        String name = scan.nextLine();
	        
	        ready = true;
	        
	        JoinPacket joinP = new JoinPacket(buffer, 0, name);
	        joinP.init();
	        
	        sendTCP(joinP);
	        sendUDP(joinP);
	        
	        // udp stuff
	        while(Client.instance().running())
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
	    		
	    		// send player info - TODO: move this
	        	PlayerPacket pp = new PlayerPacket(buffer, 0, game().player());
	        	pp.init();
	        	
	        	server.sendUDP(pp.buffer(), pp.bufferLength(), this);
	        }
	        
	        
	        DisconnectedPacket dcp = new DisconnectedPacket(buffer, 0, "Disconnected");
	        dcp.init();
	        
	        try
	        {
	        	server.sendTCP(dcp.buffer(), dcp.bufferLength(), this);
	        }
	        catch(Exception ex)
	        {
	        	// the connection was already closed
	        }
	        
	        tcpThread.join();
		}
		catch(IOException | InterruptedException ex)
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
		return ClientGame.instance();
	}

	/**
	 * If the world is ready to be updated on the client side
	 * @return If the world is ready to be updated on the client side
	 */
	public boolean ready()
	{
		return ready;
	}
	
	/**
	 * If the world is ready to be updated on the client side
	 * @param b If the world is ready to be updated on the client side
	 */
	public void ready(boolean b)
	{
		ready = b;
	}
}

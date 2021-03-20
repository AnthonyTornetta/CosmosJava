package com.cornchipss.cosmos.client;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Scanner;

import com.cornchipss.cosmos.netty.JoinPacket;
import com.cornchipss.cosmos.netty.PlayerPacket;
import com.cornchipss.cosmos.utils.Utils;

public class CosmosNettyClient implements Runnable
{
	private DatagramSocket socket;
	private InetAddress address;
	
	@Override
	public void run()
	{
		try(Scanner scan = new Scanner(System.in))
		{
			socket = new DatagramSocket();
	        address = InetAddress.getByName("localhost");
	        
	        final int port = 1337;

        	byte[] buffer = new byte[1024];
	        
	        Utils.println("name: ");
	        String name = scan.nextLine();
	        
	        JoinPacket joinP = new JoinPacket(buffer, 0, name);
	        
	        joinP.send(socket, address, port);
	        
	        while(true)
	        {	        	
	        	DatagramPacket recieved = new DatagramPacket(buffer, buffer.length);
	        	socket.receive(recieved);
	        	
	        	Utils.println((int)buffer[0]);

	        	name = scan.nextLine();
	        	
	        	PlayerPacket pp = new PlayerPacket(buffer, 0);
	        	
	        	pp.write(name);
	        	
	        	pp.send(socket, address, port);
	        }
		}
		catch(IOException ex)
		{
			
		}
	}
}

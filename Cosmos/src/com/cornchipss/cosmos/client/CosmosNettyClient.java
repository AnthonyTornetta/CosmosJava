package com.cornchipss.cosmos.client;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Scanner;

import com.cornchipss.cosmos.utils.Utils;

public class CosmosNettyClient implements Runnable
{
	private DatagramSocket socket;
	private InetAddress address;
	
	@Override
	public void run()
	{
		try
		{
			socket = new DatagramSocket();
	        address = InetAddress.getByName("localhost");
	        
	        Scanner scan = new Scanner(System.in);
	        
	        while(true)
	        {
	        	byte[] buf = new byte[1024];
	        	
	        	buf[0] = (byte)scan.nextLine().charAt(0);
	        	
	        	DatagramPacket packet = new DatagramPacket(buf, buf.length, address, 1337);
	        	socket.send(packet);
	        	
	        	DatagramPacket recieved = new DatagramPacket(buf, buf.length);
	        	socket.receive(recieved);
	        	
	        	Utils.println(new String(packet.getData(), packet.getOffset(), packet.getLength()));
	        }
		}
		catch(IOException ex)
		{
			
		}
	}
}

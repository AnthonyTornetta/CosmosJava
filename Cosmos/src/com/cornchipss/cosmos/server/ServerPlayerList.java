package com.cornchipss.cosmos.server;

import java.net.InetAddress;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.cornchipss.cosmos.physx.Transform;
import com.cornchipss.cosmos.world.World;

public class ServerPlayerList
{
	private static final class AddressPort
	{
		InetAddress addr;
		int port;
		
		private AddressPort(InetAddress a, int p)
		{
			addr = a;
			port = p;
		}
		
		@Override
		public String toString()
		{
			return addr + ":" + port;
		}
		
		@Override
		public boolean equals(Object o)
		{
			return o instanceof AddressPort && ((AddressPort)o).port == port && ((AddressPort)o).addr.equals(addr);
		}
		
		@Override
		public int hashCode()
		{
			return addr.hashCode() + port;
		}
	}
	
	private Map<String, ClientConnection> incompleteJoins = new HashMap<>();
	
	private Map<ClientConnection, ServerPlayer> playerClients;
	private Map<String, ServerPlayer> playerNames;
	private Map<AddressPort, ServerPlayer> playerAddrs;
	private Map<TCPClientConnection, ServerPlayer> playerTCPs;
	private List<ServerPlayer> players;
	
	public ServerPlayerList()
	{
		playerClients = new HashMap<>();
		playerNames = new HashMap<>();
		playerAddrs = new HashMap<>();
		playerTCPs = new HashMap<>();
		players = new LinkedList<>();
	}
	
	public void beginJoin(ClientConnection c, String name)
	{
		incompleteJoins.put(name, c);
	}
	
	public ClientConnection getPendingConnection(String name)
	{
		return incompleteJoins.get(name);
	}
	
	public void finishJoin(ClientConnection c, World world, String name)
	{
		incompleteJoins.remove(name);
		
		createPlayer(world, c, name);
	}
	
	public boolean playerExists(ClientConnection c)
	{
		return playerClients.containsKey(c);
	}
	
	public boolean nameTaken(String name)
	{
		return playerNames.containsKey(name.toLowerCase());
	}
	
	private void addPlayer(ClientConnection c, ServerPlayer p)
	{
		playerClients.put(c, p);
		playerNames.put(p.name().toLowerCase(), p);
		playerAddrs.put(new AddressPort(c.address(), c.port()), p);
		playerTCPs.put(c.tcpConnection(), p);
		players.add(p);
	}
	
	public ServerPlayer createPlayer(World world, ClientConnection c, String name)
	{
		if(!playerExists(c) && !nameTaken(name))
		{
			ServerPlayer p = new ServerPlayer(world, c, name);
			
			addPlayer(c, p);
			
			p.addToWorld(new Transform());
			
			return p;
		}
		
		return null;
	}
	
	public void removePlayer(ServerPlayer p)
	{
		ClientConnection c = p.client();
		
		playerTCPs.remove(c.tcpConnection());
		playerAddrs.remove(new AddressPort(c.address(), c.port()));
		playerNames.remove(p.name().toLowerCase());
		players.remove(p);
	}
	
	public boolean removePlayer(ClientConnection c)
	{
		ServerPlayer p = playerClients.remove(c);
		
		if(p == null)
			return false;
		
		removePlayer(p);
		return true;
	}
	
	public boolean removePlayer(String name)
	{
		ServerPlayer p = playerNames.remove(name.toLowerCase());
		
		if(p == null)
			return false;
		
		removePlayer(p);
		return true;
	}

	public boolean removePlayer(TCPClientConnection tcpClientConnection)
	{
		ServerPlayer p = playerTCPs.get(tcpClientConnection);
		
		if(p == null)
			return false;
		
		removePlayer(p);
		return true;
	}
	
	public ServerPlayer player(ClientConnection client)
	{
		return playerClients.get(client);
	}
	
	public ServerPlayer player(String name)
	{
		return playerNames.get(name.toLowerCase());
	}
	
	public ServerPlayer player(InetAddress address, int port)
	{
		return playerAddrs.get(new AddressPort(address, port));
	}

	public ServerPlayer player(TCPClientConnection tcpClientConnection)
	{
		return playerTCPs.get(tcpClientConnection);
	}
	
	
	public List<ServerPlayer> players() { return players; }
}

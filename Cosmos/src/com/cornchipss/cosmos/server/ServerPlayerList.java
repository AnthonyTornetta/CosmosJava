package com.cornchipss.cosmos.server;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class ServerPlayerList
{
	private Map<ServerClient, ServerPlayer> playerClients;
	private Map<String, ServerPlayer> playerNames;
	private List<ServerPlayer> players;
	
	public ServerPlayerList()
	{
		playerClients = new HashMap<>();
		playerNames = new HashMap<>();
		players = new LinkedList<>();
	}
	
	public boolean playerExists(ServerClient c)
	{
		return playerClients.containsKey(c);
	}
	
	public boolean nameTaken(String name)
	{
		return playerNames.containsKey(name.toLowerCase());
	}
	
	public boolean createPlayer(ServerClient c, String name)
	{
		if(!playerExists(c) && !nameTaken(name))
		{
			ServerPlayer p = new ServerPlayer(null, c, name);
			
			playerClients.put(c, p);
			playerNames.put(name, p);
			players.add(p);
			
			return true;
		}
		
		return false;
	}
	
	public boolean removePlayer(ServerClient c)
	{
		ServerPlayer p = playerClients.remove(c);
		
		if(p == null)
			return false;
		
		playerNames.remove(p.name());
		players.remove(p);
		
		return true;
	}
	
	public boolean removePlayer(String name)
	{
		ServerPlayer p = playerNames.remove(name);
		
		if(p == null)
			return false;
		
		playerClients.remove(p.client());
		players.remove(p);
		
		return true;
	}

	public ServerPlayer player(ServerClient client)
	{
		return playerClients.get(client);
	}
	
	public ServerPlayer playerFromName(String name)
	{
		return playerNames.get(name);
	}
	
	public List<ServerPlayer> players() { return players; }
}

package com.cornchipss.cosmos.server;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.cornchipss.cosmos.physx.Transform;
import com.cornchipss.cosmos.utils.Utils;
import com.cornchipss.cosmos.world.World;

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
	
	public ServerPlayer createPlayer(World world, ServerClient c, String name)
	{
		if(!playerExists(c) && !nameTaken(name))
		{
			ServerPlayer p = new ServerPlayer(world, c, name);
			
			playerClients.put(c, p);
			playerNames.put(name.toLowerCase(), p);
			players.add(p);
			
			p.addToWorld(new Transform());
			
			return p;
		}
		
		Utils.println("L:");
		
		return null;
	}
	
	public boolean removePlayer(ServerClient c)
	{
		ServerPlayer p = playerClients.remove(c);
		
		if(p == null)
			return false;
		
		playerNames.remove(p.name().toLowerCase());
		players.remove(p);
		
		return true;
	}
	
	public boolean removePlayer(String name)
	{
		ServerPlayer p = playerNames.remove(name.toLowerCase());
		
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
	
	public ServerPlayer player(String name)
	{
		return playerNames.get(name.toLowerCase());
	}
	
	public List<ServerPlayer> players() { return players; }
}

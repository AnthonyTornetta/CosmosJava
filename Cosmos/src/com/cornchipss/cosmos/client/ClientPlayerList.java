package com.cornchipss.cosmos.client;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.cornchipss.cosmos.world.entities.player.Player;

public class ClientPlayerList
{
	private Map<String, Player> names;
	private Set<Player> players;
	
	public ClientPlayerList()
	{
		names = new HashMap<>();
		players = new HashSet<>();
	}
	
	public void addPlayer(Player p)
	{
		names.put(p.name(), p);
		players.add(p);
	}
	
	public void removePlayer(Player p)
	{
		names.remove(p.name());
		players.remove(p);
	}
	
	public Player player(String name)
	{
		return names.get(name);
	}
	
	public boolean hasPlayer(String name)
	{
		return names.containsKey(name);
	}
	
	public boolean hasPlayer(Player p)
	{
		return players.contains(p);
	}
	
	public Set<Player> players()
	{
		return players;
	}
}

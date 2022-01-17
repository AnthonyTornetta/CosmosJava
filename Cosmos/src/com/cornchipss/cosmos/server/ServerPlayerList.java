package com.cornchipss.cosmos.server;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import com.cornchipss.cosmos.physx.Transform;
import com.cornchipss.cosmos.server.kyros.ClientConnection;
import com.cornchipss.cosmos.world.World;
import com.esotericsoftware.kryonet.Connection;

public class ServerPlayerList implements Iterable<ServerPlayer>
{
	private List<ServerPlayer> players;

	public ServerPlayerList()
	{
		players = new LinkedList<>();
	}

	public boolean nameTaken(String name)
	{
		for (ServerPlayer p : players)
			if (p.name().toLowerCase().equals(name))
				return true;
		return false;
	}

	public boolean connectionRegistered(Connection c)
	{
		for (ServerPlayer p : players)
			if (p.connection().equals(c))
				return true;
		return false;
	}

	public ServerPlayer createPlayer(World world, ClientConnection c,
		String name)
	{
		if (!connectionRegistered(c) && !nameTaken(name))
		{
			ServerPlayer p = new ServerPlayer(world, c, name);

			players.add(p);

			p.addToWorld(new Transform());

			players.add(p);

			return p;
		}

		return null;
	}

	public void removePlayer(ServerPlayer p)
	{
		players.remove(p);
	}

	public ServerPlayer player(String name)
	{
		for (ServerPlayer p : players)
			if (p.name().equals(name))
				return p;
		return null;
	}

	public List<ServerPlayer> players()
	{
		return players;
	}

	@Override
	public Iterator<ServerPlayer> iterator()
	{
		return players.iterator();
	}
}

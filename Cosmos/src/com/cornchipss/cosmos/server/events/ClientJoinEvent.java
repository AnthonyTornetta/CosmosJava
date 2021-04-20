package com.cornchipss.cosmos.server.events;

import com.cornchipss.cosmos.server.ServerClient;

public class ClientJoinEvent extends ClientEvent implements ICancellable
{
	private ServerClient origin;
	
	private boolean cancelled;
	
	public ClientJoinEvent(ServerClient origin )
	{
		this.origin = origin;
	}
	
	public ServerClient origin()
	{
		return origin;
	}

	@Override
	public void cancelled(boolean b)
	{
		cancelled = b;
	}

	@Override
	public boolean cancelled()
	{
		return cancelled;
	}
}

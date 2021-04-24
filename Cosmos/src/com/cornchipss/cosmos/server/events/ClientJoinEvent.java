package com.cornchipss.cosmos.server.events;

import com.cornchipss.cosmos.server.ClientConnection;

public class ClientJoinEvent extends ClientEvent implements ICancellable
{
	private ClientConnection origin;
	
	private boolean cancelled;
	
	public ClientJoinEvent(ClientConnection origin )
	{
		this.origin = origin;
	}
	
	public ClientConnection origin()
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

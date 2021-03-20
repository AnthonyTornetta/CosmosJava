package com.cornchipss.cosmos.server.events;

public interface ICancellable
{
	public void cancelled(boolean b);
	public boolean cancelled();
}

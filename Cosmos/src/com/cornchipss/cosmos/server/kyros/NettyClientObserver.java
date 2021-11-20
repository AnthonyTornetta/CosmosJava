package com.cornchipss.cosmos.server.kyros;

import com.esotericsoftware.kryonet.Connection;

public interface NettyClientObserver
{
	public boolean onReceiveObject(Connection connection, Object object);

	public void onDisconnect(Connection connection);

	public void onConnect();
}

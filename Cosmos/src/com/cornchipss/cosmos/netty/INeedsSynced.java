package com.cornchipss.cosmos.netty;

import com.cornchipss.cosmos.client.CosmosNettyClient;
import com.cornchipss.cosmos.server.CosmosNettyServer;

public interface INeedsSynced
{
	public void updateClient(CosmosNettyClient client);
	public void updateServer(CosmosNettyServer server);
	
	public boolean dirty();
}

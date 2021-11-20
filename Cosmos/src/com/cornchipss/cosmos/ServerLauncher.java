package com.cornchipss.cosmos;

import com.cornchipss.cosmos.server.CosmosServer;

/*
 * note:
 * https://github.com/EsotericSoftware/kryonet/issues/154
 */

public class ServerLauncher
{
	public static void main(String[] args)
	{
		new CosmosServer().run();
	}
}

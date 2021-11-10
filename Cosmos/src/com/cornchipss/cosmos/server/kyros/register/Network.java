package com.cornchipss.cosmos.server.kyros.register;

import com.cornchipss.cosmos.server.kyros.types.Login;
import com.cornchipss.cosmos.server.kyros.types.StatusResponse;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.EndPoint;

public class Network
{
	public static final int TCP_PORT = 54455, UDP_PORT = TCP_PORT;
	
	public static void register(EndPoint endPoint)
	{
		Kryo k = endPoint.getKryo();
		k.register(Login.class);
		k.register(StatusResponse.class);
	}
}

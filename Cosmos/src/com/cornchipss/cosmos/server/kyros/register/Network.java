package com.cornchipss.cosmos.server.kyros.register;

import org.joml.Quaternionf;
import org.joml.Quaternionfc;
import org.joml.Vector3f;
import org.joml.Vector3fc;
import org.joml.Vector3i;
import org.joml.Vector3ic;

import com.cornchipss.cosmos.netty.packets.ClientInteractPacket;
import com.cornchipss.cosmos.netty.packets.LoginPacket;
import com.cornchipss.cosmos.netty.packets.Packet;
import com.cornchipss.cosmos.netty.packets.PlayerDisconnectPacket;
import com.cornchipss.cosmos.server.kyros.types.StatusResponse;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.EndPoint;

public class Network
{
	public static final int TCP_PORT = 54455, UDP_PORT = TCP_PORT;
	public static final int TIMEOUT_MS = 10_000;
	
	public static void register(EndPoint endPoint)
	{
		Kryo k = endPoint.getKryo();
		k.register(Vector3fc.class);
		k.register(Vector3f.class);
		k.register(Quaternionfc.class);
		k.register(Quaternionf.class);
		k.register(Vector3ic.class);
		k.register(Vector3i.class);
		
		k.register(Packet.class);
		k.register(StatusResponse.class);
		k.register(LoginPacket.class);
		k.register(PlayerDisconnectPacket.class);
		k.register(ClientInteractPacket.class);
	}
}

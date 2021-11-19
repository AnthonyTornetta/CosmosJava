package com.cornchipss.cosmos.netty;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.joml.Vector3i;

import com.cornchipss.cosmos.netty.action.PlayerAction;
import com.cornchipss.cosmos.netty.packets.ClientInteractPacket;
import com.cornchipss.cosmos.netty.packets.ExitShipPacket;
import com.cornchipss.cosmos.netty.packets.JoinPacket;
import com.cornchipss.cosmos.netty.packets.LoginPacket;
import com.cornchipss.cosmos.netty.packets.ModifyBlockPacket;
import com.cornchipss.cosmos.netty.packets.MovementPacket;
import com.cornchipss.cosmos.netty.packets.Packet;
import com.cornchipss.cosmos.netty.packets.PlayerActionPacket;
import com.cornchipss.cosmos.netty.packets.PlayerDisconnectPacket;
import com.cornchipss.cosmos.netty.packets.PlayerInteractPacket;
import com.cornchipss.cosmos.netty.packets.PlayerPacket;
import com.cornchipss.cosmos.netty.packets.StructurePacket;
import com.cornchipss.cosmos.netty.packets.StructureStatusPacket;
import com.cornchipss.cosmos.structures.Planet;
import com.cornchipss.cosmos.structures.Ship;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.EndPoint;

public class NetworkRegistry
{
	public static final int TCP_PORT = 54455, UDP_PORT = TCP_PORT;
	public static final int TIMEOUT_MS = 10_000;

	public static final int BUFFER_SIZE = 4_096_000;

	private static Set<Class<?>> registered = new HashSet<>();

	private static void register(Class<?> c, Kryo k)
	{
		k.register(c);

		registered.add(c);
	}

	/**
	 * For debug purposes check if a type has been registered
	 * 
	 * @param c
	 * @return
	 */
	public static boolean check(Class<?> c)
	{
		return registered.contains(c);
	}

	public static void register(EndPoint endPoint)
	{
		Kryo k = endPoint.getKryo();
		register(Vector3f.class, k);
		register(Quaternionf.class, k);
		register(Vector3i.class, k);
		register(byte[].class, k);
		register(Class.class, k);
		register(ArrayList.class, k);

		register(Planet.class, k);
		register(Ship.class, k);
		register(PlayerAction.class, k);

		register(Packet.class, k);
		register(JoinPacket.class, k);
		register(LoginPacket.class, k);
		register(PlayerDisconnectPacket.class, k);
		register(ClientInteractPacket.class, k);
		register(StructurePacket.class, k);
		register(PlayerActionPacket.class, k);
		register(PlayerPacket.class, k);
		register(JoinPacket.class, k);
		register(ModifyBlockPacket.class, k);
		register(PlayerInteractPacket.class, k);
		register(ExitShipPacket.class, k);
		register(StructureStatusPacket.class, k);
		register(MovementPacket.class, k);
	}
}

package com.cornchipss.cosmos.netty;

import java.util.HashMap;
import java.util.Map;

import com.cornchipss.cosmos.netty.packets.ClientInteractPacket;
import com.cornchipss.cosmos.netty.packets.ClientMovementPacket;
import com.cornchipss.cosmos.netty.packets.DebugPacket;
import com.cornchipss.cosmos.netty.packets.DisconnectedPacket;
import com.cornchipss.cosmos.netty.packets.ExitShipPacket;
import com.cornchipss.cosmos.netty.packets.FullStructurePacket;
import com.cornchipss.cosmos.netty.packets.JoinFinishPacket;
import com.cornchipss.cosmos.netty.packets.JoinPacket;
import com.cornchipss.cosmos.netty.packets.ModifyBlockPacket;
import com.cornchipss.cosmos.netty.packets.Packet;
import com.cornchipss.cosmos.netty.packets.PlayerPacket;
import com.cornchipss.cosmos.netty.packets.ShipMovementPacket;

public class PacketTypes
{
	private static Map<Byte, Packet> packetTypes = new HashMap<>();
	private static Map<Class<? extends Packet>, Byte> markers = new HashMap<>();
	
	private static byte marker = 1;
	
	public static void addPacketType(Packet p)
	{
//		if(packetTypes.containsKey(p.marker()))
//			throw new IllegalArgumentException("Packet of marker " + p.marker() + " has already been registered!");
		packetTypes.put(marker, p);
		markers.put(p.getClass(), marker);
		marker++;
	}
	
	public static Packet packet(byte marker)
	{
		return packetTypes.get(marker);
	}
	
	public static byte marker(Class<? extends Packet> packetClass)
	{
		if(!markers.containsKey(packetClass))
			throw new IllegalArgumentException("PacketClass " + packetClass.getName() + " not registered!");
		
		return markers.get(packetClass);
	}
	
	public static void registerAll()
	{
		addPacketType(new JoinPacket());
		addPacketType(new PlayerPacket());
		addPacketType(new DisconnectedPacket());
		addPacketType(new ModifyBlockPacket());
		addPacketType(new FullStructurePacket());
		addPacketType(new DebugPacket());
		addPacketType(new JoinFinishPacket());
		addPacketType(new ClientMovementPacket());
		addPacketType(new ShipMovementPacket());
		addPacketType(new ClientInteractPacket());
		addPacketType(new ExitShipPacket());
	}
}

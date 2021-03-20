package com.cornchipss.cosmos.netty;

import java.util.HashMap;
import java.util.Map;

public class PacketTypes
{
	private static Map<Byte, Packet> packetTypes = new HashMap<>();
	
	public static void addPacketType(Packet p)
	{
		packetTypes.put(p.marker(), p);
	}
	
	public static Packet packet(byte marker)
	{
		return packetTypes.get(marker);
	}
}

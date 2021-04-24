package com.cornchipss.cosmos.netty;

import java.util.HashMap;
import java.util.Map;

import com.cornchipss.cosmos.netty.packets.DisconnectedPacket;
import com.cornchipss.cosmos.netty.packets.FullStructurePacket;
import com.cornchipss.cosmos.netty.packets.JoinPacket;
import com.cornchipss.cosmos.netty.packets.ModifyBlockPacket;
import com.cornchipss.cosmos.netty.packets.Packet;
import com.cornchipss.cosmos.netty.packets.PlayerPacket;

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

	public static void registerAll()
	{
		PacketTypes.addPacketType(new JoinPacket());
		PacketTypes.addPacketType(new PlayerPacket());
		PacketTypes.addPacketType(new DisconnectedPacket());
		PacketTypes.addPacketType(new ModifyBlockPacket());
		PacketTypes.addPacketType(new FullStructurePacket());
		
	}
}

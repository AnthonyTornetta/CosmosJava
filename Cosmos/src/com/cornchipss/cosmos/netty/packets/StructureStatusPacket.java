package com.cornchipss.cosmos.netty.packets;

import org.joml.Quaternionfc;
import org.joml.Vector3fc;

import com.cornchipss.cosmos.client.CosmosNettyClient;
import com.cornchipss.cosmos.game.ClientGame;
import com.cornchipss.cosmos.game.ServerGame;
import com.cornchipss.cosmos.server.CosmosNettyServer;
import com.cornchipss.cosmos.server.kyros.ClientConnection;
import com.cornchipss.cosmos.structures.Structure;

public class StructureStatusPacket extends Packet
{
	private int sid;
	private Vector3fc pos;
	private Quaternionfc rot;
	
	float energy, maxEnergy;
	
	public StructureStatusPacket()
	{
		
	}
	
	public StructureStatusPacket(Structure s)
	{
		sid = s.id();
		pos = s.position();
		rot = s.body().transform().orientation().quaternion();
		energy = s.energy();
		maxEnergy = s.maxEnergy();
	}
	
	@Override
	public void receiveClient(CosmosNettyClient client, ClientGame game)
	{
		Structure s = game.world().structureFromID(sid);
		
		if(s == null)
			return;
		
		s.body().transform().position(pos);
		s.body().transform().orientation().quaternion(rot);
		s.energy(energy);
		s.maxEnergy(maxEnergy);
	}

	@Override
	public void receiveServer(CosmosNettyServer server, ServerGame game, ClientConnection c)
	{
		// not happening
	}
}

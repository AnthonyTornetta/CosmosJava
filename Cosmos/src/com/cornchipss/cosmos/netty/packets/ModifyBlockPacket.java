package com.cornchipss.cosmos.netty.packets;

import com.cornchipss.cosmos.blocks.Block;
import com.cornchipss.cosmos.blocks.Blocks;
import com.cornchipss.cosmos.blocks.StructureBlock;
import com.cornchipss.cosmos.client.CosmosNettyClient;
import com.cornchipss.cosmos.game.ClientGame;
import com.cornchipss.cosmos.game.ServerGame;
import com.cornchipss.cosmos.server.CosmosNettyServer;
import com.cornchipss.cosmos.server.kyros.ClientConnection;
import com.cornchipss.cosmos.structures.Structure;

public class ModifyBlockPacket extends Packet
{
	int x, y, z;
	int sid;
	short bid;
	
	public ModifyBlockPacket()
	{

	}
	
	public ModifyBlockPacket(StructureBlock b, Block newB)
	{
		x = b.structureX();
		y = b.structureY();
		z = b.structureZ();
		sid = b.structure().id();
		if(newB != null)
			bid = newB.numericId();
		else
			bid = 0;
	}

	@Override
	public void receiveClient(CosmosNettyClient client, ClientGame game)
	{
		Structure s = game.world().structureFromID(sid);
		Block b = Blocks.fromNumericId(bid);
		
		s.block(x, y, z, b);
	}

	@Override
	public void receiveServer(CosmosNettyServer server, ServerGame game, ClientConnection c)
	{
		Structure s = game.world().structureFromID(sid);
		Block b = Blocks.fromNumericId(bid);
		
		s.block(x, y, z, b);
		
		server.sendToAllTCP(this);
	}
}

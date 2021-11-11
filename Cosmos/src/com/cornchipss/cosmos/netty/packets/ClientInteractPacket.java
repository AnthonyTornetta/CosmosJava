package com.cornchipss.cosmos.netty.packets;

import com.cornchipss.cosmos.blocks.StructureBlock;
import com.cornchipss.cosmos.blocks.modifiers.IInteractable;
import com.cornchipss.cosmos.client.CosmosNettyClient;
import com.cornchipss.cosmos.game.ClientGame;
import com.cornchipss.cosmos.game.ServerGame;
import com.cornchipss.cosmos.server.CosmosNettyServer;
import com.cornchipss.cosmos.server.kyros.ClientConnection;
import com.cornchipss.cosmos.structures.Structure;

public class ClientInteractPacket extends Packet
{
	private int id;
	private int x, y, z;

	public ClientInteractPacket()
	{
	}

	public ClientInteractPacket(StructureBlock b)
	{
		id = b.structure().id();

		x = b.structureX();
		y = b.structureY();
		z = b.structureZ();
	}

	@Override
	public void receiveServer(CosmosNettyServer server, ServerGame game,
		ClientConnection c)
	{
		Structure s = game.world().structureFromID(id);
			((IInteractable) s.block(x, y, z)).onInteract(
				new StructureBlock(s, x, y, z), c.player());
	}

	@Override
	public void receiveClient(CosmosNettyClient client, ClientGame game)
	{	
		Structure s = game.world().structureFromID(id);
			((IInteractable) s.block(x, y, z)).onInteract(
				new StructureBlock(s, x, y, z), game.player());
	}
}

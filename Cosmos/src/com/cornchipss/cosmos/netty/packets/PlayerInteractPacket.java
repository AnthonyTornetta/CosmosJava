package com.cornchipss.cosmos.netty.packets;

import com.cornchipss.cosmos.blocks.StructureBlock;
import com.cornchipss.cosmos.blocks.modifiers.IInteractable;
import com.cornchipss.cosmos.client.CosmosNettyClient;
import com.cornchipss.cosmos.game.ClientGame;
import com.cornchipss.cosmos.game.ServerGame;
import com.cornchipss.cosmos.server.CosmosNettyServer;
import com.cornchipss.cosmos.server.kyros.ClientConnection;
import com.cornchipss.cosmos.structures.Structure;
import com.cornchipss.cosmos.world.entities.player.Player;

public class PlayerInteractPacket extends Packet
{
	private int sid, x, y, z;
	private String name;

	public PlayerInteractPacket()
	{

	}

	public PlayerInteractPacket(StructureBlock b)
	{
		sid = b.structure().id();
		x = b.structureX();
		y = b.structureY();
		z = b.structureZ();
	}

	@Override
	public void receiveClient(CosmosNettyClient client, ClientGame game)
	{
		Structure s = game.world().structureFromID(sid);
		if (s == null)
			return;
		
		StructureBlock b = new StructureBlock(s, x, y, z);
		Player p = client.players().player(name);

		if (s.block(x, y, z) instanceof IInteractable)
		{
			((IInteractable) s.block(x, y, z)).onInteract(b, p);
		}
	}

	@Override
	public void receiveServer(CosmosNettyServer server, ServerGame game, ClientConnection c)
	{
		Structure s = game.world().structureFromID(sid);
		if (s == null)
			return;

		StructureBlock b = new StructureBlock(s, x, y, z);
		Player p = c.player();

		if (s.block(x, y, z) instanceof IInteractable)
		{
			((IInteractable) s.block(x, y, z)).onInteract(b, p);
			name = c.player().name();

			server.sendToAllTCP(this);
		}
	}
}

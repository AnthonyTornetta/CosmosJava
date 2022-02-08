package com.cornchipss.cosmos.structures;

import com.cornchipss.cosmos.blocks.Block;
import com.cornchipss.cosmos.blocks.StructureBlock;
import com.cornchipss.cosmos.netty.packets.ModifyBlockPacket;
import com.cornchipss.cosmos.server.CosmosServer;
import com.cornchipss.cosmos.utils.Utils;

public class ServerStructureObserver implements StructureObserver
{
	@Override
	public void onBlockModify(StructureBlock block,
		Block from, Block to)
	{
		if(block.structure().initialized())
		{
			if(!Utils.equals(from, to))
			{
				ModifyBlockPacket mbp = new ModifyBlockPacket(block, to);
				
				CosmosServer.nettyServer().sendToAllTCP(mbp);
			}
		}
	}
}

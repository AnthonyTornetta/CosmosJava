package com.cornchipss.cosmos.structures;

import com.cornchipss.cosmos.blocks.Block;
import com.cornchipss.cosmos.blocks.StructureBlock;

public interface StructureObserver
{
	public void onBlockModify(StructureBlock block, Block from, Block to);
}

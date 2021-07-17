package com.cornchipss.cosmos.systems;

import java.util.List;

import com.cornchipss.cosmos.blocks.StructureBlock;
import com.cornchipss.cosmos.structures.Structure;

public abstract class BlockSystem
{
	public abstract void addBlock(StructureBlock added, List<StructureBlock> otherBlocks);
	public abstract void removeBlock(StructureBlock removed, List<StructureBlock> otherBlocks);
	
	public abstract void update(Structure s, List<StructureBlock> blocks, float delta);
}

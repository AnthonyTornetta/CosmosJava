package com.cornchipss.cosmos.systems;

import com.cornchipss.cosmos.blocks.StructureBlock;
import com.cornchipss.cosmos.structures.Structure;

public abstract class BlockSystem
{
	private Structure s;
	
	public BlockSystem(Structure s)
	{
		this.s = s;
	}
	
	public abstract void addBlock(StructureBlock added);

	public abstract void removeBlock(StructureBlock removed);

	public abstract void update(float delta);
	
	public abstract String id();
	
	public Structure structure() { return s; };
}

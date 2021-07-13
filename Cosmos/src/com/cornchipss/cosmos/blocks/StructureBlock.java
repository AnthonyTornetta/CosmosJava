package com.cornchipss.cosmos.blocks;

import com.cornchipss.cosmos.structures.Structure;

public class StructureBlock
{
	private Structure structure;
	private int x, y, z;
	
	public StructureBlock(Structure s, int x, int y, int z)
	{
		this.structure = s;
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public Structure structure()
	{
		return structure;
	}

	public int structureX()
	{
		return x;
	}

	public int structureY()
	{
		return y;
	}

	public int structureZ()
	{
		return z;
	}

	public Block block()
	{
		return structure.block(x, y, z);
	}
	
	public void changeBlock(Block to)
	{
		structure.block(x, y, z, to);
	}
}

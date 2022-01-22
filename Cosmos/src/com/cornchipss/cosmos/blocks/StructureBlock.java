package com.cornchipss.cosmos.blocks;

import org.joml.Vector3i;
import org.joml.Vector3ic;

import com.cornchipss.cosmos.structures.Structure;

public class StructureBlock
{
	private Structure structure;
	private Vector3i position;

	public StructureBlock(Structure s, int x, int y, int z)
	{
		this.structure = s;
		this.position = new Vector3i(x, y, z);
	}

	public Structure structure()
	{
		return structure;
	}

	public Vector3ic position()
	{
		return position;
	}

	public int structureX()
	{
		return position.x;
	}

	public int structureY()
	{
		return position.y;
	}

	public int structureZ()
	{
		return position.z;
	}

	public Block block()
	{
		return structure.block(position);
	}

	public void changeBlock(Block to)
	{
		structure.block(position, to);
	}

	@Override
	public boolean equals(Object o)
	{
		if (!(o instanceof StructureBlock))
			return false;

		StructureBlock otr = (StructureBlock) o;

		return position.equals(otr.position) && structure.equals(otr.structure);
	}

	@Override
	public int hashCode()
	{
		return position.hashCode() * structure.hashCode();
	}
}

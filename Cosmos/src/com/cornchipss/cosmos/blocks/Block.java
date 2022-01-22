package com.cornchipss.cosmos.blocks;

import org.joml.Vector3f;
import org.joml.Vector3fc;

import com.cornchipss.cosmos.blocks.data.BlockData;
import com.cornchipss.cosmos.blocks.modifiers.IHasData;
import com.cornchipss.cosmos.models.CubeModel;
import com.cornchipss.cosmos.models.IHasModel;
import com.cornchipss.cosmos.structures.Structure;

/**
 * <p>
 * A block in the world
 * </p>
 * <p>
 * Only one instance of each block should ever be present
 * </p>
 * <p>
 * Each block of the same type in the world points to that instance
 * </p>
 * <p>
 * Use {@link IHasData} to differentiate between different blocks
 * </p>
 */
public class Block implements IHasModel
{
	private CubeModel model;

	private short id;

	private String name;

	private float mass;

	private float maxDamage;

	/**
	 * <p>
	 * A block in the world
	 * </p>
	 * <p>
	 * Only one instance of each block should ever be present
	 * </p>
	 * <p>
	 * Each block of the same type in the world points to that instance
	 * </p>
	 * <p>
	 * Use {@link BlockData} to differentiate between different blocks
	 * </p>
	 * 
	 * @param m         The model the block has
	 * @param name      The name used to refer to the block in the r@Override
	 *                  egistry
	 * @param mass      The mass of the block in kilograms (water is 1000kg)
	 * @param maxDamage The max damage this block can take before breaking
	 */
	public Block(CubeModel m, String name, float mass, float maxDamage)
	{
		this.model = m;
		this.name = name;
		this.mass = mass;
		this.maxDamage = maxDamage;

		id = -1;
	}

	@Override
	public CubeModel model()
	{
		return model;
	}

	public short numericId()
	{
		if (id == -1)
			throw new IllegalStateException(
				"Id of a block was asked for before the block was initialized");

		return id;
	}

	public void blockId(short s)
	{
		if (id != -1)
			throw new IllegalStateException(
				"Id of a block cannot be set more than once!!!");

		id = s;
	}

	/**
	 * Determines whether this block can be added to a given structure
	 * 
	 * @param s The structure to check
	 * @return True if it can be added, false if not
	 */
	public boolean canAddTo(Structure s)
	{
		return true;
	}

	public float mass()
	{
		return mass;
	}

	@Override
	public boolean equals(Object o)
	{
		if (o instanceof Block)
		{
			return ((Block) o).numericId() == numericId();
		}

		return false;
	}

	@Override
	public int hashCode()
	{
		return numericId();
	}

	public String name()
	{
		return name;
	}

	@Override
	public String toString()
	{
		return "Block [" + name() + "]";
	}

	public void takeDamage(StructureBlock b, float dmg)
	{
		BlockData data = b.structure().blockData(b.structureX(), b.structureY(),
			b.structureZ());
		data.takeDamage(dmg);

		if (data.damage() >= maxDamage())
			b.structure().removeBlock(b.structureX(), b.structureY(),
				b.structureZ());
	}

	public float maxDamage()
	{
		return maxDamage;
	}

	/**
	 * Halfwidths for OBB collision detection
	 */
	private Vector3fc halfwidths = new Vector3f(0.5f, 0.5f, 0.5f);

	/**
	 * Halfwidths for OBB collision detection
	 */
	public Vector3fc halfWidths()
	{
		return halfwidths;
	}

	public BlockData generateData(Structure s, int x, int y, int z)
	{
		return new BlockData();
	}
}

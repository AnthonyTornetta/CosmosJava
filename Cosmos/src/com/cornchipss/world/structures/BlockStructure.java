package com.cornchipss.world.structures;

import java.util.HashMap;
import java.util.Map;

import org.joml.Vector3f;
import org.joml.Vector3fc;
import org.joml.Vector3i;
import org.joml.Vector3ic;

import com.cornchipss.physics.Transform;
import com.cornchipss.registry.Blocks;
import com.cornchipss.rendering.Model;
import com.cornchipss.utils.Utils;
import com.cornchipss.utils.datatypes.Vector3fList;
import com.cornchipss.world.blocks.Block;
import com.cornchipss.world.objects.PhysicalObject;
import com.cornchipss.world.sector.Sector;

public abstract class BlockStructure extends PhysicalObject
{
	/**
	 * The total mass of the planet - stored as a variable to avoid calculating it every time
	 */
	private float mass;
	
	/**
	 * Every block that makes up the BlockStructure
	 */
	private short[][][] blocks;
	
	/**
	 * Dimensions of the BlockStructure
	 */
	private Vector3i dimensions;
	
	/**
	 * The sector the BlockStructure is a part of (wow)
	 */
	private Sector sector;
	
	
	private BulkModel model;
	
	public BlockStructure(int width, int height, int length, float rx, float ry, float rz)
	{
		dimensions = new Vector3i(width, height, length);
		setTransform(new Transform());
		transform().rotation(rx, ry, rz);
	}
	
	@Override
	public float getMass()
	{
		return mass;
	}
	
	/**
	 * <p>Sets the sector that the BlockStructure is a part of</p>
	 * <p>Make sure to update variables such as the BlockStructure's position in the sector!</p>
	 * @param sector The sector to set it to
	 */
	public void sector(Sector sector) { this.sector = sector; }
	public Sector sector() { return sector; }
	
	/**
	 * Saves memory by not creating an array for every BlockStructure, even if the actual blocks haven't been generated/set yet.
	 * This is called whenever the {@link BlockStructure#setBlock(int, int, int, boolean, short)} function is called for the first time.
	 */
	private void initBlocks()
	{
		blocks = new short[dimensions.z][dimensions.y][dimensions.x];
	}
	
	private boolean within(int x, int y, int z)
	{
		return x >= beginningCornerX() && x <= endingCornerX() &&
				y >= beginningCornerY() && y <= endingCornerY() &&
				z >= beginningCornerZ() && z <= endingCornerZ();
	}
	
	/**
	 * <p>Updates every model in the BlockStructure, so call sparingly.</p>
	 */
	public void render()
	{
		model.render();
	}
	
	/**
	 * Gets a block at the relative coordinates of the BlockStructure's center
	 * @param x The x coordinate relative to the BlockStructure's center
	 * @param y The y coordinate relative to the BlockStructure's center
	 * @param z The z coordinate relative to the BlockStructure's center
	 * @return The block at a given coordinate relative the BlockStructure's center
	 */
	public Block blockAt(int x, int y, int z)
	{
		if(x < beginningCornerX() || x > endingCornerX())
			throw new IndexOutOfBoundsException("X (" + x + ") is out of the planet's bounds! (" + beginningCornerX() + " to " + endingCornerX() + ")");
		if(y < beginningCornerY() || y > endingCornerY())
			throw new IndexOutOfBoundsException("Y (" + y + ") is out of the planet's bounds! (" + beginningCornerY() + " to " + endingCornerY() + ")");
		if(z < beginningCornerZ() || z > endingCornerZ())
			throw new IndexOutOfBoundsException("Z (" + z + ") is out of the planet's bounds! (" + beginningCornerZ() + " to " + endingCornerZ() + ")");
		
		if(blocks == null)
			return null;
		
		short id = blocks[z - beginningCornerZ()][y - beginningCornerY()][x - beginningCornerX()];
		
		return Blocks.getBlock(id);
	}
	
	/**
	 * Gets a block at the relative coordinates of the BlockStructure's center
	 * @param c The coordinates relative to the BlockStructure's center
	 * @return The block at a given coordinate relative the BlockStructure's center
	 */
	public Block blockAt(Vector3i c)
	{
		return blockAt(c.x, c.y, c.z);
	}

	/**
	 * Sees if there is a block at the relative coordinates of the BlockStructure's center
	 * @param c The coordinates relative to the BlockStructure's center
	 * @return If there is a block at a given coordinate relative the BlockStructure's center
	 */
	public boolean hasBlockAt(Vector3f c)
	{
		return hasBlockAt(c.x, c.y, c.z);
	}
	
	/**
	 * Sees if there is a block at the relative coordinates of the BlockStructure's center
	 * @param x The x coordinate relative to the BlockStructure's center
	 * @param y The y coordinate relative to the BlockStructure's center
	 * @param z The z coordinate relative to the BlockStructure's center
	 * @return If there is a block at a given coordinate relative the BlockStructure's center
	 */
	public boolean hasBlockAt(float x, float y, float z)
	{
		Vector3f actualBeginning = getBeginningCorner();
		Vector3f actualEnd = getEndingCorner();
		
		return isGenerated() && x >= actualBeginning.x && x < actualEnd.x &&
				y >= actualBeginning.y && y < actualEnd.y &&
				z >= actualBeginning.z && z < actualEnd.z;
	}
	
	/**
	 * Sets a block at the given coordinate, relative to the BlockStructure and updates the BlockStructure's model
	 * @param x The x coordinate, relative to the BlockStructure's center
	 * @param y The y coordinate, relative to the BlockStructure's center
	 * @param z The z coordinate, relative to the BlockStructure's center
	 * @param b The block to set it to
	 */
	public void setBlock(int x, int y, int z, Block b)
	{
		setBlock(x, y, z, b.getId());
	}
	
	/**
	 * Sets a block at the given coordinate, relative to the BlockStructure
	 * @param x The x coordinate, relative to the BlockStructure's center
	 * @param y The y coordinate, relative to the BlockStructure's center
	 * @param z The z coordinate, relative to the BlockStructure's center
	 * @param b The block to set it to
	 */
	public void setBlock(int x, int y, int z, short id)
	{
		if(x < beginningCornerX() || x > endingCornerX())
			throw new IndexOutOfBoundsException("X (" + x + ") is out of the planet's bounds! (" + beginningCornerX() + " to " + endingCornerX() + ")");
		if(y < beginningCornerY() || y > endingCornerY())
			throw new IndexOutOfBoundsException("Y (" + y + ") is out of the planet's bounds! (" + beginningCornerY() + " to " + endingCornerY() + ")");
		if(z < beginningCornerZ() || z > endingCornerZ())
			throw new IndexOutOfBoundsException("Z (" + z + ") is out of the planet's bounds! (" + beginningCornerZ() + " to " + endingCornerZ() + ")");

		if(blocks == null)
			initBlocks();
		
		int zz = z - beginningCornerZ();
		int yy = y - beginningCornerY();
		int xx = x - beginningCornerX();
		
		if(id != blockAt(x, y, z).getId())
		{
			Block oldBlock = blockAt(x, y, z);
			
			blocks[zz][yy][xx] = id;
			
			mass = mass - oldBlock.getMass() + blockAt(x, y, z).getMass();
			
			model.setBlock(x, y, z, Blocks.getBlock(id), oldBlock);
		}
	}
	
	/**
	 * Gets the corner of the BlockStructure in the negative x direction
	 * @return The corner of the BlockStructure in the negative x direction
	 */
	public int beginningCornerX()
	{
		return -getWidth() / 2;
	}
	
	/**
	 * Gets the corner of the BlockStructure in the negative y direction
	 * @return The corner of the BlockStructure in the negative y direction
	 */
	public int beginningCornerY()
	{
		return -getHeight() / 2;
	}
	
	/**
	 * Gets the corner of the BlockStructure in the negative z direction
	 * @return The corner of the BlockStructure in the negative z direction
	 */
	public int beginningCornerZ()
	{
		return -getLength() / 2;
	}
	
	/**
	 * Gets the corner of the BlockStructure in the positive x direction
	 * @return The corner of the BlockStructure in the positive x direction
	 */
	public int endingCornerX()
	{
		if(getWidth() % 2 == 0)
			return getWidth() / 2 - 1;
		else
			return getWidth() / 2;
	}
	
	/**
	 * Gets the corner of the BlockStructure in the positive y direction
	 * @return The corner of the BlockStructure in the positive y direction
	 */
	public int endingCornerY()
	{
		if(getHeight() % 2 == 0)
			return getHeight() / 2 - 1;
		else
			return getHeight() / 2;
	}
	
	/**
	 * Gets the corner of the BlockStructure in the positive z direction
	 * @return The corner of the BlockStructure in the positive z direction
	 */
	public int endingCornerZ()
	{
		if(getLength() % 2 == 0)
			return getLength() / 2 - 1;
		else
			return getLength() / 2;
	}
	
	/**
	 * The x of the BlockStructure relative to the position of the sector
	 * @return The x of the BlockStructure relative to the position of the sector
	 */
	public float getSectorX() { return sectorCoords.x; }
	
	/**
	 * The y of the BlockStructure relative to the position of the sector
	 * @return The y of the BlockStructure relative to the position of the sector
	 */
	public float getSectorY() { return sectorCoords.y; }
	
	/**
	 * The z of the BlockStructure relative to the position of the sector
	 * @return The z of the BlockStructure relative to the position of the sector
	 */
	public float getSectorZ() { return sectorCoords.z; }
	
	/**
	 * Sets the coords of the BlockStructure relative to the center of the sector
	 * @param coords The coords relative to the center of the center
	 */
	public void setSectorCoords(Vector3fc coords)
	{
		setSectorCoords(coords.x(), coords.y(), coords.z());
	}
	
	/**
	 * Sets the coords of the BlockStructure relative to the center of the sector
	 * @param x The x coord relative to the center of the center
	 * @param y The y coord relative to the center of the center
	 * @param z The z coord relative to the center of the center
	 */
	public void setSectorCoords(float x, float y, float z)
	{
		sectorCoords = new Vector3f(x, y, z);
		transform().position(
				new Vector3f(getSectorX() * Sector.CHUNK_DIMENSIONS, 
						getSectorY() * Sector.CHUNK_DIMENSIONS, 
						getSectorZ() * Sector.CHUNK_DIMENSIONS));
	}
	
	/**
	 * Gets the array of blocks as an array of shorts that have each block's ID
	 * @return The array of blocks as an array of shorts that have each block's ID
	 */
	public short[][][] getBlocks() { return blocks; }
	
	/**
	 * The width of blocks the BlockStructure can hold
	 * @return The width of blocks the BlockStructure can hold
	 */
	public int getWidth() { return width; }
	
	/**
	 * The height of blocks the BlockStructure can hold
	 * @return The height of blocks the BlockStructure can hold
	 */
	public int getHeight() { return height; }
	
	/**
	 * The length of blocks the BlockStructure can hold
	 * @return The length of blocks the BlockStructure can hold
	 */
	public int getLength() { return length; }
	
	/**
	 * Gets every model the BlockStructure has and every position that model is at
	 * @return Every model the BlockStructure has and every position that model is at
	 */
	public Map<Model, Vector3fList> getModelsAndPositions()
	{
		if(modelsList.size() == 0)
			render();
		
		return modelsList;
	}
	
	/**
	 * Gets if the BlockStructure has been generated
	 * @return If the BlockStructure has been generated or not
	 */
	public boolean isGenerated() { return generated; }
	
	public boolean isRendered() { return rendered; }
	
	public boolean isRenderable() { return isRenderable; }
	
	/**
	 * Sets if the BlockStructure has been generated
	 * @param b Whether or not it has
	 */
	public void setGenerated(boolean b) 
	{
		this.generated = b;
	}
	
	/**
	 * The relative to the planet's center beginning corner of the planet (+x, +y, +z)
	 * @return The relative to the planet's center beginning corner of the planet (+x, +y, +z)
	 */
	public Vector3f getBeginningCorner()
	{
		return new Vector3f(beginningCornerX(), beginningCornerY(), beginningCornerZ());
	}
	
	/**
	 * The relative to the planet's center ending corner of the planet (-x, -y, -z)
	 * @return The relative to the planet's center ending corner of the planet (-x, -y, -z)
	 */
	public Vector3f getEndingCorner()
	{
		return new Vector3f(endingCornerX(), endingCornerY(), endingCornerZ());
	}
}

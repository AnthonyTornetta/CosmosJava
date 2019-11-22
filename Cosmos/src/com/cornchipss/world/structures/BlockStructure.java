package com.cornchipss.world.structures;

import java.util.HashMap;
import java.util.Map;

import org.joml.Matrix4f;
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
import com.cornchipss.world.sector.Sector;

public abstract class BlockStructure
{
	/**
	 * <p>Coordinate of the BlockStructure's center, relative to the sector's chunk coordinates</p>
	 * <p>So a BlockStructure coordinate of 0 would coordinate exactly to the center of the sector</p>
	 */
	private Vector3f sectorCoords;
	
	/**
	 * Every block that makes up the BlockStructure
	 */
	private short[][][] blocks;
	
	/**
	 * Dimensions of the BlockStructure
	 */
	private int width, height, length;
	
	private Transform transform;
	
	/**
	 * The sector the BlockStructure is a part of (wow)
	 */
	private Sector sector;
	
	/**
	 * A list of every model present on the BlockStructure and each position that model is at
	 */
	private Map<Model, Vector3fList> modelsList = new HashMap<>();
	private Map<Vector3i, Model> modelsCoords = new HashMap<>();
	
	private boolean generated = false;
	
	public BlockStructure(int width, int height, int length, float rx, float ry, float rz)
	{
		this.width = width;
		this.height = height;
		this.length = length;
		
		transform = new Transform();
		transform.setRotation(new Vector3f(rx, ry, rz));
	}
	
	/*
	 * https://open.gl/transformations
	 */
	
	public float getRotationX() { return transform.getRotationX(); }
	public void setRotationX(float rx) { transform.setRotationX(rx); }
	
	public float getRotationY() { return transform.getRotationY(); }
	public void setRotationY(float ry) { transform.setRotationY(ry); }
	
	public float getRotationZ() { return transform.getRotationZ(); }
	public void setRotationZ(float rz) { transform.setRotationZ(rz); }
	
	public Matrix4f getRotationXMatrix() { return transform.getRotationMatrixX(); }
	public Matrix4f getRotationYMatrix() { return transform.getRotationMatrixY(); }
	public Matrix4f getRotationZMatrix() { return transform.getRotationMatrixZ(); }
	
	/**
	 * Gets the absolute center position of the BlockStructure
	 * @return The absolute center position of the BlockStructure
	 */
	private float getAbsoluteX()
	{
		return getSector().getAbsoluteX() + getSectorX() * Sector.CHUNK_DIMENSIONS;
	}
	
	/**
	 * Gets the absolute center position of the BlockStructure
	 * @return The absolute center position of the BlockStructure
	 */
	private float getAbsoluteY()
	{
		return getSector().getAbsoluteY() + getSectorY() * Sector.CHUNK_DIMENSIONS;
	}
	
	/**
	 * Gets the absolute center position of the BlockStructure
	 * @return The absolute center position of the BlockStructure
	 */
	private float getAbsoluteZ()
	{
		return getSector().getAbsoluteZ() + getSectorZ() * Sector.CHUNK_DIMENSIONS;
	}
	
	/**
	 * Gets the absolute center position of the BlockStructure
	 * @return The absolute center position of the BlockStructure
	 */
	public Vector3f getAbsolutePosition()
	{
		return new Vector3f(getAbsoluteX(), getAbsoluteY(), getAbsoluteZ());
	}
	
	/**
	 * Gets the absolute center position of the BlockStructure
	 * @return The absolute center position of the BlockStructure
	 */
	public Vector3fc getUniverseCoords()
	{
		return new Vector3f(getAbsoluteX(), getAbsoluteY(), getAbsoluteZ());
	}
	
	/**
	 * <p>Sets the sector that the BlockStructure is a part of</p>
	 * <p>Make sure to update variables such as the BlockStructure's position in the sector!</p>
	 * @param sector The sector to set it to
	 */
	public void setSector(Sector sector)
	{
		this.sector = sector;
	}
	
	public Sector getSector() { return sector; }
	
	/**
	 * <p>Removes a block's model at a given coordinate from the list of models to render</p>
	 * <p>Remove a model before adding a model in the same spot!</p>
	 * <p>This is safe to call even if there was no model there before</p>
	 * @param x The x coordinate of the block to remove
	 * @param y The y coordinate of the block to remove
	 * @param z The z coordinate of the block to remove
	 * @return True if a model was removed, false if not
	 */
	public boolean removeModel(int x, int y, int z, Block oldBlock)
	{
		if(oldBlock == null)
			return false;
		
		Model model = oldBlock.getModel();
		
		if(model == null)
			return false;
		
		Vector3fList positions = modelsList.get(model);
		
		if(positions == null)
			return false;
		
		modelsCoords.remove(new Vector3i(x, y, z));
		
		return positions.removeVector(new Vector3f(x, y, z));
	}
	
	/**
	 * <p>Adds/Replaces a model to the list of models at a given coordinate, and makes sure there is no model already there.</p>
	 * <p>This automatically calls {@link BlockStructure#removeModel(int, int, int)} before trying to add a new one</p>
	 * @param pos The coordinates of the block to add/replace
	 * @param oldBlock The block that used to be here if you're changing it
	 */
	public boolean updateModel(Vector3ic pos, Block oldBlock)
	{
		return updateModel(pos.x(), pos.y(), pos.z(), oldBlock, true);
	}
	
	/**
	 * <p>Adds/Replaces a model to the list of models at a given coordinate, and makes sure there is no model already there.</p>
	 * <p>This automatically calls {@link BlockStructure#removeModel(int, int, int)} before trying to add a new one</p>
	 * @param x The x coordinate of the block to add/replace
	 * @param y The y coordinate of the block to add/replace
	 * @param z The z coordinate of the block to add/replace
	 * @param oldBlock The block that used to be here if you're changing it
	 */
	public boolean updateModel(int x, int y, int z, Block oldBlock)
	{
		return updateModel(x, y, z, oldBlock, true);
	}
	
	/**
	 * <p>Adds/Replaces a model to the list of models at a given coordinate, and makes sure there is no model already there.</p>
	 * @param x The x coordinate of the block to add/replace
	 * @param y The y coordinate of the block to add/replace
	 * @param z The z coordinate of the block to add/replace
	 * @param oldBlock The block that used to be here if you're changing it
	 * @param updateSurrounding Whether or not to update the surrounding blocks
	 * @return If the model was changed at all
	 */
	public boolean updateModel(int x, int y, int z, Block oldBlock, boolean updateSurrounding)
	{
		boolean nextToClear = false;
		
		big:
		for(int zz = -1; zz <= 1; zz++)
		{
			for(int yy = -1; yy <= 1; yy++)
			{
				for(int xx = -1; xx <= 1; xx++)
				{
					if(within(xx + x, yy + y, zz + z))
					{
						if(!getBlock(xx + x, yy + y, zz + z).isOpaque())
						{
							nextToClear = true;
							break big;
						}
					}
					else
					{
						nextToClear = true;
						break big;
					}
				}
			}
		}
		
		boolean changed = false;
		
		if(nextToClear)
		{
			changed = setModel(x, y, z, getBlock(x, y, z).getModel());
		}
		else
		{
			changed = setModel(x, y, z, null);
		}
		
		changed = changed || !Utils.equals(oldBlock, getBlock(x, y, z));
		
		if(updateSurrounding && changed)
		{
			for(int zz = -1; zz <= 1; zz++)
			{
				for(int yy = -1; yy <= 1; yy++)
				{
					for(int xx = -1; xx <= 1; xx++)
					{
						if(within(xx + x, yy + y, zz + z))
						{
							updateModel(new Vector3i(x + xx, y + yy, z + zz), getBlock(xx + x, yy + y, zz + z));
						}
					}
				}
			}
		}
		
		return changed;
	}
	
	public boolean setModel(int x, int y, int z, Model newModel)
	{
		Vector3i position = new Vector3i(x, y, z);
		
		Model oldModel = getModel(position);

		if(Utils.equals(newModel, oldModel))
			return false;
		
		if(newModel != null && !modelsList.containsKey(newModel))
		{
			modelsList.put(newModel, new Vector3fList());
		}
		modelsCoords.put(position, newModel);
		
		return setModel(x, y, z, newModel, modelsList.get(oldModel));
	}
	
	private Model getModel(Vector3i v)
	{
		return modelsCoords.get(v);
	}
	
	public boolean setModel(int x, int y, int z, Model m, Vector3fList oldList)
	{
		Vector3i pos = new Vector3i(x, y, z);
		Vector3f posf = new Vector3f(x, y, z);
		
		if(oldList != null)
		{
			modelsCoords.remove(pos);
			
			boolean removed = oldList.removeVector(posf);
			
			if(m == null)
			{
				return removed;
			}
		}
		
		if(m != null)
		{
			modelsCoords.put(pos, m);

			return modelsList.get(m).addVector(posf);//setModel(x, y, z, m);
		}
		
		return false;
	}
	
	/**
	 * Saves memory by not creating an array for every BlockStructure, even if the actual blocks haven't been generated/set yet.
	 * This is called whenever the {@link BlockStructure#setBlock(int, int, int, boolean, short)} function is called for the first time.
	 */
	private void initBlocks()
	{
		generated = false;
		blocks = new short[length][height][width];
	}
	
	private boolean within(int x, int y, int z)
	{
		return x >= getBeginningCornerX() && x < getEndingCornerX() &&
				y >= getBeginningCornerY() && y < getEndingCornerY() &&
				z >= getBeginningCornerZ() && z < getEndingCornerZ();
	}
	
	/**
	 * <p>Updates every model in the BlockStructure, so call sparingly.</p>
	 */
	public void render()
	{
		for(int z = getBeginningCornerZ(); z < getEndingCornerX(); z++)
		{
			for(int y = getBeginningCornerY(); y < getEndingCornerY(); y++)
			{
				for(int x = getBeginningCornerX(); x < getEndingCornerX(); x++)
				{
					updateModel(x, y, z, null, false);
				}
			}
		}
		
		for(Model m : modelsList.keySet())
			modelsList.get(m).shrink(); // Saves memory
	}
	
	/**
	 * Gets a block at the relative coordinates of the BlockStructure's center
	 * @param x The x coordinate relative to the BlockStructure's center
	 * @param y The y coordinate relative to the BlockStructure's center
	 * @param z The z coordinate relative to the BlockStructure's center
	 * @return The block at a given coordinate relative the BlockStructure's center
	 */
	public Block getBlock(int x, int y, int z)
	{
		if(blocks == null)
			return null;
		
		short id = blocks[z - getBeginningCornerZ()][y - getBeginningCornerY()][x - getBeginningCornerX()];
		
		return Blocks.getBlock(id);
	}
	
	/**
	 * Gets a block at the relative coordinates of the BlockStructure's center
	 * @param c The coordinates relative to the BlockStructure's center
	 * @return The block at a given coordinate relative the BlockStructure's center
	 */
	public Block getBlock(Vector3i c)
	{
		return getBlock(c.x, c.y, c.z);
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
		Vector3f actualBeginning = new Vector3f(getBeginningCornerX(), getBeginningCornerY(), getBeginningCornerZ());
		Vector3f actualEnd = new Vector3f(getEndingCornerX(), getEndingCornerY(), getEndingCornerZ());
		
//		Vector3f actualBeginning = Maths.getPositionActual(new Vector3f(getBeginningCornerX(), getBeginningCornerY(), getBeginningCornerZ()), rotationX, rotationY, rotationZ);
//		Vector3f actualEnd = Maths.getPositionActual(new Vector3f(getEndingCornerX(), getEndingCornerY(), getEndingCornerZ()), rotationX, rotationY, rotationZ);
//		Utils.println(actualBeginning);
//		Utils.println(actualEnd);
		

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
	 * @param setModel Whether or not the model should be set - this should only be called if you are calling a render() later. (When in doubt, set to true)
	 * @param b The block to set it to
	 */
	public void setBlock(int x, int y, int z, boolean setModel, Block b)
	{
		setBlock(x, y, z, setModel, b.getId());
	}
	
	/**
	 * Sets a block at the given coordinate, relative to the BlockStructure and updates the BlockStructure's model
	 * @param x The x coordinate, relative to the BlockStructure's center
	 * @param y The y coordinate, relative to the BlockStructure's center
	 * @param z The z coordinate, relative to the BlockStructure's center
	 * @param id The block's id to set it to
	 */
	public void setBlock(int x, int y, int z, short id)
	{
		setBlock(x, y, z, true, id);
	}
	
	/**
	 * Sets a block at the given coordinate, relative to the BlockStructure
	 * @param x The x coordinate, relative to the BlockStructure's center
	 * @param y The y coordinate, relative to the BlockStructure's center
	 * @param z The z coordinate, relative to the BlockStructure's center
	 * @param setModel Whether or not the model should be set - this should only be called if you are calling a render() later. (When in doubt, set to true)
	 * @param b The block to set it to
	 */
	public void setBlock(int x, int y, int z, boolean setModel, short id)
	{
		if(blocks == null)
			initBlocks();
		
		int zz = z - getBeginningCornerZ();
		int yy = y - getBeginningCornerY();
		int xx = x - getBeginningCornerX();
		
		if(id != getBlock(x, y, z).getId())
		{
			Block oldBlock = getBlock(x, y, z);
			blocks[zz][yy][xx] = id;			
			if(setModel)
			{
				updateModel(x, y, z, oldBlock);
			}
		}
	}
	
	/**
	 * Gets the corner of the BlockStructure in the negative x direction
	 * @return The corner of the BlockStructure in the negative x direction
	 */
	public int getBeginningCornerX()
	{
		return -getWidth() / 2;
	}
	
	/**
	 * Gets the corner of the BlockStructure in the negative y direction
	 * @return The corner of the BlockStructure in the negative y direction
	 */
	public int getBeginningCornerY()
	{
		return -getHeight() / 2;
	}
	
	/**
	 * Gets the corner of the BlockStructure in the negative z direction
	 * @return The corner of the BlockStructure in the negative z direction
	 */
	public int getBeginningCornerZ()
	{
		return -getLength() / 2;
	}
	
	/**
	 * Gets the corner of the BlockStructure in the positive x direction
	 * @return The corner of the BlockStructure in the positive x direction
	 */
	public int getEndingCornerX()
	{
		return getWidth() / 2;
	}
	
	/**
	 * Gets the corner of the BlockStructure in the positive y direction
	 * @return The corner of the BlockStructure in the positive y direction
	 */
	public int getEndingCornerY()
	{
		return getHeight() / 2;
	}
	
	/**
	 * Gets the corner of the BlockStructure in the positive z direction
	 * @return The corner of the BlockStructure in the positive z direction
	 */
	public int getEndingCornerZ()
	{
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
		transform.setPosition(getAbsolutePosition());
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

	/**
	 * Sets if the BlockStructure has been generated
	 * @param b Whether or not it has
	 */
	public void setGenerated(boolean b) 
	{ 
		this.generated = b;
	}
	
	/**
	 * Rotates the BlockStructure
	 * @param rx The change in x rotation
	 * @param ry The change in y rotation
	 * @param rz The change in z rotation
	 */
	public void rotate(float rx, float ry, float rz)
	{
		transform.rotate(rx, ry, rz);
	}
}

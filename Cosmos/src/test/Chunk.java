package test;

import org.joml.Matrix4f;

import com.cornchipss.utils.Utils;

import test.blocks.Block;

public class Chunk
{
	private BulkModel model;
	
	private boolean rendered;
	
	public static final int WIDTH = 16, HEIGHT = 16, LENGTH = 16;
	
	private Block[][][] blocks;
	
	private Matrix4f transformMatrix;
	
	public Chunk()
	{
		rendered = false;
		blocks = new Block[LENGTH][HEIGHT][WIDTH];
		model = new BulkModel(blocks);
	}
	
	/**
	 * <p>Sets the block at the given coordinates relative to this chunk.</p>
	 * <p>If {@link Chunk#render()} has been called previously, this will also call it.</p>
	 * @param x The X coordinate relative to this chunk
	 * @param y The Y coordinate relative to this chunk
	 * @param z The Z coordinate relative to this chunk
	 * @param block The block to set it to
	 */
	public void block(int x, int y, int z, Block block)
	{
		if(!Utils.equals(blocks[z][y][x], block))
		{
			blocks[z][y][x] = block;
			
			if(rendered)
				model.render();
		}
	}
	
	/**
	 * <p>Converts the blocks into a drawable mesh accessible through {@link Chunk#mesh()}</p>
	 * <p>Once this method is called, all changes to this chunk's blocks will call this method.</p>
	 */
	public void render()
	{
		rendered = true;
		
		model.render();
	}
	
	/**
	 * Gets the block relative to this chunk's position
	 * @param x The X coordinate relative to the chunk's position
	 * @param y The Y coordinate relative to the chunk's position
	 * @param z The Z coordinate relative to the chunk's position
	 * @return The block at this position - null if there is no block
	 */
	public Block block(int x, int y, int z)
	{
		return blocks[z][y][x];
	}
	
	/**
	 * The mesh of all the blocks - null if {@link Chunk#render()} has not been called.
	 * @return The mesh of all the blocks - null if {@link Chunk#render()} has not been called.
	 */
	public Mesh mesh()
	{
		return model.mesh();
	}
	
	public int width() { return WIDTH; }
	public int height() { return HEIGHT; }
	public int length() { return LENGTH; }

	public void transformMatrix(Matrix4f m)
	{
		transformMatrix = m;
	}
	
	public Matrix4f transformMatrix()
	{
		return transformMatrix;
	}
}

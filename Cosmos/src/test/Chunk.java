package test;

import java.util.LinkedHashSet;
import java.util.Set;

import org.joml.Matrix4f;
import org.joml.Vector3i;
import org.joml.Vector3ic;

import com.bulletphysics.collision.shapes.CollisionShape;
import com.bulletphysics.collision.shapes.CompoundShape;
import com.bulletphysics.collision.shapes.CompoundShapeChild;
import com.bulletphysics.linearmath.Transform;
import com.cornchipss.utils.Utils;

import test.blocks.Block;
import test.blocks.LitBlock;

public class Chunk
{
	private BulkModel model;
	
	private boolean rendered;
	
	public static final int WIDTH = 16, HEIGHT = 16, LENGTH = 16;
	
	private Block[][][] blocks;
	
	private CompoundShape shape;
	
	/**
	 * Offset relative to block structure's 0,0,0
	 */
	private Vector3ic offset;
	
	/**
	 * block structure it's a part of
	 */
	private Structure structure;
	
	public Chunk(int offX, int offY, int offZ, Structure s)
	{
		this.offset = new Vector3i(offX, offY, offZ);
		
		this.structure = s;
		
		rendered = false;
		blocks = new Block[LENGTH][HEIGHT][WIDTH];
		model = new BulkModel(blocks);
		
		shape = new CompoundShape();
	}
	
	/**
	 * Neighbors
	 */
	private Chunk left, right, top, bottom, front, back;
	
	public void leftNeighbor(Chunk c)
	{
		left = c;
	}
	public void rightNeighbor(Chunk c)
	{
		right = c;
	}
	public void topNeighbor(Chunk c)
	{
		top = c;
	}
	public void bottomNeighbor(Chunk c)
	{
		bottom = c;
	}
	public void frontNeighbor(Chunk c)
	{
		front = c;
	}
	public void backNeighbor(Chunk c)
	{
		back = c;
	}
	
	public Chunk leftNeighbor()
	{
		return left;
	}
	public Chunk rightNeighbor()
	{
		return right;
	}
	public Chunk topNeighbor()
	{
		return top;
	}
	public Chunk bottomNeighbor()
	{
		return bottom;
	}
	public Chunk frontNeighbor()
	{
		return front;
	}
	public Chunk backNeighbor()
	{
		return back;
	}
	
	private Matrix4f transformMatrix;

	public void block(int x, int y, int z, Block block)
	{
		block(x, y, z, block, true);
	}
	
	/**
	 * <p>Sets the block at the given coordinates relative to this chunk.</p>
	 * <p>If {@link Chunk#render()} has been called previously, this will also call it.</p>
	 * @param x The X coordinate relative to this chunk
	 * @param y The Y coordinate relative to this chunk
	 * @param z The Z coordinate relative to this chunk
	 * @param block The block to set it to
	 * @param shouldRender If true, the chunk will update the lightmap + re-render the mesh. If the chunk has not been rendered yet - this will be false no matter what is passed
	 */
	public void block(int x, int y, int z, Block block, boolean shouldRender)
	{
		shouldRender = rendered && shouldRender;
		
		if(!Utils.equals(blocks[z][y][x], block))
		{
			blocks[z][y][x] = block;
			
			if(block != null)
				structure.lightMap().addBlocking(x + offset.x(), y + offset.y(), z + offset.z());
			else
				structure.lightMap().removeBlocking(x + offset.x(), y + offset.y(), z + offset.z());
			
			if(block instanceof LitBlock)
			{
				// remove it if there is already one
				structure.lightMap().removeLightSource(x + offset.x(), y + offset.y(), z + offset.z());
				
				structure.lightMap().lightSource(x + offset.x(), y + offset.y(), z + offset.z(), 
							((LitBlock) block).lightSource());
				
				if(shouldRender)
				{
					structure.calculateLights(true);
				}
			}
			else if(structure.lightMap().hasLightSource(x + offset.x(), y + offset.y(), z + offset.z()))
			{
				structure.lightMap().removeLightSource(x + offset.x(), y + offset.y(), z + offset.z());
				
				if(shouldRender)
					structure.calculateLights(true);
			}
			else if(shouldRender)
			{
				structure.calculateLights(true);
			}
			
			if(shouldRender) // only if the chunk has been rendered at least 1 time before
				render(); // update the chunk's model for the new block
			
			// Make sure if this block is neighboring another chunk, that chunk updates aswell
			if(x == 0 && left != null && shouldRender)
				left.render();
			if(x + 1 == Chunk.WIDTH && right != null && shouldRender)
				right.render();
			
			if(y == 0 && bottom != null && shouldRender)
				bottom.render();
			if(y + 1 == Chunk.HEIGHT && top != null && shouldRender)
				top.render();
			
			if(z == 0 && back != null && shouldRender)
				back.render();
			if(z + 1 == Chunk.LENGTH && front != null && shouldRender)
				front.render();
		}
	}
	
	/**
	 * <p>Converts the blocks into a drawable mesh accessible through {@link Chunk#mesh()}</p>
	 * <p>Once this method is called, all changes to this chunk's blocks will call this method.</p>
	 */
	public void render()
	{
		rendered = true;
		
		calculatePhysicsShape();
		
		model.render(
				left != null ? left.model : null, 
				right != null ? right.model : null, 
				top != null ? top.model : null, 
				bottom != null ? bottom.model : null, 
				front != null ? front.model : null, 
				back != null ? back.model : null,
						offset.x(), offset.y(), offset.z(), structure.lightMap());
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
	
	public BulkModel model()
	{
		return model;
	}
	
	public Vector3ic offset()
	{
		return offset;
	}
	
	public Structure structure()
	{
		return structure;
	}
	
	private void calculatePhysicsShape()
	{
		Set<CollisionShape> shapesToRemove = new LinkedHashSet<>();
		for(CompoundShapeChild s : shape.getChildList())
		{
			shapesToRemove.add(s.childShape);
		}
		
		for(CollisionShape s : shapesToRemove)
		{
			shape.removeChildShape(s);
		}
		
		Transform here = new Transform();

		for(int z = 0; z < LENGTH; z++)
		{
			for(int y = 0; y < HEIGHT; y++)
			{
				for(int x = 0; x < WIDTH; x++)
				{
					if(block(x, y, z) != null)
					{
						CollisionShape blockShape = block(x, y, z).collisionShape();
						
						here.setIdentity();
						here.origin.set(x + 0.5f, y + 0.5f, z + 0.5f);
						
						shape.addChildShape(here, blockShape);
					}
				}
			}
		}
		
		structure.updatePhysics();
	}
	
	public CollisionShape physicsShape()
	{
		return shape;
	}
}

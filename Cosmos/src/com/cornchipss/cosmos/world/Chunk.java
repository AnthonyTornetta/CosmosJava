package com.cornchipss.cosmos.world;

import java.awt.Color;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector3fc;
import org.joml.Vector3i;
import org.joml.Vector3ic;

import com.cornchipss.cosmos.blocks.Block;
import com.cornchipss.cosmos.blocks.Blocks;
import com.cornchipss.cosmos.blocks.LitBlock;
import com.cornchipss.cosmos.physx.collision.CollisionInfo;
import com.cornchipss.cosmos.physx.collision.obb.IOBBCollisionChecker;
import com.cornchipss.cosmos.physx.collision.obb.OBBCollider;
import com.cornchipss.cosmos.rendering.BulkModel;
import com.cornchipss.cosmos.rendering.MaterialMesh;
import com.cornchipss.cosmos.rendering.debug.DebugRenderer;
import com.cornchipss.cosmos.rendering.debug.DebugRenderer.DrawMode;
import com.cornchipss.cosmos.structures.Structure;
import com.cornchipss.cosmos.utils.Maths;
import com.cornchipss.cosmos.utils.Utils;
import com.cornchipss.cosmos.utils.io.IWritable;

public class Chunk implements IWritable
{
	private BulkModel model;

	private boolean needsRendered;

	private boolean empty = true;

	public boolean lightmapNeedsUpdating()
	{
		return false;
	}

	public boolean needsRendered()
	{
		return needsRendered;
	}

	public void needsRendered(boolean b)
	{
		needsRendered = b;
	}

	/**
	 * Dimensions of a Chunk - must be even
	 */
	public static final int WIDTH = 16, HEIGHT = 16, LENGTH = 16;
	public static final int HALF_WIDTH = WIDTH / 2, HALF_HEIGHT = HEIGHT / 2,
		HALF_LENGTH = LENGTH / 2;

	public static final Vector3fc DIMENSIONS = new Vector3f(WIDTH, HEIGHT,
		LENGTH);
	public static final Vector3fc HALF_DIMENSIONS = new Vector3f(DIMENSIONS)
		.div(2);

	private static final float EPSILON = 1E-4f;

	private Block[][][] blocks;

	/**
	 * Offset in the structure's lightmap
	 */
	private Vector3ic lightingOffset;

	/**
	 * The position of the chunk relative to the structure's 0, 0, 0 without
	 * accounting for its orientation
	 */
	private Vector3fc relativePos;

	/**
	 * Where the chunk is stored in the structure
	 */
	private Vector3ic localPosition;

	/**
	 * block structure it's a part of
	 */
	private Structure structure;

	public Chunk(int x, int y, int z, float relX, float relY, float relZ,
		int offX, int offY, int offZ, Structure s)
	{
		this.lightingOffset = new Vector3i(offX, offY, offZ);
		this.localPosition = new Vector3i(x, y, z);
		this.relativePos = new Vector3f(relX, relY, relZ);

		this.structure = s;

		needsRendered = true;

		blocks = new Block[LENGTH][HEIGHT][WIDTH];
		model = new BulkModel(blocks);
	}

	@Override
	public void write(DataOutputStream writer) throws IOException
	{
		short currentId = -1;
		int amount = 0;

		for (int z = 0; z < blocks.length; z++)
		{
			for (int y = 0; y < blocks[z].length; y++)
			{
				for (int x = 0; x < blocks[z][y].length; x++)
				{
					short id = blocks[z][y][x] != null
						? blocks[z][y][x].numericId()
						: 0;

					if (currentId == -1)
					{
						currentId = id;
						amount = 1;
					}
					else if (currentId != id)
					{
						writer.writeShort(currentId);
						writer.writeInt(amount);

						currentId = id;
						amount = 1;
					}
					else
					{
						amount++;
					}
				}
			}
		}

		// This would only ever be true if the chunk was 0x0x0.
		if (currentId != -1)
		{
			writer.writeShort(currentId);
			writer.writeInt(amount);
		}
	}

	@Override
	public void read(DataInputStream reader) throws IOException
	{
		int position = 0;

		while (position < WIDTH * HEIGHT * LENGTH)
		{
			short id = reader.readShort();
			int amount = reader.readInt();

			for (int i = 0; i < amount; i++)
			{
				int z = position / HEIGHT / WIDTH;
				int y = (position - z * HEIGHT * WIDTH) / HEIGHT;
				int x = position - z * HEIGHT * WIDTH - y * HEIGHT;

				if (id != 0)
					block(x, y, z, Blocks.fromNumericId(id));

				position++;
			}
		}
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

	public boolean within(int x, int y, int z)
	{
		return x >= 0 && x < WIDTH && y >= 0 && y < HEIGHT && z >= 0
			&& z < LENGTH;
	}

	/**
	 * <p>
	 * Sets the block at the given coordinates relative to this chunk.
	 * </p>
	 * <p>
	 * Sets the needs rendered flag to true
	 * </p>
	 * 
	 * @param x            The X coordinate relative to this chunk
	 * @param y            The Y coordinate relative to this chunk
	 * @param z            The Z coordinate relative to this chunk
	 * @param block        The block to set it to
	 * @param shouldRender If true, the chunk will update the lightmap +
	 *                     re-render the mesh. If the chunk has not been
	 *                     rendered yet - this will be false no matter what is
	 *                     passed
	 */
	public void block(int x, int y, int z, Block block)
	{
		if (!within(x, y, z))
			throw new IllegalArgumentException(
				"Bad x,y,z: " + x + ", " + y + ", " + z);

		boolean shouldRender = false;// rendered && shouldRender &&
										// NettySide.side() == NettySide.CLIENT;

		if (block != null)
			empty = false;

		if (!Utils.equals(blocks[z][y][x], block))
		{
			blocks[z][y][x] = block;

			if (block != null)
				structure.lightMap().setBlocking(x + lightingOffset.x(),
					y + lightingOffset.y(), z + lightingOffset.z());
			else
				structure.lightMap().removeBlocking(x + lightingOffset.x(),
					y + lightingOffset.y(), z + lightingOffset.z());

			if (block instanceof LitBlock)
			{
				// remove it if there is already one
				structure.lightMap().removeLight(x + lightingOffset.x(),
					y + lightingOffset.y(), z + lightingOffset.z());

				structure.lightMap().addLight(((LitBlock) block).lightSource(),
					x + lightingOffset.x(), y + lightingOffset.y(),
					z + lightingOffset.z());
			}
			else if (structure.lightMap().hasLightSource(x + lightingOffset.x(),
				y + lightingOffset.y(), z + lightingOffset.z()))
			{
				structure.lightMap().removeLight(x + lightingOffset.x(),
					y + lightingOffset.y(), z + lightingOffset.z());
			}

			needsRendered(true);

			// Make sure if this block is neighboring another chunk, that chunk
			// updates
			// aswell
			if (x == 0 && left != null && shouldRender)
				left.needsRendered(true);
			if (x + 1 == Chunk.WIDTH && right != null && shouldRender)
				right.needsRendered(true);

			if (y == 0 && bottom != null && shouldRender)
				bottom.needsRendered(true);
			if (y + 1 == Chunk.HEIGHT && top != null && shouldRender)
				top.needsRendered(true);

			if (z == 0 && back != null && shouldRender)
				back.needsRendered(true);
			if (z + 1 == Chunk.LENGTH && front != null && shouldRender)
				front.needsRendered(true);
		}
	}

	/**
	 * <p>
	 * Converts the blocks into a drawable mesh accessible through
	 * {@link Chunk#mesh()}
	 * </p>
	 * <p>
	 * Once this method is called, all changes to this chunk's blocks will call
	 * this method.
	 * </p>
	 */
	public void render()
	{
		needsRendered = false;

		model.render(left != null ? left.model : null,
			right != null ? right.model : null, top != null ? top.model : null,
			bottom != null ? bottom.model : null,
			front != null ? front.model : null,
			back != null ? back.model : null, lightingOffset.x(),
			lightingOffset.y(), lightingOffset.z(), structure.lightMap());
	}

	/**
	 * Gets the block relative to this chunk's position
	 * 
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
	 * If the chunk has a block at a given point
	 * 
	 * @param x The X coordinate relative to the chunk's position
	 * @param y The Y coordinate relative to the chunk's position
	 * @param z The Z coordinate relative to the chunk's position
	 * @return True if there is a block, false if not
	 */
	public boolean hasBlock(Vector3ic v)
	{
		return hasBlock(v.x(), v.y(), v.z());
	}

	/**
	 * If the chunk has a block at a given point
	 * 
	 * @param x The X coordinate relative to the chunk's position
	 * @param y The Y coordinate relative to the chunk's position
	 * @param z The Z coordinate relative to the chunk's position
	 * @return True if there is a block, false if not
	 */
	public boolean hasBlock(int x, int y, int z)
	{
		return within(x, y, z) && block(x, y, z) != null;
	}

	/**
	 * The mesh of all the blocks - null if {@link Chunk#render()} has not been
	 * called.
	 * 
	 * @return The mesh of all the blocks - null if {@link Chunk#render()} has
	 *         not been called.
	 */
	public List<MaterialMesh> meshes()
	{
		return model.materialMeshes();
	}

	public int width()
	{
		return WIDTH;
	}

	public int height()
	{
		return HEIGHT;
	}

	public int length()
	{
		return LENGTH;
	}

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

	public Structure structure()
	{
		return structure;
	}

	/**
	 * The position of the chunk relative to the structure's 0, 0, 0 without
	 * accounting for its orientation
	 */
	public Vector3fc relativePosition()
	{
		return relativePos;
	}

	/**
	 * Where the chunk is stored in the structure
	 */
	public Vector3ic localPosition()
	{
		return localPosition;
	}

	private boolean testLineIntersection(Vector3fc lineStart,
		Vector3fc lineDelta, Vector3ic dir, CollisionInfo info,
		IOBBCollisionChecker checker, int x, int y, int z, Set<Vector3i> done,
		Vector3i temp)
	{
		boolean res = false;

		float dist = Maths.sqrt(lineDelta.dot(lineDelta));

		Set<Vector3i> todo = new HashSet<>();
		Set<Vector3i> nextTodo = new HashSet<>();

		Vector3i start = new Vector3i(x, y, z);
		todo.add(start);
		
		Vector3i here = new Vector3i();
		
		while (todo.size() != 0)
		{
			for (Vector3i v : todo)
			{
				structure().chunkCoordsToBlockCoords(this, v, here);
				OBBCollider c = structure().wholeOBBForBlock(here);
				
				if(c == null)
					continue;
				
//				DebugRenderer.instance().drawOBB(c, Color.PINK, DrawMode.LINES);
				
				Vector3f wc = structure().chunkCoordsToWorldCoords(this, v,
					new Vector3f());

				// 1 doesnt work so I use 2
				if (wc.distance(lineStart) > dist + 2)
				{
					continue;
				}

				if (checker.testLineOBB(lineStart, lineDelta, c, null))
				{
					temp.set(v.x + 1, v.y, v.z);
					if (structure().withinBlocks(temp.x, temp.y, temp.z)
						&& !todo.contains(temp) && !done.contains(temp))
						nextTodo.add(new Vector3i(temp));
					temp.set(v.x - 1, v.y, v.z);
					if (structure().withinBlocks(temp.x, temp.y, temp.z)
						&& !todo.contains(temp) && !done.contains(temp))
						nextTodo.add(new Vector3i(temp));
					temp.set(v.x, v.y + 1, v.z);
					if (structure().withinBlocks(temp.x, temp.y, temp.z)
						&& !todo.contains(temp) && !done.contains(temp))
						nextTodo.add(new Vector3i(temp));
					temp.set(v.x, v.y - 1, v.z);
					if (structure().withinBlocks(temp.x, temp.y, temp.z)
						&& !todo.contains(temp) && !done.contains(temp))
						nextTodo.add(new Vector3i(temp));
					temp.set(v.x, v.y, v.z + 1);
					if (structure().withinBlocks(temp.x, temp.y, temp.z)
						&& !todo.contains(temp) && !done.contains(temp))
						nextTodo.add(new Vector3i(temp));
					temp.set(v.x, v.y, v.z - 1);
					if (structure().withinBlocks(temp.x, temp.y, temp.z)
						&& !todo.contains(temp) && !done.contains(temp))
						nextTodo.add(new Vector3i(temp));

					c = structure().obbForBlock(here);

					if (c != null && checker.testLineOBB(lineStart, lineDelta, c, info))
					{
						DebugRenderer.instance().drawOBB(c, Color.GREEN, DrawMode.FILL);
						res = true;
					}
				}
			}

			if (!res)
			{
				done.addAll(todo);
				todo = nextTodo;
				nextTodo = new HashSet<>();
			}
			else
				return res;
		}

		return res;
	}

	public boolean testLineIntersection(Vector3fc lineStart,
		Vector3fc lineDelta, CollisionInfo info, IOBBCollisionChecker checker)
	{
		OBBCollider chunkCollider = structure.obbForChunk(this);

		CollisionInfo tempInfo = new CollisionInfo();

		// Checks if the line is within the chunk
		if (!checker.testLineOBB(lineStart, lineDelta, chunkCollider, tempInfo))
		{
			tempInfo.collisionPoint.set(lineStart);
			DebugRenderer.instance().drawPoint(lineStart, Color.blue);
			DebugRenderer.instance().drawPoint(lineStart.add(lineDelta, new Vector3f()), Color.orange);
		}

		Set<Vector3i> done = new HashSet<>();

		Vector3i direction = Maths.signumi(lineDelta, new Vector3i());

		Vector3i here = structure.worldCoordsToChunkCoords(
			tempInfo.collisionPoint.add(EPSILON * direction.x,
				EPSILON * direction.y, EPSILON * direction.z));

		return testLineIntersection(lineStart, lineDelta, direction, info,
			checker, here.x, here.y, here.z, done, new Vector3i());
	}

	public boolean empty()
	{
		return empty;
	}
}

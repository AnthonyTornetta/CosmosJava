package com.cornchipss.cosmos.structures;

import java.awt.Color;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.joml.Vector3f;
import org.joml.Vector3fc;
import org.joml.Vector3i;
import org.joml.Vector3ic;

import com.cornchipss.cosmos.blocks.Block;
import com.cornchipss.cosmos.blocks.BlockFace;
import com.cornchipss.cosmos.blocks.StructureBlock;
import com.cornchipss.cosmos.blocks.data.BlockData;
import com.cornchipss.cosmos.client.world.entities.ClientPlayer;
import com.cornchipss.cosmos.lights.LightMap;
import com.cornchipss.cosmos.memory.MemoryPool;
import com.cornchipss.cosmos.physx.PhysicalObject;
import com.cornchipss.cosmos.physx.RigidBody;
import com.cornchipss.cosmos.physx.Transform;
import com.cornchipss.cosmos.physx.collision.CollisionInfo;
import com.cornchipss.cosmos.physx.collision.obb.IOBBCollisionChecker;
import com.cornchipss.cosmos.physx.collision.obb.OBBCollider;
import com.cornchipss.cosmos.rendering.IRenderable;
import com.cornchipss.cosmos.rendering.MaterialMesh;
import com.cornchipss.cosmos.rendering.debug.DebugRenderer;
import com.cornchipss.cosmos.rendering.debug.DebugRenderer.DrawMode;
import com.cornchipss.cosmos.structures.types.IEnergyHolder;
import com.cornchipss.cosmos.systems.BlockSystem;
import com.cornchipss.cosmos.systems.BlockSystemManager;
import com.cornchipss.cosmos.utils.IUpdatable;
import com.cornchipss.cosmos.utils.Logger;
import com.cornchipss.cosmos.utils.Maths;
import com.cornchipss.cosmos.utils.Utils;
import com.cornchipss.cosmos.utils.io.IWritable;
import com.cornchipss.cosmos.world.Chunk;
import com.cornchipss.cosmos.world.World;

public abstract class Structure extends PhysicalObject
	implements IWritable, IEnergyHolder, IRenderable, IUpdatable
{
	private Chunk[] chunks;

	private boolean initialized = false;

	private int width, height, length;

	private int cWidth, cHeight, cLength;
	private LightMap lightMap;

	private int id;

	private float energy;
	private float maxEnergy;

	private BlockSystemManager blockSystemManager;

	private float totalMass = 0;

	private Vector3fc halfwidths;
	
	private Set<StructureBlock> changes = new HashSet<>();
	
	public Structure(World world, int id)
	{
		super(world);

		this.id = id;

		blockSystemManager = new BlockSystemManager();
	}

	public Structure(World world, int width, int height, int length, int id)
	{
		this(world, id);

		if (width <= 0 || height <= 0 || length <= 0)
			throw new IllegalArgumentException(
				"A Structure's width/height/length cannot be <= 0");

		this.width = width;
		this.height = height;
		this.length = length;

		cLength = (int) Math.ceil((float) length / Chunk.LENGTH);
		cHeight = (int) Math.ceil((float) height / Chunk.HEIGHT);
		cWidth = (int) Math.ceil((float) width / Chunk.WIDTH);

		lightMap = new LightMap(width + 2, height + 2, length + 2);

		chunks = new Chunk[cLength * cHeight * cWidth];
	}

	@Override
	public boolean update(float delta)
	{
		blockSystemManager.update(delta);

		return true;
	}

	public static final class RayRes
	{
		BlockFace face;
		float distance;
		StructureBlock block;

		private RayRes(StructureBlock b, float d, BlockFace f)
		{
			this.block = b;
			this.distance = d;
			this.face = f;
		}

		public StructureBlock block()
		{
			return block;
		}

		public BlockFace face()
		{
			return face;
		}

		public float distance()
		{
			return distance;
		}

		@Override
		public String toString()
		{
			return block.block() + " " + face + " "
				+ Utils.toEasyString(distance) + "m";
		}
	}

	public RayRes raycast(Vector3fc start, Vector3fc direction, float length,
		IOBBCollisionChecker obc)
	{
		return raycast(start, direction.mul(length, new Vector3f()), obc);
	}

	public RayRes raycast(Vector3fc start, Vector3fc delta,
		IOBBCollisionChecker obc)
	{
		Vector3i sc = worldCoordsToBlockCoords(start, new Vector3i());
		if (!withinBlocks(sc.x, sc.y, sc.z))
		{
			CollisionInfo info = new CollisionInfo();
			if (obc.testLineOBB(start, delta, OBB(), info))
			{
				Vector3f dd = info.collisionPoint.sub(start, new Vector3f());
				delta = delta.sub(dd, new Vector3f());
				start = info.collisionPoint;

				sc = worldCoordsToBlockCoords(start, new Vector3i());
			}
			else
				return null; // the line does not intersect the structure
		}

		final float totalDistSquared = delta.dot(delta);

		CollisionInfo info = new CollisionInfo();
		info.distanceSquared = totalDistSquared;
		RayRes rr = null;

		int signX = (int) Math.signum(delta.x());
		int signY = (int) Math.signum(delta.y());
		int signZ = (int) Math.signum(delta.z());

		Set<Vector3ic> done = new HashSet<>();
		Set<Vector3ic> todo = new HashSet<>();
		Set<Vector3ic> nextTodo = new HashSet<>();

		Vector3i tempVec = new Vector3i();

		todo.add(worldCoordsToBlockCoords(start, tempVec));

		Vector3f tempDistanceVec = new Vector3f();

		float ddddd = Maths.sqrt(delta.dot(delta));

		while (todo.size() != 0 && rr == null)
		{
			for (Vector3ic point : todo)
			{
				this.blockCoordsToWorldCoords(point, tempDistanceVec);

				if (start.distance(tempDistanceVec) + 2 > ddddd)
					continue;

				if (withinBlocks(point.x(), point.y(), point.z()))
				{
					OBBCollider obbBlock = wholeOBBForBlock(point.x(),
						point.y(), point.z());

					CollisionInfo temp = new CollisionInfo();

					if (obc.testLineOBB(start, delta, obbBlock, temp))
					{
						DebugRenderer.instance().drawOBB(obbBlock, Color.pink,
							DrawMode.LINES);

						if (hasBlock(point.x(), point.y(), point.z())
							&& temp.distanceSquared < info.distanceSquared
							&& temp.normal.dot(temp.normal) != 0)
						{
							info.set(temp);
							BlockFace face = BlockFace.fromNormal(body()
								.transform().orientation().applyInverseRotation(
									info.normal, new Vector3f()));

							rr = new RayRes(
								new StructureBlock(this, point.x(), point.y(),
									point.z()),
								Maths.sqrt(info.distanceSquared), face);
						}
						else if (rr == null)
						{
							Vector3i tempV = new Vector3i(point.x() + signX,
								point.y(), point.z());
							if (!done.contains(tempV) && !todo.contains(tempV))
								nextTodo.add(tempV);

							tempV = new Vector3i(point.x(), point.y() + signY,
								point.z());
							if (!done.contains(tempV) && !todo.contains(tempV))
								nextTodo.add(tempV);

							tempV = new Vector3i(point.x(), point.y(),
								point.z() + signZ);
							if (!done.contains(tempV) && !todo.contains(tempV))
								nextTodo.add(tempV);
						}
					}

				}
			}

			if (rr == null)
			{
				done.addAll(todo);
				todo = nextTodo;
				nextTodo = new HashSet<>();
			}
		}

		return rr;
	}

	public void energy(float f)
	{
		energy = f;
	}

	public void maxEnergy(float f)
	{
		maxEnergy = f;
	}

	@Override
	public float energy()
	{
		return energy;
	}

	@Override
	public float maxEnergy()
	{
		return maxEnergy;
	}

	@Override
	public boolean hasEnoughEnergyToUse(float amount)
	{
		return amount <= energy();
	}

	@Override
	public boolean useEnergy(float amount)
	{
		if (!hasEnoughEnergyToUse(amount))
			return false;

		energy -= amount;
		if (energy < 0)
			energy = 0;

		return true;
	}

	@Override
	public void addEnergy(float amount)
	{
		energy += amount;
		if (energy > maxEnergy())
			energy = maxEnergy();
	}

	/**
	 * <p>
	 * <b>Deprecated</b>: does not send data to server
	 * <p>
	 * <p>
	 * Removes blocks in a sphere originating from pos with the specified radius
	 * 
	 * @param radius The radius of blocks to remove
	 * @param pos    The sphere's origin
	 */
	@Deprecated
	public void explode(int radius, Vector3i pos)
	{
		Vector3f temp = new Vector3f();
		Vector3f tempPos = new Vector3f(pos.x, pos.y, pos.z);

		for (int dz = -radius; dz <= radius; dz++)
		{
			for (int dy = -radius; dy <= radius; dy++)
			{
				for (int dx = -radius; dx <= radius; dx--)
				{
					int xx = pos.x + dx, yy = pos.y + dy, zz = pos.z + dz;

					temp.x = xx;
					temp.y = yy;
					temp.z = zz;

					if (Maths.distSqrd(temp, tempPos) < radius * radius)
					{
						if (withinBlocks(xx, yy, zz))
						{
							block(xx, yy, zz, null);
						}
					}
				}
			}
		}
	}

	/**
	 * The system, null if it doesn't exist
	 * 
	 * @param id the system's ID
	 * @return The system, null if it doesn't exist
	 */
	public BlockSystem systemFromID(String id)
	{
		return blockSystemManager.systemFromID(id);
	}

	@Override
	public void addToWorld(Transform transform)
	{
		body(new RigidBody(transform));
		world().addObject(this);
	}

	public int chunksLength()
	{
		return cLength;
	}

	public int chunksHeight()
	{
		return cHeight;
	}

	public int chunksWidth()
	{
		return cWidth;
	}

	public Chunk[] chunks()
	{
		return chunks;
	}

	private int flatten(int x, int y, int z)
	{
		if (!withinChunks(x, y, z))
			throw new IndexOutOfBoundsException(
				x + "," + y + "," + z + " is out of bounds for " + cWidth + "x"
					+ cHeight + "x" + cLength + ".");
		return x + cWidth * (y + cHeight * z);
	}

	public void chunk(int x, int y, int z, Chunk c)
	{
		chunks[flatten(x, y, z)] = c;
	}

	public Chunk chunk(int x, int y, int z)
	{
		return chunks[flatten(x, y, z)];
	}

	private Chunk chunkFromBlockCoords(int x, int y, int z)
	{
		return chunk(x / Chunk.WIDTH, y / Chunk.HEIGHT, z / Chunk.LENGTH);
	}

	/**
	 * The center of the chunk relative to the world's center
	 * 
	 * @param c   The chunk
	 * @param out The output vector
	 * @return center of the chunk relative to the structure's center
	 */
	public Vector3f chunkWorldPosition(Chunk c, Vector3f out)
	{
		chunkRelativePosCentered(c, out);
		this.body().transform().orientation().applyRotation(out, out);
		return out.add(position());
	}

	/**
	 * The center of the chunk relative to the structure's center
	 * 
	 * @param c   The chunk
	 * @param out The output vector
	 * @return center of the chunk relative to the structure's center
	 */
	public Vector3f chunkRelativePosCentered(Chunk c, Vector3f out)
	{
		return out.set(c.relativePosition().x(), c.relativePosition().y(),
			c.relativePosition().z());
	}

	public void init()
	{
		initialized = true;
		
		halfwidths = new Vector3f(width() / 2.f, height() / 2.f, length() / 2.f);

		for (int z = 0; z < chunksLength(); z++)
		{
			for (int y = 0; y < chunksHeight(); y++)
			{
				for (int x = 0; x < chunksWidth(); x++)
				{
					int i = flatten(x, y, z);
					chunks[i] = new Chunk(x, y, z,
						(x - cWidth / 2) * Chunk.WIDTH
							+ ((1 - cWidth % 2) * Chunk.WIDTH / 2.f),
						(y - cHeight / 2) * Chunk.HEIGHT
							+ ((1 - cHeight % 2) * Chunk.HEIGHT / 2.f),
						(z - cLength / 2) * Chunk.LENGTH
							+ ((1 - cLength % 2) * Chunk.LENGTH / 2.f),
						x * Chunk.WIDTH + 1, y * Chunk.HEIGHT + 1,
						z * Chunk.LENGTH + 1, this);

					chunks[i]
						.transformMatrix(
							Maths.createTransformationMatrix(
								new Vector3f(x * Chunk.WIDTH, y * Chunk.HEIGHT,
									z * Chunk.LENGTH),
								Maths.blankQuaternion()));

				}
			}
		}

		for (int z = 0; z < chunksLength(); z++)
		{
			for (int y = 0; y < chunksHeight(); y++)
			{
				for (int x = 0; x < chunksWidth(); x++)
				{
					int i = flatten(x, y, z);

					chunks[i].leftNeighbor(
						withinChunks(x - 1, y, z) ? chunks[flatten(x - 1, y, z)]
							: null);
					chunks[i].rightNeighbor(
						withinChunks(x + 1, y, z) ? chunks[flatten(x + 1, y, z)]
							: null);
					chunks[i].topNeighbor(
						withinChunks(x, y + 1, z) ? chunks[flatten(x, y + 1, z)]
							: null);
					chunks[i].bottomNeighbor(
						withinChunks(x, y - 1, z) ? chunks[flatten(x, y - 1, z)]
							: null);
					chunks[i].frontNeighbor(
						withinChunks(x, y, z + 1) ? chunks[flatten(x, y, z + 1)]
							: null);
					chunks[i].backNeighbor(
						withinChunks(x, y, z - 1) ? chunks[flatten(x, y, z - 1)]
							: null);

				}
			}
		}
	}

	public void calculateLights()
	{
		if (lightMap.needsUpdating())
		{
			Vector3i extremeNeg = MemoryPool.getInstanceOrCreate(Vector3i.class);
			Vector3i extremePos = MemoryPool.getInstanceOrCreate(Vector3i.class);
			
			Map<Vector3ic, Integer> changedAreas = lightMap.updateMap();

			for (Vector3ic point : changedAreas.keySet())
			{
				int radius = changedAreas.get(point);

				extremeNeg.set(point.x() - radius,
					point.y() - radius, point.z() - radius);
				extremePos.set(point.x() + radius,
					point.y() + radius, point.z() + radius);

				for (int cz = extremeNeg.z() / 16; cz < Math
					.ceil(extremePos.z() / 16.0f); cz++)
				{
					for (int cy = extremeNeg.y() / 16; cy < Math
						.ceil(extremePos.y() / 16.0f); cy++)
					{
						for (int cx = extremeNeg.x() / 16; cx < Math
							.ceil(extremePos.x() / 16.0f); cx++)
						{
							if (withinChunks(cx, cy, cz))
								chunks[flatten(cx, cy, cz)].needsRendered(true);
						}
					}
				}
			}
		}
	}

	@Override
	public void write(DataOutputStream writer) throws IOException
	{
		long sec = System.currentTimeMillis();

		writer.writeInt(width);
		writer.writeInt(height);
		writer.writeInt(length);

		for (int z = 0; z < chunksLength(); z++)
		{
			for (int y = 0; y < chunksHeight(); y++)
			{
				for (int x = 0; x < chunksWidth(); x++)
				{
					chunk(x, y, z).write(writer);
				}
			}
		}

		Logger.LOGGER.debug((System.currentTimeMillis() - sec) + "ms to save "
			+ width() + "x" + height + "x" + length() + " structure.");
	}

	@Override
	public void read(DataInputStream reader) throws IOException
	{
		long sec = System.currentTimeMillis();

		this.width = reader.readInt();
		this.height = reader.readInt();
		this.length = reader.readInt();

		cLength = (int) Math.ceil((float) length / Chunk.LENGTH);
		cHeight = (int) Math.ceil((float) height / Chunk.HEIGHT);
		cWidth = (int) Math.ceil((float) width / Chunk.WIDTH);

		lightMap = new LightMap(width + 2, height + 2, length + 2);

		chunks = new Chunk[cLength * cHeight * cWidth];

		init();

		for (int z = 0; z < chunksLength(); z++)
		{
			for (int y = 0; y < chunksHeight(); y++)
			{
				for (int x = 0; x < chunksWidth(); x++)
				{
					chunk(x, y, z).read(reader);
				}
			}
		}

		for (int z = 0; z < length(); z++)
		{
			for (int y = 0; y < height(); y++)
			{
				for (int x = 0; x < width(); x++)
				{
					if (hasBlock(x, y, z))
					{
						Block b = block(x, y, z);

						totalMass += b.mass();

						blockSystemManager
							.addBlock(new StructureBlock(this, x, y, z));
					}
				}
			}
		}

		Logger.LOGGER.debug((System.currentTimeMillis() - sec) + "ms to read "
			+ width() + "x" + height + "x" + length() + " structure.");
	}

	public boolean withinChunks(int x, int y, int z)
	{
		return x >= 0 && x < cWidth && y >= 0 && y < cHeight && z >= 0
			&& z < cLength;
	}

	public boolean withinBlocks(int x, int y, int z)
	{
		return x >= 0 && x < width && y >= 0 && y < height && z >= 0
			&& z < length;
	}

	/**
	 * The position of the center the block in the world at this position in
	 * chunk coordinates [0-16)
	 * 
	 * @param c   The chunk
	 * @param pos The position relative to the chunk (bounded by [0, 16))
	 * @param out The output vector
	 * @return position of the center the block in the world at this position in
	 *         chunk coordinates
	 */
	public Vector3f chunkCoordsToWorldCoords(Chunk c, Vector3i pos,
		Vector3f out)
	{
		this.chunkRelativePosCentered(c, out);

		out.add(pos.x - Chunk.HALF_WIDTH + 0.5f,
			pos.y - Chunk.HALF_HEIGHT + 0.5f, pos.z - Chunk.HALF_LENGTH + 0.5f);

		body().transform().orientation().applyRotation(out, out);

		out.add(position());

		return out;
	}

	public Vector3i worldCoordsToChunkCoords(Vector3fc v, Vector3i out)
	{
		Vector3f temp = MemoryPool.getInstanceOrCreate(Vector3f.class).set(v);
		temp.sub(body().transform().position());
		this.body().transform().orientation().applyInverseRotation(temp, temp);

		if (cWidth % 2 == 0)
		{
			if (temp.x >= 0)
			{
				out.x = (int) temp.x % Chunk.WIDTH;
			}
			else
			{
				out.x = (Chunk.WIDTH + (int) Math.floor(temp.x) % Chunk.WIDTH)
					% Chunk.WIDTH;
			}
		}
		else
		{
			if (temp.x >= 0)
			{
				out.x = (int) (temp.x + Chunk.WIDTH / 2) % Chunk.WIDTH;
			}
			else
			{
				out.x = (Chunk.WIDTH
					- (int) Math.abs(Math.floor(temp.x - Chunk.WIDTH / 2))
						% Chunk.WIDTH)
					% Chunk.WIDTH;
			}
		}

		if (cHeight % 2 == 0)
		{
			if (temp.y >= 0)
			{
				out.y = (int) temp.y % Chunk.HEIGHT;
			}
			else
			{
				out.y = (Chunk.HEIGHT + (int) Math.floor(temp.y) % Chunk.HEIGHT)
					% Chunk.HEIGHT;
			}
		}
		else
		{
			if (temp.y >= 0)
			{
				out.y = (int) (temp.y + Chunk.HEIGHT / 2) % Chunk.HEIGHT;
			}
			else
			{
				out.y = (Chunk.HEIGHT
					- (int) Math.abs(Math.floor(temp.y - Chunk.HEIGHT / 2))
						% Chunk.HEIGHT)
					% Chunk.HEIGHT;
			}
		}

		if (cLength % 2 == 0)
		{
			if (temp.z >= 0)
			{
				out.z = (int) temp.z % Chunk.LENGTH;
			}
			else
			{
				out.z = (Chunk.LENGTH + (int) Math.floor(temp.z) % Chunk.LENGTH)
					% Chunk.LENGTH;
			}
		}
		else
		{
			if (temp.z >= 0)
			{
				out.z = (int) (temp.z + Chunk.LENGTH / 2) % Chunk.LENGTH;
			}
			else
			{
				out.z = (Chunk.LENGTH
					- (int) Math.abs(Math.floor(temp.z - Chunk.LENGTH / 2))
						% Chunk.LENGTH)
					% Chunk.LENGTH;
			}
		}
		
		MemoryPool.addToPool(temp);

		return out;
	}

	public void block(Vector3ic pos, Block b)
	{
		this.block(pos.x(), pos.y(), pos.z(), b);
	}

	public Block block(Vector3ic pos)
	{
		return this.block(pos.x(), pos.y(), pos.z());
	}

	/**
	 * Returns the block data for that specific block Creates that data if none
	 * is present
	 * 
	 * @param x The block's X
	 * @param y The block's Y
	 * @param z The block's Z
	 * @return the block data for that specific block
	 */
	public BlockData blockData(int x, int y, int z)
	{
		if (!initialized)
			throw new IllegalStateException(
				"Cannot get block data from an uninitialized structure!");

		if (withinBlocks(x, y, z))
		{
			Chunk c = chunkFromBlockCoords(x, y, z);
			return c.blockData(x % Chunk.WIDTH, y % Chunk.HEIGHT,
				z % Chunk.LENGTH);
		}
		else
			throw new IndexOutOfBoundsException(
				x + ", " + y + ", " + z + " was out of bounds for " + width
					+ "x" + height + "x" + length);
	}

	public void block(int x, int y, int z, Block b)
	{
		if (!initialized)
			init();

		if (withinBlocks(x, y, z))
		{
			Chunk c = chunkFromBlockCoords(x, y, z);

			Block old = c.block(x % Chunk.WIDTH, y % Chunk.HEIGHT,
				z % Chunk.LENGTH);

			if (old != null)
			{
				totalMass -= old.mass();
			}

			if (b != null)
			{
				totalMass += b.mass();
			}

			StructureBlock sb = new StructureBlock(this, x, y, z);

			blockSystemManager.removeBlock(sb);

			c.block(x % Chunk.WIDTH, y % Chunk.HEIGHT, z % Chunk.LENGTH, b);

			blockSystemManager.addBlock(sb);
			
			changes.add(sb);
		}
		else
			throw new IndexOutOfBoundsException(
				x + ", " + y + ", " + z + " was out of bounds for " + width
					+ "x" + height + "x" + length);
	}
	
	public boolean isDirty()
	{
		return changes.size() != 0;
	}
	
	public void makeClean()
	{
		changes.clear();
	}
	
	public Set<StructureBlock> modifiedBlocks()
	{
		return changes;
	}

	public Block block(int x, int y, int z)
	{
		if (!initialized)
			init();

		if (withinBlocks(x, y, z))
		{
			Chunk c = chunkFromBlockCoords(x, y, z);

			return c.block(x % Chunk.WIDTH, y % Chunk.HEIGHT, z % Chunk.LENGTH);
		}
		else
			throw new IndexOutOfBoundsException(
				x + ", " + y + ", " + z + " was out of bounds for " + width
					+ "x" + height + "x" + length);
	}

	public int length()
	{
		return length;
	}

	public int height()
	{
		return height;
	}

	public int width()
	{
		return width;
	}

	private Matrix4f openGLMatrixContainer = new Matrix4f();
	public Matrix4fc openGLMatrix()
	{
		openGLMatrixContainer.set(body().transform().matrix());
		openGLMatrixContainer.translate(-width() / 2.f, -height() / 2.f, -length() / 2.f);
		return openGLMatrixContainer;
	}

	public Matrix4fc transformMatrix()
	{
		return body().transform().matrix();
	}

	public LightMap lightMap()
	{
		return lightMap;
	}

	public void removeBlock(int x, int y, int z)
	{
		block(x, y, z, null);
	}

	public Vector3i worldCoordsToBlockCoords(Vector3fc v, Vector3i out)
	{
		Vector3f temp = MemoryPool.getInstanceOrCreate(Vector3f.class).set(v);
		
		temp.sub(position());
		body().transform().orientation().applyInverseRotation(temp, temp);

		temp.add(width() / 2.f, height() / 2.f, length() / 2.f);

		out.set((int) temp.x, (int) temp.y, (int) temp.z);
		
		MemoryPool.addToPool(temp);
		
		return out;
	}

	public Vector3f blockCoordsToWorldCoords(float x, float y, float z,
		Vector3f storage)
	{
		storage.set(x - width() / 2.f + 0.5f, y - height() / 2.f + 0.5f,
			z - length() / 2.f + 0.5f);

		body().transform().orientation().applyRotation(storage, storage);

		storage.add(body().transform().position());

		return storage;
	}

	public Vector3f blockCoordsToWorldCoords(Vector3fc v, Vector3f storage)
	{
		return blockCoordsToWorldCoords(v.x(), v.y(), v.z(), storage);
	}

	public Vector3f blockCoordsToWorldCoords(Vector3ic v, Vector3f storage)
	{
		return blockCoordsToWorldCoords(v.x(), v.y(), v.z(), storage);
	}

	public Vector3i chunkCoordsToBlockCoords(Chunk c, Vector3ic v, Vector3i out)
	{
		Vector3fc rp = c.relativePosition();

		out.set((int) rp.x(), (int) rp.y(), (int) rp.z());

		out.x += v.x() - Chunk.WIDTH / 2 + width() / 2;
		out.y += v.y() - Chunk.HEIGHT / 2 + height() / 2;
		out.z += v.z() - Chunk.LENGTH / 2 + length() / 2;

		return out;
	}

	public int higehstYAt(int x, int z)
	{
		for (int y = height() - 1; y >= 0; y--)
		{
			if (block(x, y, z) != null)
				return y;
		}

		return -1;
	}

	public int lowestYAt(int x, int z)
	{
		for (int y = 0; y < height(); y++)
		{
			if (block(x, y, z) != null)
				return y;
		}

		return -1;
	}

	public boolean hasBlock(Vector3ic v)
	{
		return hasBlock(v.x(), v.y(), v.z());
	}

	public boolean hasBlock(int x, int y, int z)
	{
		return withinBlocks(x, y, z) && block(x, y, z) != null;
	}

	public int id()
	{
		return id;
	}

	@Override
	public int hashCode()
	{
		return id;
	}

	@Override
	public boolean equals(Object o)
	{
		if (o instanceof Structure)
		{
			return ((Structure) o).id == id;
		}
		return false;
	}

	public void increasePowerCapacity(float delta)
	{
		maxEnergy += delta;
	}

	public void decreasePowerCapacity(float delta)
	{
		maxEnergy -= delta;
	}

	public float mass()
	{
		return totalMass;
	}

	private Vector3f lastCalculations = new Vector3f();
	private Map<Chunk, OBBCollider> obbs = new HashMap<>();

	public OBBCollider obbForChunk(Chunk c)
	{
		Vector3fc position = position();
		if (!lastCalculations.equals(position))
		{
			obbs.clear();
			lastCalculations.set(position);
		}

		if (!obbs.containsKey(c))
		{
			obbs.put(c, new OBBCollider(chunkWorldPosition(c, new Vector3f()),
				body().transform().orientation(), Chunk.HALF_DIMENSIONS));
		}

		return obbs.get(c);
	}

	public OBBCollider OBB()
	{
		return MemoryPool.getInstanceOrCreate(OBBCollider.class).set(position(),
			body().transform().orientation(),
			halfwidths);
	}

	public Vector3f blockCoordToWorldCoord(Vector3ic b, Vector3f out)
	{
		out.set(b.x() - width() / 2.f + 0.5f, b.y() - height() / 2.f + 0.5f,
			b.z() - length() / 2.f + 0.5f);

		this.body().transform().orientation().applyRotation(out, out);

		out.add(position());

		return out;
	}

	private OBBCollider genOBB(Vector3fc pos, Vector3fc halfwidths)
	{
		return MemoryPool.getInstanceOrCreate(OBBCollider.class).set(pos,
			body().transform().orientation(), halfwidths);
	}

	private OBBCollider genOBB(Vector3ic pos, Vector3fc halfwidths)
	{
		return genOBB(blockCoordToWorldCoord(pos, new Vector3f()), halfwidths);
	}

	private static final Vector3fc HALF_WIDTHS_DEFAULT = new Vector3f(0.5f,
		0.5f, 0.5f);

	public OBBCollider wholeOBBForBlock(Vector3ic pos)
	{
		if (withinBlocks(pos.x(), pos.y(), pos.z()))
			return genOBB(pos, HALF_WIDTHS_DEFAULT);

		return null;
	}

	public OBBCollider obbForBlock(Vector3ic pos)
	{
		if (hasBlock(pos.x(), pos.y(), pos.z()))
			return genOBB(pos, block(pos.x(), pos.y(), pos.z()).halfWidths());

		return null;
	}

	public OBBCollider obbForBlock(int x, int y, int z)
	{
		if (hasBlock(x, y, z))
			return genOBB(blockCoordsToWorldCoords(x, y, z, new Vector3f()),
				block(x, y, z).halfWidths());

		return null;
	}

	public OBBCollider wholeOBBForBlock(int x, int y, int z)
	{
		if (withinBlocks(x, y, z))
			return genOBB(blockCoordsToWorldCoords(x, y, z, new Vector3f()),
				HALF_WIDTHS_DEFAULT);

		return null;
	}

	protected BlockSystemManager blockSystemManager()
	{
		return blockSystemManager;
	}

	@Override
	public void updateGraphics()
	{
		if (lightMap().needsUpdating())
			calculateLights();

		for (Chunk c : chunks())
			if (c.needsRendered())
				c.render();
	}

	@Override
	public void draw(Matrix4fc projectionMatrix, Matrix4fc camera,
		ClientPlayer p)
	{
		for (Chunk chunk : chunks())
		{
			Matrix4f transform = new Matrix4f();
			Matrix4fc trans = openGLMatrix();
			trans.mul(chunk.transformMatrix(), transform);

			for (MaterialMesh m : chunk.model().materialMeshes())
			{
				m.material().use();

				m.material().initUniforms(projectionMatrix, camera, transform,
					false);

				m.mesh().prepare();
				m.mesh().draw();
				m.mesh().finish();

				m.material().stop();
			}
		}
	}

	@Override
	public boolean shouldBeDrawn()
	{
		return true;
	}
}

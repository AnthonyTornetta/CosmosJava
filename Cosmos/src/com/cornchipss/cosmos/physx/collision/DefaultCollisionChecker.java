package com.cornchipss.cosmos.physx.collision;

import java.awt.Color;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.joml.Vector3f;
import org.joml.Vector3fc;
import org.joml.Vector3i;

import com.cornchipss.cosmos.memory.MemoryPool;
import com.cornchipss.cosmos.netty.NettySide;
import com.cornchipss.cosmos.physx.PhysicalObject;
import com.cornchipss.cosmos.physx.collision.obb.IOBBCollisionChecker;
import com.cornchipss.cosmos.physx.collision.obb.OBBCollider;
import com.cornchipss.cosmos.physx.collision.obb.OBBCollisionCheckerJOML;
import com.cornchipss.cosmos.rendering.debug.DebugRenderer;
import com.cornchipss.cosmos.rendering.debug.DebugRenderer.DrawMode;
import com.cornchipss.cosmos.structures.Structure;
import com.cornchipss.cosmos.world.Chunk;

public class DefaultCollisionChecker implements ICollisionChecker
{
	private IOBBCollisionChecker obbChecker;

	public DefaultCollisionChecker()
	{
		obbChecker = new OBBCollisionCheckerJOML();
	}

	private void aggregateChunks(Structure a, Structure b, Vector3fc deltaA,
		Map<Chunk, List<Chunk>> map)
	{
		for (int z = 0; z < a.chunksLength(); z++)
		{
			for (int y = 0; y < a.chunksHeight(); y++)
			{
				for (int x = 0; x < a.chunksWidth(); x++)
				{
					Chunk aChunk = a.chunk(x, y, z);

					if (aChunk.empty())
						continue;

					OBBCollider obbA = a.obbForChunk(aChunk);

					for (int zz = 0; zz < b.chunksLength(); zz++)
					{
						for (int yy = 0; yy < b.chunksHeight(); yy++)
						{
							for (int xx = 0; xx < b.chunksWidth(); xx++)
							{
								Chunk bChunk = b.chunk(xx, yy, zz);

								if (bChunk.empty())
									continue;

								OBBCollider obbB = b.obbForChunk(bChunk);

								if (obbChecker.testMovingOBBOBB(deltaA, obbA,
									obbB, null))
								{
									List<Chunk> here = map.getOrDefault(aChunk,
										new LinkedList<>());
									here.add(bChunk);
									map.put(aChunk, here);
								}
							}
						}
					}
				}
			}
		}
	}

	private boolean fineCheck(Chunk a, Chunk b, Vector3fc deltaA,
		CollisionInfo info)
	{
		List<Integer> xs = new LinkedList<>();
		List<Integer> ys = new LinkedList<>();
		List<Integer> zs = new LinkedList<>();

		{
			OBBCollider bOBB = b.structure().obbForChunk(b);

			Vector3f pos = a.structure().chunkWorldPosition(a,
				MemoryPool.getInstanceOrCreate(Vector3f.class).set(0, 0, 0));

			OBBCollider obc = MemoryPool.getInstanceOrCreate(OBBCollider.class);

			Vector3f temp = MemoryPool.getInstanceOrCreate(Vector3f.class);

			Vector3f at = MemoryPool.getInstanceOrCreate(Vector3f.class)
				.set(pos);

			at.sub(a.structure().body().orientation().up()
				.mul(Chunk.HEIGHT / 2.f, temp));

			// Start Y
			Vector3f halfwidthsStorage = MemoryPool
				.getInstanceOrCreate(Vector3f.class)
				.set(Chunk.WIDTH / 2.f, 0.5f, Chunk.LENGTH / 2.f);

			for (int y = 0; y < a.height(); y++)
			{
				obc.set(at, a.structure().body().orientation(),
					halfwidthsStorage);

				if (obbChecker.testMovingOBBOBB(deltaA, obc, bOBB, null))
				{
					ys.add(y);
				}

				at.add(a.structure().body().orientation().up());
			}

			at.set(pos);
			at.sub(a.structure().body().orientation().right()
				.mul(Chunk.WIDTH / 2.f, temp));

			// Start X
			halfwidthsStorage.set(0.5f, Chunk.HEIGHT / 2.f, Chunk.LENGTH / 2.f);

			for (int x = 0; x < a.width(); x++)
			{
				obc.set(at, a.structure().body().orientation(),
					halfwidthsStorage);

				if (obbChecker.testMovingOBBOBB(deltaA, obc, bOBB, null))
				{
					xs.add(x);
				}

				at.add(a.structure().body().orientation().right());
			}

			Vector3f backward = a.structure().body().orientation()
				.forward().mul(-1, MemoryPool.getInstanceOrCreate(Vector3f.class));
			
			at.set(pos);
			// it is add here
			at.add(a.structure().body().orientation().forward()
				.mul(Chunk.LENGTH / 2.f, temp));

			// Start Z
			halfwidthsStorage.set(Chunk.WIDTH / 2.f, Chunk.HEIGHT / 2.f, 0.5f);

			for (int z = 0; z < a.length(); z++)
			{
				obc.set(at, a.structure().body().orientation(),
					halfwidthsStorage);

				if (obbChecker.testMovingOBBOBB(deltaA, obc, bOBB, null))
				{
					zs.add(z);
				}

				at.add(backward);
			}
			
			MemoryPool.addToPool(obc);
			MemoryPool.addToPool(at);
			MemoryPool.addToPool(pos);
			MemoryPool.addToPool(temp);
			MemoryPool.addToPool(backward);
			MemoryPool.addToPool(halfwidthsStorage.set(0));
		}

		Vector3i tempVeci = MemoryPool.getInstanceOrCreate(Vector3i.class);
		Vector3i out = MemoryPool.getInstanceOrCreate(Vector3i.class);
		
		try
		{
			boolean hit = false;
	
			for (int z : zs)
			{
				for (int y : ys)
				{
					for (int x : xs)
					{
						if (a.hasBlock(x, y, z))
						{
							tempVeci.set(x, y, z);
							Vector3i blockCoords = a.structure()
								.chunkCoordsToBlockCoords(a, tempVeci, out);
	
							OBBCollider obbBlockA = a.structure()
								.obbForBlock(blockCoords);
	
							if (obbChecker.testMovingOBBOBB(deltaA, obbBlockA,
								b.structure().obbForChunk(b), null))
							{
								for (Vector3fc pointOfInterest : obbBlockA)
								{
									if (b.testLineIntersection(pointOfInterest,
										deltaA, info, obbChecker))
									{
										if (info == null
											|| info.distanceSquared == 0)
											return true;
										hit = true;
									}
								}
							}
						}
					}
				}
			}
	
			return hit;
		}
		finally
		{
			MemoryPool.addToPool(tempVeci);
			MemoryPool.addToPool(out);
		}
	}

	@Override
	public boolean colliding(PhysicalObject a, PhysicalObject b,
		Vector3fc deltaA, CollisionInfo info)
	{
		if (a instanceof Structure && b instanceof Structure)
		{
			boolean hit = false;

			Structure sa = (Structure) a;
			Structure sb = (Structure) b;

			if (!obbChecker.testMovingOBBOBB(deltaA, sa.OBB(), sb.OBB(), null))
			{
				return false;
			}

			Map<Chunk, List<Chunk>> chunks = new HashMap<>();

			aggregateChunks(sa, sb, deltaA, chunks);

			for (Chunk aC : chunks.keySet())
			{
				for (Chunk bC : chunks.get(aC))
				{
					if (fineCheck(aC, bC, deltaA, info))
					{
						if (info == null || info.distanceSquared == 0)
							return true;

						hit = true;
					}
				}
			}

			return hit;

		} // BEWARE: UNTESTED CODE BELOW THIS LINE //
		else if (b instanceof Structure)
		{
			boolean hit = false;

			OBBCollider obbA = a.OBB();

			if (NettySide.side() == NettySide.CLIENT)
			{
				DebugRenderer.instance().drawOBB(obbA, Color.red,
					DrawMode.LINES);
				DebugRenderer.instance().drawOBB(b.OBB(), Color.blue,
					DrawMode.LINES);
			}

			if (!obbChecker.testMovingOBBOBB(deltaA, obbA, b.OBB(), null))
			{
				return false;
			}

			for (Chunk c : ((Structure) b).chunks())
			{
				if (c.empty())
					continue;

				OBBCollider cldr = ((Structure) b).obbForChunk(c);

				if (obbChecker.testMovingOBBOBB(deltaA, obbA, cldr, null))
				{
					DebugRenderer.instance().drawOBB(cldr, Color.green,
						DrawMode.LINES);

					for (Vector3fc pointOfInterest : obbA)
					{
						if (c.testLineIntersection(pointOfInterest, deltaA,
							info, obbChecker))
						{
							if (info == null || info.distanceSquared == 0)
								return true;
							hit = true;
						}
					}
				}
			}

			return hit;
		}
		else if (a instanceof Structure)
		{
			OBBCollider obbB = b.OBB();

			if (!obbChecker.testMovingOBBOBB(deltaA, a.OBB(), obbB, null))
			{
				return false;
			}

			boolean hit = false;

			for (Chunk c : ((Structure) a).chunks())
			{
				if (c.empty())
					continue;

				Vector3i chunkCoords = MemoryPool
					.getInstanceOrCreate(Vector3i.class),
					blockCoords = MemoryPool
						.getInstanceOrCreate(Vector3i.class);

				try
				{
					if (obbChecker.testMovingOBBOBB(deltaA,
						((Structure) a).obbForChunk(c), obbB, null))
					{
						for (int z = 0; z < c.length(); z++)
						{
							for (int y = 0; y < c.height(); y++)
							{
								for (int x = 0; x < c.width(); x++)
								{
									if (c.hasBlock(x, y, z))
									{
										chunkCoords.set(x, y, z);
										((Structure) a)
											.chunkCoordsToBlockCoords(c,
												chunkCoords, blockCoords);

										if (obbChecker
											.testMovingOBBOBB(
												deltaA, ((Structure) a)
													.obbForBlock(blockCoords),
												obbB, info))
										{
											if (info == null
												|| info.distanceSquared == 0)
												return true;

											hit = true;
										}
									}
								}
							}
						}
					}
				}
				finally
				{
					MemoryPool.addToPool(chunkCoords.set(0));
					MemoryPool.addToPool(blockCoords.set(0));
				}
			}

			return hit;
		}
		else
		{
			return obbChecker.testMovingOBBOBB(deltaA, a.OBB(), b.OBB(), info);
		}
	}
}

package com.cornchipss.cosmos.physx.collision;

import java.awt.Color;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.joml.Vector3f;
import org.joml.Vector3fc;
import org.joml.Vector3i;

import com.cornchipss.cosmos.netty.NettySide;
import com.cornchipss.cosmos.physx.PhysicalObject;
import com.cornchipss.cosmos.physx.collision.obb.IOBBCollisionChecker;
import com.cornchipss.cosmos.physx.collision.obb.OBBCollider;
import com.cornchipss.cosmos.physx.collision.obb.OBBCollisionCheckerJOML;
import com.cornchipss.cosmos.rendering.debug.DebugRenderer;
import com.cornchipss.cosmos.rendering.debug.DebugRenderer.DrawMode;
import com.cornchipss.cosmos.structures.Structure;
import com.cornchipss.cosmos.utils.Utils;
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
		OBBCollider bOBB = b.structure().obbForChunk(b);

		Vector3fc pos = a.structure().chunkWorldPosition(a, new Vector3f());

		List<Integer> xs = new LinkedList<>();
		List<Integer> ys = new LinkedList<>();
		List<Integer> zs = new LinkedList<>();

		{
			Vector3f at = new Vector3f(pos);
			at.sub(a.structure().body().transform().orientation().up()
				.mul(Chunk.HEIGHT / 2.f, new Vector3f()));

			// Start Y
			Vector3f halfWidthsY = new Vector3f(Chunk.WIDTH / 2.f, 0.5f,
				Chunk.LENGTH / 2.f);

			for (int y = 0; y < a.height(); y++)
			{
				OBBCollider obc = new OBBCollider(at,
					a.structure().body().transform().orientation(),
					halfWidthsY);

				if (obbChecker.testMovingOBBOBB(deltaA, obc, bOBB, null))
				{
					ys.add(y);
				}

				at.add(a.structure().body().transform().orientation().up());
			}
		}

		{
			Vector3f at = new Vector3f(pos);
			at.sub(a.structure().body().transform().orientation().right()
				.mul(Chunk.WIDTH / 2.f, new Vector3f()));

			// Start X
			Vector3f halfWidthsX = new Vector3f(0.5f, Chunk.HEIGHT / 2.f,
				Chunk.LENGTH / 2.f);

			for (int x = 0; x < a.width(); x++)
			{
				OBBCollider obc = new OBBCollider(at,
					a.structure().body().transform().orientation(),
					halfWidthsX);

				if (obbChecker.testMovingOBBOBB(deltaA, obc, bOBB, null))
				{
					xs.add(x);
				}

				at.add(a.structure().body().transform().orientation().right());
			}
		}

		Vector3fc backward = a.structure().body().transform().orientation()
			.forward().mul(-1, new Vector3f());

		{
			Vector3f at = new Vector3f(pos);
			// it is add here
			at.add(a.structure().body().transform().orientation().forward()
				.mul(Chunk.LENGTH / 2.f, new Vector3f()));

			// Start Z
			Vector3f halfWidthsZ = new Vector3f(Chunk.WIDTH / 2.f,
				Chunk.HEIGHT / 2.f, 0.5f);

			for (int z = 0; z < a.length(); z++)
			{
				OBBCollider obc = new OBBCollider(at,
					a.structure().body().transform().orientation(),
					halfWidthsZ);

				if (obbChecker.testMovingOBBOBB(deltaA, obc, bOBB, null))
				{
					zs.add(z);
				}

				at.add(backward);
			}
		}

		boolean hit = false;

		Vector3i temp = new Vector3i(), out = new Vector3i();

		for (int z : zs)
		{
			for (int y : ys)
			{
				for (int x : xs)
				{
					if (a.hasBlock(x, y, z))
					{
						temp.set(x, y, z);
						Vector3i blockCoords = a.structure()
							.chunkCoordsToBlockCoords(a, temp, out);

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
				Utils.println("EARLY RETURN");
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

				Vector3i chunkCoords = new Vector3i(),
					blockCoords = new Vector3i();

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
									((Structure) a).chunkCoordsToBlockCoords(c,
										chunkCoords, blockCoords);

									if (obbChecker.testMovingOBBOBB(deltaA,
										((Structure) a).obbForBlock(
											blockCoords),
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

			return hit;
		}
		else
		{
			return obbChecker.testMovingOBBOBB(deltaA, a.OBB(), b.OBB(), info);
		}
	}
}

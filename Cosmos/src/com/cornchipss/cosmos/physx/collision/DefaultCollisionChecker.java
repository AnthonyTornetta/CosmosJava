package com.cornchipss.cosmos.physx.collision;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.joml.Vector3f;
import org.joml.Vector3fc;

import com.cornchipss.cosmos.physx.PhysicalObject;
import com.cornchipss.cosmos.physx.collision.obb.IOBBCollisionChecker;
import com.cornchipss.cosmos.physx.collision.obb.OBBCollider;
import com.cornchipss.cosmos.physx.collision.obb.OBBCollisionCheckerJOML;
import com.cornchipss.cosmos.structures.Structure;
import com.cornchipss.cosmos.world.Chunk;

public class DefaultCollisionChecker implements ICollisionChecker
{
	private IOBBCollisionChecker obbChecker;
	
	public DefaultCollisionChecker()
	{
		obbChecker = new OBBCollisionCheckerJOML();
	}
	
	private void calcChunks(List<Chunk> chunks, Vector3fc deltaA, Structure a, Structure b)
	{
		OBBCollider obbB = new OBBCollider(b.position(), b.body().transform().orientation(), 
				new Vector3f(b.width() / 2.f, b.height() / 2.f, b.length() / 2.f));
		
		for(int z = 0; z < a.chunksLength(); z++)
		{
			for(int y = 0; y < a.chunksHeight(); y++)
			{
				for(int x = 0; x < a.chunksWidth(); x++)
				{
					OBBCollider obbA = a.obbForChunk(a.chunk(x, y, z));
					
					if(obbChecker.testMovingOBBOBB(deltaA, obbA, obbB, null))
					{
						chunks.add(a.chunk(x, y, z));
					}
				}
			}
		}
	}
	
	private void aggregateChunks(Structure sa, Structure sb, Vector3fc deltaA, Map<Chunk, List<Chunk>> cl)
	{
		List<Chunk> aChunks = new LinkedList<>();
		List<Chunk> bChunks = new LinkedList<>();
		
		calcChunks(aChunks, deltaA, sa, sb);
		calcChunks(bChunks, deltaA.mul(-1, new Vector3f()), sb, sa);
		
		for(Chunk c : aChunks)
		{
			if(c.empty())
				continue;
			
			OBBCollider obbA = sa.obbForChunk(c);
			
			for(Chunk bc : bChunks)
			{
				if(bc.empty())
					continue;
				
				OBBCollider obbB = sb.obbForChunk(bc);
				
				if(obbChecker.testMovingOBBOBB(deltaA, obbA, obbB, null))
				{
					List<Chunk> chunks = cl.getOrDefault(cl, new LinkedList<>());
					chunks.add(bc);
					cl.put(c, chunks);
				}
			}
		}
	}
	
	private boolean fineCheck(Chunk a, Chunk b, Vector3fc deltaA, CollisionInfo info)
	{		
		OBBCollider bOBB = b.structure().obbForChunk(b);
		
		Vector3fc pos = a.relativePosition();
		
		List<Integer> xs = new LinkedList<>();
		List<Integer> ys = new LinkedList<>();		
		List<Integer> zs = new LinkedList<>();

		{
			Vector3f at = new Vector3f(pos);
			at.sub(a.structure().body().transform().orientation().up().mul(Chunk.HEIGHT / 2.f, new Vector3f()));
			
			// Start Y
			Vector3f halfWidthsY = new Vector3f(Chunk.WIDTH / 2.f, 0.5f, Chunk.LENGTH / 2.f);
			
			for(int y = 0; y < a.height(); y++)
			{
				OBBCollider obc = new OBBCollider(at, 
						a.structure().body().transform().orientation(), 
						halfWidthsY);

				if(obbChecker.testMovingOBBOBB(deltaA, obc, bOBB, null))
				{
					ys.add(y);
				}
				
				at.add(a.structure().body().transform().orientation().up());
			}
		}
		
		{
			Vector3f at = new Vector3f(pos);
			at.sub(a.structure().body().transform().orientation().right().mul(Chunk.WIDTH / 2.f, new Vector3f()));
			
			// Start X
			Vector3f halfWidthsX = new Vector3f(0.5f, Chunk.HEIGHT / 2.f, Chunk.LENGTH / 2.f);
			
			for(int x = 0; x < a.width(); x++)
			{
				OBBCollider obc = new OBBCollider(at, 
						a.structure().body().transform().orientation(), 
						halfWidthsX);
				
				if(obbChecker.testMovingOBBOBB(deltaA, obc, bOBB, null))
				{
					xs.add(x);
				}
				
				at.add(a.structure().body().transform().orientation().right());
			}
		}
		
		Vector3fc backward = a.structure().body().transform().orientation().forward().mul(-1, new Vector3f());
		
		{
			Vector3f at = new Vector3f(pos);
			// it is add here
			at.add(a.structure().body().transform().orientation().forward().mul(Chunk.LENGTH / 2.f, new Vector3f()));
			
			// Start Z
			Vector3f halfWidthsZ = new Vector3f(Chunk.WIDTH / 2.f, Chunk.HEIGHT / 2.f, 0.5f);
			
			for(int z = 0; z < a.length(); z++)
			{
				OBBCollider obc = new OBBCollider(at, 
						a.structure().body().transform().orientation(), 
						halfWidthsZ);

				if(obbChecker.testMovingOBBOBB(deltaA, obc, bOBB, null))
				{
					zs.add(z);
				}
				
				at.add(backward);
			}
		}
		
		if(info != null)
			info.distanceSquared = Float.MAX_VALUE;
		
		boolean hit = false;
		
		for(int z : zs)
		{
			for(int y : ys)
			{
				for(int x : xs)
				{
					if(a.hasBlock(x, y, z))
					{
						OBBCollider obbBlockA = a.obbForBlock(x, y, z);
						
						for(Vector3fc pointOfInterest : obbBlockA)
						{
							if(b.testLineIntersection(pointOfInterest, deltaA, info, obbChecker))
							{
								if(info == null)
									return true;
								hit = true;
							}
						}
					}
				}
			}
		}
				
		return hit;
	}
	
	@Override
	public boolean colliding(PhysicalObject a, PhysicalObject b, Vector3fc deltaA, CollisionInfo info)
	{
		if(a instanceof Structure && b instanceof Structure)
		{
			Structure sa = (Structure)a;
			Structure sb = (Structure)b;
			
			Map<Chunk, List<Chunk>> chunks = new HashMap<>();
			
			aggregateChunks(sa, sb, deltaA, chunks);
			
			for(Chunk aC : chunks.keySet())
			{
				for(Chunk bC : chunks.get(aC))
				{
					if(fineCheck(aC, bC, deltaA, info))
					{
						return true;
					}
				}
			}
		}
		
		return false;
	}
}

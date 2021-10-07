package com.cornchipss.cosmos.physx.collision;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.joml.Vector3f;
import org.joml.Vector3fc;
import org.joml.Vector3i;

import com.cornchipss.cosmos.blocks.Block;
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
	
	private void calcChunks(List<Chunk> chunks, Structure a, Structure b)
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
					if(obbChecker.testOBBOBB(obbA, obbB, null))
					{
						chunks.add(a.chunk(x, y, z));
					}
				}
			}
		}
	}
	
	private void aggregateChunks(Structure sa, Structure sb, Map<Chunk, List<Chunk>> cl)
	{
		List<Chunk> aChunks = new LinkedList<>();
		List<Chunk> bChunks = new LinkedList<>();
		
		calcChunks(aChunks, sa, sb);
		calcChunks(bChunks, sb, sa);
		
		for(Chunk c : aChunks)
		{
			OBBCollider obbA = sa.obbForChunk(c);
			for(Chunk bc : bChunks)
			{
				OBBCollider obbB = sb.obbForChunk(bc);
				
				if(obbChecker.testOBBOBB(obbA, obbB, null))
				{
					List<Chunk> chunks = cl.getOrDefault(cl, new LinkedList<>());
					chunks.add(bc);
					cl.put(c, chunks);
				}
			}
		}
	}
	
	private boolean fineCheck(Chunk a, Chunk b, Vector3f normal)
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
				
				if(obbChecker.testOBBOBB(obc, bOBB, null))
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
				
				if(obbChecker.testOBBOBB(obc, bOBB, null))
				{
					xs.add(x);
				}
				
				at.add(a.structure().body().transform().orientation().right());
			}
		}
		
		{
			Vector3f at = new Vector3f(pos);
			at.sub(a.structure().body().transform().orientation().forward().mul(Chunk.LENGTH / 2.f, new Vector3f()));
			
			// Start Z
			Vector3f halfWidthsZ = new Vector3f(Chunk.WIDTH / 2.f, Chunk.HEIGHT / 2.f, 0.5f);
			
			for(int z = 0; z < a.length(); z++)
			{
				OBBCollider obc = new OBBCollider(at, 
						a.structure().body().transform().orientation(), 
						halfWidthsZ);
				
				if(obbChecker.testOBBOBB(obc, bOBB, null))
				{
					zs.add(z);
				}
				
				at.add(a.structure().body().transform().orientation().forward());
			}
		}
		
		for(int z : zs)
		{
			for(int y : ys)
			{
				for(int x : xs)
				{
//					Block bbbb = a.block(x, y, z);
					if(a.hasBlock(x, y, z))
					{
						OBBCollider obbBlockA = a.obbForBlock(x, y, z);
						
						for(Vector3fc pointOfInterest : obbBlockA)
						{
							Vector3f delta = new Vector3f();
//							Vector3f p = a.structure().chunkWorldPosCentered(a, new Vector3f());
							Vector3f pp = b.structure().chunkWorldPosCentered(b, new Vector3f());
							
							delta.x += pointOfInterest.x() - pp.x - 0.5f;
							delta.y += pointOfInterest.y() - pp.y - 0.5f;
							delta.z += pointOfInterest.z() - pp.z - 0.5f;
							
//							delta.x += Math.signum(delta.x) * .001f;
//							delta.y += Math.signum(delta.y) * .001f;
//							delta.z += Math.signum(delta.z) * .001f; 
							
							Vector3i relative = new Vector3i((int)delta.x(), (int)delta.y(), (int)delta.z());
							
							relative.x += Chunk.WIDTH / 2;
							relative.y += Chunk.HEIGHT / 2;
							relative.z += Chunk.LENGTH / 2;
							
//							Block bbb = b.block(relative.x, relative.y, relative.z);
							
							if(b.hasBlock(relative))
							{
								OBBCollider obbBlockB = b.obbForBlock(relative.x, relative.y, relative.z);
								
								if(obbChecker.testOBBOBB(obbBlockA, obbBlockB, normal))
									return true;
							}
						}
					}
				}
			}
		}
		
		return false;
	}
	
	@Override
	public boolean colliding(PhysicalObject a, PhysicalObject b, Vector3f normal)
	{
		if(a instanceof Structure && b instanceof Structure)
		{
			Structure sa = (Structure)a;
			Structure sb = (Structure)b;
			
			Map<Chunk, List<Chunk>> chunks = new HashMap<>();
			
			aggregateChunks(sa, sb, chunks);
			
			for(Chunk aC : chunks.keySet())
			{
				for(Chunk bC : chunks.get(aC))
				{
					if(fineCheck(aC, bC, normal))
					{
						return true;
					}
				}
			}
		}
		
		return false;
	}
}

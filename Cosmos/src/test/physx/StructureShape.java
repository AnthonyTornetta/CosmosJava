package test.physx;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.joml.Intersectionf;
import org.joml.Vector3f;
import org.joml.Vector3fc;
import org.joml.Vector3i;

import com.cornchipss.utils.Maths;
import com.cornchipss.utils.Utils;
import com.cornchipss.world.blocks.BlockFace;

import test.Structure;
import test.blocks.Block;

public class StructureShape
{
	private Structure s;
	
	public StructureShape(Structure s)
	{
		this.s = s;
	}
	
	public boolean solidAt(float x, float y, float z)
	{
		x = Maths.floor(x + s.width() / 2.0f);
		y = Maths.floor(y + s.height() / 2.0f);
		z = Maths.floor(z + s.length() / 2.0f);
		
		return s.withinBlocks((int)x, (int)y, (int)z) && s.block((int)x, (int)y, (int)z) != null;
	}
	
	public Vector3f solidAt(float x, float y, float z, float w, float h, float l)
	{
		for(float xx = 0; xx <= Math.ceil(w); xx++)
		{
			for(float yy = 0; yy <= Math.ceil(h); yy++)
			{
				for(float zz = 0; zz <= Math.ceil(l); zz++)
				{
					if(solidAt(xx + x + 0.5f, yy + y + 0.5f, zz + z + 0.5f))
						return new Vector3f(xx + x + 0.5f, yy + y + 0.5f, zz + z + 0.5f);
				}
			}
		}
		
		return null;
	}
		
	public RayResult raycast(Vector3fc from, Vector3fc to)
	{
		Vector3f delta = new Vector3f(to.x() - from.x(), to.y() - from.y(), to.z() - from.z());
		
		Vector3f intersectionPoint = new Vector3f();
		Vector3f temp1 = new Vector3f(), temp2 = new Vector3f(), temp3 = new Vector3f();
		
		List<Vector3f> hitPositions = new LinkedList<>();
		List<Vector3f> hits = new LinkedList<>();
		List<BlockFace> faces = new LinkedList<>();
		
		for(float dz = 0; Math.abs(dz) <= Math.abs(delta.z()); dz += Maths.signum0(delta.z()))
		{
			for(float dy = 0; Math.abs(dy) <= Math.abs(delta.y()); dy += Maths.signum0(delta.y()))
			{
				for(float dx = 0; Math.abs(dx) <= Math.abs(delta.x()); dx += Maths.signum0(delta.x()))
				{
					float x = from.x() + dx;
					float y = from.y() + dy;
					float z = from.z() + dz;
					
					Vector3i coords = s.worldCoordsToStructureCoords(x, y, z);
					
					if(s.withinBlocks(coords.x, coords.y, coords.z))
					{
						Block b = s.block(coords.x, coords.y, coords.z);
						
						if(b != null)
						{
							PhysicsShape sh = new CubeShape();
							
							float xOff = (s.width() % 2) * 0.5f;
							float yOff = (s.height() % 2) * 0.5f;
							float zOff = (s.length() % 2) * 0.5f;
							
							float xx = Maths.floor(x + xOff) - xOff,
									yy = Maths.floor(y + yOff) - yOff,
									zz = Maths.floor(z + zOff) - zOff;
							
							for(int i = 0; i < sh.sides().length; i+=3)
							{
								BlockFace face = sh.faces()[i/3];
								
								temp1.set(xx + sh.sides()[i].x(), yy + sh.sides()[i].y(), zz + sh.sides()[i].z());
								temp2.set(xx + sh.sides()[i+1].x(), yy + sh.sides()[i+1].y(), zz + sh.sides()[i+1].z());
								temp3.set(xx + sh.sides()[i+2].x(), yy + sh.sides()[i+2].y(), zz + sh.sides()[i+2].z());
								
								if(Intersectionf.intersectLineSegmentTriangle(from, to, 
										temp1, temp2, temp3, 
										(float) 1E-9, intersectionPoint))
								{
									faces.add(face);
									hits.add(new Vector3f(coords.x, coords.y, coords.z));
									hitPositions.add(new Vector3f(intersectionPoint));
								}
							}
						}
					}
				}
			}
		}
		
		RayResult res = new RayResult(from, to, s, hits, faces);
		
		return res;
	}
}

package test.physx;

import org.joml.Intersectionf;
import org.joml.Vector3f;
import org.joml.Vector3fc;
import org.joml.Vector3i;

import com.cornchipss.utils.Maths;
import com.cornchipss.utils.Utils;

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
	
	public Vector3f firstHit(Vector3fc from, Vector3fc to)
	{
		Vector3f delta = new Vector3f(to.x() - from.x(), to.y() - from.y(), to.z() - from.z());
		
		float dist = Maths.sqrt(delta.x() * delta.x() + delta.y() * delta.y() + delta.z() * delta.z());
		
		Vector3f slope = new Vector3f(delta).div(dist);
		
		Vector3f intersectionPoint = new Vector3f();
		
		Vector3f temp1 = new Vector3f(), temp2 = new Vector3f(), temp3 = new Vector3f();
		
		for(float z = from.z(); Math.abs(z - from.z()) <= Math.abs(to.z() - from.z()); z += Math.signum(to.z()))
		{
			for(float y = from.y(); Math.abs(y - from.y()) <= Math.abs(to.y() - from.y()); y += Math.signum(to.y()))
			{
				for(float x = from.x(); Math.abs(x - from.x()) <= Math.abs(to.x() - from.x()); x += Math.signum(to.x()))
				{
					Vector3i coords = s.worldCoordsToStructureCoords(x, y, z);
					
					if(s.withinBlocks(coords.x, coords.y, coords.z))
					{
						Block b = s.block(coords.x, coords.y, coords.z);
						
						PhysicsShape sh = new CubeShape();
						
						for(int i = 0; i < sh.sides().length; i+=3)
						{
							int xx = Maths.floor(x), yy = Maths.floor(y), zz = Maths.floor(z);
							
							temp1.set(xx + sh.sides()[i].x(), yy + sh.sides()[i].y(), zz + sh.sides()[i].z());
							temp2.set(xx + sh.sides()[i+1].x(), yy + sh.sides()[i].y(), zz + sh.sides()[i+1].z());
							temp3.set(xx + sh.sides()[i+2].x(), yy + sh.sides()[i].y(), zz + sh.sides()[i+2].z());
							
							if(Intersectionf.intersectLineSegmentTriangle(from, to, 
									temp1, temp2, temp3, 
									(float) 1E-9, intersectionPoint))
							{
								Utils.println(intersectionPoint);
								
								return new Vector3f(coords.x, coords.y, coords.z);
//								return intersectionPoint.add(s.width() / 2.0f, s.height() / 2.0f, s.length() / 2.0f);
							}
						}
					}
				}
			}
		}
		
//		Vector3f delta = new Vector3f(to.x() - from.x(), to.y() - from.y(), to.z() - from.z());
//		delta.normalize(1);
//
//		float dist = Maths.sqrt(Maths.distSqrd(from, to));
//		
////		Vector3f slope = new Vector3f(delta).div(dist);
//		
//		Vector3f pos = new Vector3f(from);
//		
//		for(int totalDist = 0; totalDist <= dist; totalDist++)
//		{
//			int x = Maths.round(s.width() / 2.0f + pos.x() - 0.5f);
//			int y = Maths.round(s.height() / 2.0f + pos.y() - 0.5f);
//			int z = Maths.round(s.length() / 2.0f + pos.z() - 0.5f);
//
//			if(solidAt(pos.x, pos.y, pos.z))
//			{
//				return new Vector3f(x, y, z);
//			}
//			
//			pos.add(delta);
//		}
		
		return null;
	}
}

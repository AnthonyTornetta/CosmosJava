package test.physx;

import org.joml.Intersectionf;
import org.joml.Vector3f;
import org.joml.Vector3fc;

import com.cornchipss.utils.Maths;

import test.Structure;

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
		Intersectionf.intersectLineSegmentTriangle(from, to, 
				v0, v1, v2, (float) 1E-9, intersectionPoint);
		
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

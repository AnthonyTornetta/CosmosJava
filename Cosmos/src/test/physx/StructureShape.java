package test.physx;

import java.util.LinkedList;
import java.util.List;

import org.joml.Intersectionf;
import org.joml.Vector3f;
import org.joml.Vector3fc;
import org.joml.Vector3i;
import org.joml.Vector4f;

import com.cornchipss.utils.Maths;
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
		Vector4f temp4 = new Vector4f();
		
		List<Vector3f> hitPositions = new LinkedList<>();
		List<Vector3f> hits = new LinkedList<>();
		List<BlockFace> faces = new LinkedList<>();
		
		for(int z = 0; z < s.length(); z++)
		{
			for(int y = 0; y < s.height(); y++)
			{
				for(int x = 0; x < s.width(); x++)
				{
					if(s.withinBlocks(x, y, z))
					{
						Block b = s.block(x, y, z);
						
						if(b != null)
						{
							PhysicsShape sh = new CubeShape();
							
							for(int i = 0; i < sh.sides().length; i+=3)
							{
								BlockFace face = sh.faces()[i/3];
								
								temp1.set(x + sh.sides()[i].x(), y + sh.sides()[i].y(), z + sh.sides()[i].z());
								temp2.set(x + sh.sides()[i+1].x(), y + sh.sides()[i+1].y(), z + sh.sides()[i+1].z());
								temp3.set(x + sh.sides()[i+2].x(), y + sh.sides()[i+2].y(), z + sh.sides()[i+2].z());
								
								temp4.set(temp1.x, temp1.y, temp1.z, 1);
								s.transformMatrix().transform(temp4);
								temp1.set(temp4.x, temp4.y, temp4.z);
								
								temp4.set(temp2.x, temp2.y, temp2.z, 1);
								s.transformMatrix().transform(temp4);
								temp2.set(temp4.x, temp4.y, temp4.z);
								
								temp4.set(temp3.x, temp3.y, temp3.z, 1);
								s.transformMatrix().transform(temp4);
								temp3.set(temp4.x, temp4.y, temp4.z);
								
								if(Intersectionf.intersectLineSegmentTriangle(from, to, 
										temp1, temp2, temp3, 
										(float) 1E-9, intersectionPoint))
								{
									faces.add(face);
									hits.add(new Vector3f(x, y, z));
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

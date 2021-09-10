package com.cornchipss.cosmos.physx.shapes;

import java.util.LinkedList;
import java.util.List;

import org.joml.Intersectionf;
import org.joml.Vector3f;
import org.joml.Vector3fc;
import org.joml.Vector3i;
import org.joml.Vector3ic;

import com.cornchipss.cosmos.blocks.Block;
import com.cornchipss.cosmos.blocks.BlockFace;
import com.cornchipss.cosmos.physx.Orientation;
import com.cornchipss.cosmos.physx.RayResult;
import com.cornchipss.cosmos.structures.Structure;
import com.cornchipss.cosmos.utils.Maths;

public class StructureShape implements PhysicsShape
{
	private Structure s;
	
	public StructureShape(Structure s)
	{
		this.s = s;
	}
	
	/**
	 * Sends a raycast from a given point A to point B relative to world coordinates.
	 * @param from Point A
	 * @param to Point B
	 * @return A RayResult where the first point is marked as the closest.
	 */
	public RayResult raycast(Vector3fc from, Vector3fc to)
	{
		Vector3f intersectionPoint = new Vector3f();
		
		// Used  for checking the triangles of each cube face
		Vector3f temp1 = new Vector3f(), temp2 = new Vector3f(), temp3 = new Vector3f();
		
		List<Vector3f> hitPositions = new LinkedList<>();
		List<Vector3f> hits = new LinkedList<>();
		List<BlockFace> faces = new LinkedList<>();
		
		Vector3i coordsNeg = s.worldCoordsToStructureCoords(from);
		Vector3i coordsPos = s.worldCoordsToStructureCoords(to);
		
		if(coordsNeg.x > coordsPos.x)
		{
			int temp = coordsNeg.x;
			coordsNeg.x = coordsPos.x;
			coordsPos.x = temp;
		}
		
		if(coordsNeg.y > coordsPos.y)
		{
			int temp = coordsNeg.y;
			coordsNeg.y = coordsPos.y;
			coordsPos.y = temp;
		}
		
		if(coordsNeg.z > coordsPos.z)
		{
			int temp = coordsNeg.z;
			coordsNeg.z = coordsPos.z;
			coordsPos.z = temp;
		}
		
		for(int z = Maths.max(coordsNeg.z, 0); z <= Maths.min(s.length() - 1, coordsPos.z); z++)
		{
			for(int y = Maths.max(coordsNeg.y, 0); y <= Maths.min(s.height() - 1, coordsPos.y); y++)
			{
				for(int x = Maths.max(coordsNeg.x, 0); x <= Maths.min(s.width() - 1, coordsPos.x); x++)
				{
					Block b = s.block(x, y, z);
					
					if(b != null)
					{
						CubeShape sh = new CubeShape();
						
						for(int i = 0; i < sh.sides().length; i+=3)
						{
							BlockFace face = sh.faces()[i/3];
							
							temp1.set(x + sh.sides()[i].x(), y + sh.sides()[i].y(), z + sh.sides()[i].z());
							temp2.set(x + sh.sides()[i+1].x(), y + sh.sides()[i+1].y(), z + sh.sides()[i+1].z());
							temp3.set(x + sh.sides()[i+2].x(), y + sh.sides()[i+2].y(), z + sh.sides()[i+2].z());
							
							s.localCoordsToWorldCoords(temp1, temp1);
							s.localCoordsToWorldCoords(temp2, temp2);
							s.localCoordsToWorldCoords(temp3, temp3);
							
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
		
		RayResult res = new RayResult(from, to, s, hits, faces);
		
		return res;
	}

	@Override
	public boolean pointIntersects(Vector3fc point, Vector3fc position, Orientation orientation)
	{
		Vector3f delta = new Vector3f(point.x() - position.x(), point.y() - position.y(), point.z() - position.z());
		
		orientation.applyInverseRotation(delta, delta);
		
		delta.add(position);
		
		Vector3i v = s.worldCoordsToStructureCoords(delta.x, delta.y, delta.z);
		
		if(s.hasBlock(v.x, v.y, v.z))
		{
			Block b = s.block(v.x, v.y, v.z);
			
			Vector3f aaa = s.localCoordsToWorldCoords(v.x, v.y, v.z);
			
			return b.shape().pointIntersects(delta, 
					aaa, new Orientation());
		}
		
		return false;
	}

	private boolean check(Vector3fc start, Vector3fc end, Vector3ic localCoords, Orientation or, Vector3f res)
	{
		if(s.hasBlock(localCoords.x(), localCoords.y(), localCoords.z()))
		{
			Block b = s.block(localCoords.x(), localCoords.y(), localCoords.z());
			if(b.shape().lineIntersects(start, end, 
					s.localCoordsToWorldCoords(localCoords, new Vector3f()), new Orientation(), 
					res))
			{
				or.applyRotation(res, res);
				return true;
			}
		}
		
		return false;
	}
	
	@Override
	public boolean lineIntersects(Vector3fc lineStart, Vector3fc lineEnd, 
			Vector3fc position, Orientation orientation, Vector3f res)
	{		
		Vector3f lineStartMod = new Vector3f(lineStart);
		Vector3f lineEndMod = new Vector3f(lineEnd);
		
		orientation.applyInverseRotation(lineStartMod, lineStartMod);
		orientation.applyInverseRotation(lineEndMod, lineEndMod);
		
		Vector3f delta = lineEndMod.sub(lineStartMod, new Vector3f());
		
		float totalDist = Maths.sqrt(delta.dot(delta));
		
		delta = Maths.safeNormalize(delta, 1);
		
		Vector3f pos = new Vector3f(lineStartMod);
		
		Vector3i localCoords = s.worldCoordsToStructureCoords(pos);
		
		if(check(pos, lineEndMod, localCoords, orientation, res))
			return true;
		
		float dist = 1;
		
		while(dist < totalDist)
		{
			pos.add(delta);
			
			localCoords = s.worldCoordsToStructureCoords(pos);
			if(check(pos, lineEndMod, localCoords, orientation, res))
				return true;
			
			dist++;
		}
		
		localCoords = s.worldCoordsToStructureCoords(lineEndMod);
		if(check(lineStartMod, lineEndMod, localCoords, orientation, res))
			return true;
		
		return false;
	}
}

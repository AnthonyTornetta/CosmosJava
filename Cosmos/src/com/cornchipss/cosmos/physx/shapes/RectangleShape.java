package com.cornchipss.cosmos.physx.shapes;

import org.joml.Intersectionf;
import org.joml.Vector3f;
import org.joml.Vector3fc;

import com.cornchipss.cosmos.physx.Orientation;
import com.cornchipss.cosmos.utils.Maths;

public class RectangleShape implements PhysicsShape
{
	private final float w, h, l;
	
	public static final float EPSILON = 1E-7f;
	
	private static final Vector3fc[] sides = new Vector3fc[]
			{
					// Back
					new Vector3f(-0.5f, -0.5f, -0.5f),
					new Vector3f(0.5f, -0.5f, -0.5f),
					new Vector3f(0.5f, 0.5f, -0.5f),
					
					new Vector3f(0.5f, 0.5f, -0.5f),
					new Vector3f(-0.5f, 0.5f, -0.5f),
					new Vector3f(-0.5f, -0.5f, -0.5f),
					
					// Front
					new Vector3f(-0.5f, -0.5f, 0.5f),
					new Vector3f(0.5f, -0.5f, 0.5f),
					new Vector3f(0.5f, 0.5f, 0.5f),
					
					new Vector3f(0.5f, 0.5f, 0.5f),
					new Vector3f(-0.5f, 0.5f, 0.5f),
					new Vector3f(-0.5f, -0.5f, 0.5f),
					
					// Left
					new Vector3f(-0.5f, -0.5f, -0.5f),
					new Vector3f(-0.5f, 0.5f, -0.5f),
					new Vector3f(-0.5f, 0.5f, 0.5f),
					
					new Vector3f(-0.5f, 0.5f, 0.5f),
					new Vector3f(-0.5f, -0.5f, 0.5f),
					new Vector3f(-0.5f, -0.5f, -0.5f),
					
					// Right
					new Vector3f(0.5f, -0.5f, -0.5f),
					new Vector3f(0.5f, 0.5f, -0.5f),
					new Vector3f(0.5f, 0.5f, 0.5f),
					
					new Vector3f(0.5f, 0.5f, 0.5f),
					new Vector3f(0.5f, -0.5f, 0.5f),
					new Vector3f(0.5f, -0.5f, -0.5f),
					
					// Bottom
					new Vector3f(-0.5f, -0.5f, -0.5f),
					new Vector3f(0.5f, -0.5f, -0.5f),
					new Vector3f(0.5f, -0.5f, 0.5f),
					
					new Vector3f(0.5f, -0.5f, 0.5f),
					new Vector3f(-0.5f, -0.5f, 0.5f),
					new Vector3f(-0.5f, -0.5f, -0.5f),
					
					// Top
					new Vector3f(-0.5f, 0.5f, -0.5f),
					new Vector3f(0.5f, 0.5f, -0.5f),
					new Vector3f(0.5f, 0.5f, 0.5f),
					
					new Vector3f(0.5f, 0.5f, 0.5f),
					new Vector3f(-0.5f, 0.5f, 0.5f),
					new Vector3f(-0.5f, 0.5f, -0.5f)
			};
	
	public RectangleShape(float w, float h, float l)
	{
		this.w = w;
		this.h = h;
		this.l = l;
	}
	
	@Override
	public boolean pointIntersects(Vector3fc point, Vector3fc position, Orientation orientation)
	{		
		Vector3f delta = new Vector3f(point.x() - position.x(), point.y() - position.y(), point.z() - position.z());
		
		orientation.applyInverseRotation(delta, delta);
		
		delta.add(position);
		
		return (delta.x() + EPSILON >= position.x() - w / 2.f && delta.x() <= position.x() + w / 2.f + EPSILON &&
				delta.y() + EPSILON >= position.y() - h / 2.f && delta.y() <= position.y() + h / 2.f + EPSILON &&
				delta.z() + EPSILON >= position.z() - l / 2.f && delta.z() <= position.z() + l / 2.f + EPSILON);
	}
	
	@Override
	public boolean lineIntersects(Vector3fc lineStart, Vector3fc lineEnd, 
			Vector3fc position, Orientation orientation, Vector3f res)
	{
		Vector3f delta = new Vector3f(w, h, l);
		orientation.applyRotation(delta, delta);
		
		float bestDist = -1;
		Vector3f temp = new Vector3f();
		
		for(int i = 0; i < sides.length; i+=3)
		{		
			Vector3f vert1 = new Vector3f(position.x() + delta.x * sides[i].x(), position.y() + delta.y * sides[i].y(), position.z() + delta.z * sides[i].z());
			Vector3f vert2 = new Vector3f(position.x() + delta.x * sides[i+1].x(), position.y() + delta.y * sides[i+1].y(), position.z() + delta.z * sides[i+1].z());
			Vector3f vert3 = new Vector3f(position.x() + delta.x * sides[i+2].x(), position.y() + delta.y * sides[i+2].y(), position.z() + delta.z * sides[i+2].z());
			
			if(Intersectionf.intersectLineSegmentTriangle(lineStart, lineEnd, 
					vert1, vert2, vert3, 
					(float) 1E-9, temp))
			{
				float dist = Maths.distSqrd(temp, lineStart);
				if(bestDist == -1 || dist < bestDist)
				{
					bestDist = dist;
					res.set(temp);
				}
			}
		}
		
		return bestDist != -1;
	}
}

package com.cornchipss.world.blocks;

import org.joml.Vector3f;
import org.joml.Vector3fc;

import com.cornchipss.utils.Maths;

public enum BlockFace
{
	FRONT(0), 
	BACK(1),
	TOP(2),
	BOTTOM(3),
	RIGHT(4),
	LEFT(5),
	MAX(5),
	UNKNOWN(-1);
	
	private int f;
	
	private BlockFace(int f)
	{
		this.f = f;
	}
	
	public int getValue()
	{
		return f;
	}

	public static BlockFace fromFaceIndex(int i)
	{
		switch(i)
		{
		case 0:
			return FRONT;
		case 1:
			return BACK;
		case 2:
			return TOP;
		case 3:
			return BOTTOM;
		case 4:
			return RIGHT;
		case 5:
			return LEFT;
		default:
			return UNKNOWN;
		}
	}

	public Vector3f getRelativePosition()
	{
		switch(this)
		{
		case FRONT:
			return new Vector3f(0, 0, .5f);
		case BACK:
			return new Vector3f(0, 0, -.5f);
		case TOP:
			return new Vector3f(0, .5f, 0);
		case BOTTOM:
			return new Vector3f(0, -.5f, 0);
		case RIGHT:
			return new Vector3f(.5f, 0, 0);
		case MAX:
		case LEFT:
			return new Vector3f(-.5f, 0, 0);
		default:
			return new Vector3f(0, 0, 0);
		}
	}
	
	public Vector3f getDirection()
	{
		return Maths.mul(2, getRelativePosition());
	}

	public static BlockFace getClosestFace(Vector3fc start, Vector3fc block)
	{
		BlockFace closest = fromFaceIndex(0);
		float dist = Maths.add(block, closest.getRelativePosition()).distanceSquared(start);
		
		for(int i = 1; i <= BlockFace.MAX.f; i++)
		{
			BlockFace f = fromFaceIndex(i);
			
			float distTemp = Maths.add(block, f.getRelativePosition()).distanceSquared(start);
			
			if(distTemp < dist)
			{
				dist = distTemp;
				closest = f;
			}
		}
		
		return closest;
	}
}

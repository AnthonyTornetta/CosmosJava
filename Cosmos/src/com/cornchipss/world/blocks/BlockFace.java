package com.cornchipss.world.blocks;

public enum BlockFace
{
	FRONT(0), 
	BACK(1),
	TOP(2),
	BOTTOM(3),
	RIGHT(4),
	LEFT(5),
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
}

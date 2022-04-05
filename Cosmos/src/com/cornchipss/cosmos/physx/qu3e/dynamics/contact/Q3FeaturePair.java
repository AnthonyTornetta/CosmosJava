package com.cornchipss.cosmos.physx.qu3e.dynamics.contact;

public class Q3FeaturePair
{
	int key;
	
//	private int bitExtracted(int k, int p)
//    {
//        return (((1 << k) - 1) & (key >> (p - 1)));
//    }
//	
	public int inR()
	{
//		return bitExtracted(32 - 8 - 1, 8);
		return (key & 0xFF000000) >> 24;
	}
	
	public int outR()
	{
//		return bitExtracted(32 - 16 - 1, 8);
		return (key & 0xFF0000) >> 16;
	}
	
	public int inI()
	{
//		return bitExtracted(32 - 24 - 1, 8);
		
		return (key & 0xFF00) >> 8;
	}
	
	public int outI()
	{
		return key & 0xFF;
	}
	
	public int key()
	{
		return key;
	}
}

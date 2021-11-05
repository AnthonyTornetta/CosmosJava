package com.cornchipss.cosmos.netty;

public enum NettySide
{
	CLIENT, SERVER;

	private static NettySide side;

	public static void initNettySide(NettySide s)
	{
		side = s;
	}

	public static NettySide side()
	{
		return side;
	}
}

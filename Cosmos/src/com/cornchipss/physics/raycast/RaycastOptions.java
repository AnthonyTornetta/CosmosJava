package com.cornchipss.physics.raycast;

import com.cornchipss.world.blocks.Block;

public class RaycastOptions
{
	Block[] blacklist;
	Block[] whitelist;
	
	public void setWhitelist(Block... blocks)
	{
		whitelist = blocks;
	}
	
	public void setBlacklist(Block... blocks)
	{
		blacklist = blocks;
	}
	
	public Block[] getBlacklist() { return blacklist; }
	public Block[] getWhitelist() { return whitelist; }
}

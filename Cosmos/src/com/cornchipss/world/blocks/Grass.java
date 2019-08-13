package com.cornchipss.world.blocks;

import com.cornchipss.utils.Utils;
import com.cornchipss.world.Block;

public class Grass extends Block
{
	@Override
	public int getTexture(int face)
	{
		if(face == Utils.TOP)
			return 1;
		if(face == Utils.BOTTOM)
			return 3;
		else
			return 4;
	}
}

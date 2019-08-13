package com.cornchipss.world.blocks;

import com.cornchipss.rendering.Model;
import com.cornchipss.rendering.ModelCreator;
import com.cornchipss.world.Block;

public class Air extends Block
{
	public Air() 
	{
		setOpaque(false);
	}
	
	@Override
	public Model createModel(ModelCreator mc)
	{
		return null;
	}
}

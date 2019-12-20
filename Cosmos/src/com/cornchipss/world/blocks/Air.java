package com.cornchipss.world.blocks;

import com.cornchipss.rendering.Model;
import com.cornchipss.rendering.ModelCreator;

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
	
	@Override
	public boolean isInteractable()
	{
		return false;
	}

	@Override
	public float getMass()
	{
		return 0; // I know air doesn't have 0 mass, but shhh
	}
}

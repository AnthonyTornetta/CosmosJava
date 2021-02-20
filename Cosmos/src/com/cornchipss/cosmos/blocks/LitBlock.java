package com.cornchipss.cosmos.blocks;

import javax.annotation.Nonnull;

import com.cornchipss.cosmos.lights.LightSource;
import com.cornchipss.cosmos.models.CubeModel;

public class LitBlock extends Block
{
	private LightSource source;
	
	public LitBlock(CubeModel m, @Nonnull LightSource src)
	{
		super(m);
		
		source = src;
	}
	
	public LightSource lightSource()
	{
		return source;
	}
}

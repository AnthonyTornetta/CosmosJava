package com.cornchipss.cosmos.blocks;

import javax.annotation.Nonnull;

import com.cornchipss.cosmos.lights.LightSource;
import com.cornchipss.cosmos.models.CubeModel;

/**
 * A block that emits light
 */
public class LitBlock extends Block
{
	private LightSource source;
	
	/**
	 * A block that emits light
	 * @param m The model to use
	 * @param src The {@link LightSource} the block emits
	 */
	public LitBlock(CubeModel m, @Nonnull LightSource src, String name, int mass)
	{
		super(m, name, mass);
		
		source = src;
	}
	
	public LightSource lightSource()
	{
		return new LightSource(16, 0.5f, 0.5f, 1.0f);
	}
}

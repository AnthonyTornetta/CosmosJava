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
	 * 
	 * @param m         The model to use
	 * @param src       The {@link LightSource} the block emits
	 * @param name      Name of the block
	 * @param mass      The mass
	 * @param maxDamage The max damage this can take
	 */
	public LitBlock(CubeModel m, @Nonnull LightSource src, String name,
		int mass, float maxDamage)
	{
		super(m, name, mass, maxDamage);

		source = src;
	}

	public LightSource lightSource()
	{
		return source;
	}
}

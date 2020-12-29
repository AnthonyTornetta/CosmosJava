package test.blocks;

import javax.annotation.Nonnull;

import test.lights.LightSource;
import test.models.CubeModel;

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

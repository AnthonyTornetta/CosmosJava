package test.models;

public class LightModel extends CubeModel
{
	@Override
	public float u(BlockSide side)
	{
		return 0;
	}

	@Override
	public float v(BlockSide side)
	{
		return CubeModel.TEXTURE_DIMENSIONS;
	}
}

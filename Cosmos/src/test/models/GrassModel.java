package test.models;

public class GrassModel extends CubeModel
{
	@Override
	public float u(BlockSide side)
	{
		switch(side)
		{
		case TOP:
			return 1 * CubeModel.TEXTURE_DIMENSIONS;
		case BOTTOM:
			return 3 * CubeModel.TEXTURE_DIMENSIONS;
		default:
			return 4 * CubeModel.TEXTURE_DIMENSIONS;
		}
	}

	@Override
	public float v(BlockSide side)
	{
		return 0;
	}
}

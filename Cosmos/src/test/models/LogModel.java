package test.models;

public class LogModel extends CubeModel
{
	@Override
	public float u(BlockSide side)
	{
		switch(side)
		{
		case TOP:
			return 1 * CubeModel.TEXTURE_DIMENSIONS;
		case BOTTOM:
			return 1 * CubeModel.TEXTURE_DIMENSIONS;
		default:
			return 2 * CubeModel.TEXTURE_DIMENSIONS;
		}
	}

	@Override
	public float v(BlockSide side)
	{
		return CubeModel.TEXTURE_DIMENSIONS;
	}
}

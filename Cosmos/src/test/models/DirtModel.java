package test.models;

public class DirtModel extends CubeModel
{
	@Override
	public float u(BlockSide side)
	{
		return CubeModel.TEXTURE_DIMENSIONS * 3;
	}

	@Override
	public float v(BlockSide side)
	{
		return 0;
	}

}

package test.models;

public class StoneModel extends CubeModel
{

	@Override
	public float u(BlockSide side)
	{
		return CubeModel.TEXTURE_DIMENSIONS * 2;
	}

	@Override
	public float v(BlockSide side)
	{
		return 0;
	}

}

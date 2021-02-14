package test.models;

public class LeafModel extends CubeModel
{
	@Override
	public float u(BlockSide side)
	{
		return CubeModel.TEXTURE_DIMENSIONS * 3;
	}

	@Override
	public float v(BlockSide side)
	{
		return CubeModel.TEXTURE_DIMENSIONS;
	}
}

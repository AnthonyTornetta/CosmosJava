package test.models;

public abstract class CubeModel
{
	public static final float TEXTURE_DIMENSIONS = 16.0f / 256.0f;
	
	public static int[] sideIndicies = new int[]
			{
					0, 1, 2,
					2, 3, 0
			};
	
	public abstract float u(BlockSide side);
	public abstract float v(BlockSide side);
	
	public int[] indicies(BlockSide side)
	{
		return sideIndicies;
	}
	
	public boolean opaque()
	{
		return true;
	}
}

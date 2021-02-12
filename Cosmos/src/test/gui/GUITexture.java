package test.gui;

import test.Mesh;
import test.Vec3;

public class GUITexture extends GUIElement
{
	private static final float UV_WIDTH = 0.5f;
	private static final float UV_HEIGHT = 0.5f;
	
	public static final int[] indices = new int[]
			{
				0, 1, 3,
				1, 2, 3
			};
	
	public static float[] makeVerts(float w, float h)
	{
		return new float[]
			{
				 w/2,  h/2, -1.0f,  // top right
				 w/2, -h/2, -1.0f,  // bottom right
			    -w/2, -h/2, -1.0f,  // bottom left
			    -w/2,  h/2, -1.0f   // top left 
			};
	}
	
	public static float[] makeUVs(float u, float v)
	{
		return new float[]
			{
				u + UV_WIDTH, v,
				u + UV_WIDTH, v + UV_HEIGHT,
				u, v + UV_HEIGHT,
				u, v
			};
	}
	
	private Mesh guiMesh;

	public GUITexture(Vec3 position, float w, float h, float u, float v)
	{
		super(position);
		guiMesh = Mesh.createMesh(makeVerts(w, h), indices, makeUVs(u, v));
	}
	
	@Override
	public Mesh guiMesh()
	{
		return guiMesh;
	}
}

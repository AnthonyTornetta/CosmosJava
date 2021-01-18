package test.gui;

import org.joml.Matrix4f;
import org.joml.Matrix4fc;

import test.Mesh;
import test.Vec3;

public class GUIElement
{
	private Mesh guiMesh;
	private Vec3 position;
	
	private static final float UV_WIDTH = 1.0f;
	private static final float UV_HEIGHT = 1.0f;
	
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
	
	public GUIElement(Vec3 position, float w, float h, float u, float v)
	{
		this.position = position;
		guiMesh = Mesh.createMesh(makeVerts(w, h), indices, makeUVs(u, v));
	}
	
	public Vec3 position()
	{
		return position;
	}
	
	public void position(Vec3 position)
	{
		this.position = position;
	}
	
	public Mesh guiMesh()
	{
		return guiMesh;
	}

	public Matrix4fc transform()
	{
		return new Matrix4f().identity().translate(position.x(), position.y(), position.z());
	}
}

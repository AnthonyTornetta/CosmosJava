package test.gui;

import org.joml.Matrix4f;
import org.joml.Matrix4fc;

import com.cornchipss.utils.Maths;

import test.Mesh;
import test.Vec3;
import test.models.BlockSide;
import test.models.CubeModel;

public class GUIModel
{
	private Mesh mesh;
	private Matrix4f transform;
	
	public GUIModel(Vec3 position, float scale, CubeModel model)
	{
		this(Maths.createTransformationMatrix(position, 0, 0, 0, scale), model);
	}
	
	public GUIModel(Matrix4f transform, CubeModel m)
	{
		this(transform, m.createMesh(0, 0, 0, 1.0f, BlockSide.FRONT));
	}
	
	public GUIModel(Vec3 position, float scale, Mesh m)
	{
		this(Maths.createTransformationMatrix(position, 0, 0, 0, scale), m);
	}
	
	public GUIModel(Matrix4f transform, Mesh m)
	{
		this.transform = transform;
		this.mesh = m;
	}
	
	public void prepare()
	{
		mesh.prepare();
	}
	
	public void draw()
	{
		mesh.draw();
	}
	
	public void finish()
	{
		mesh.finish();
	}

	public Matrix4fc transform()
	{
		return transform;
	}
}

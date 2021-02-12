package test.gui;

import org.joml.Matrix4f;

import com.cornchipss.rendering.Texture;
import com.cornchipss.utils.Maths;

import test.Mesh;
import test.Vec3;
import test.models.BlockSide;
import test.models.CubeModel;

public class GUIModel extends GUIElement
{
	private Mesh mesh;
	private Texture map;
	
	public GUIModel(Vec3 position, float scale, CubeModel model, Texture map)
	{
		this(Maths.createTransformationMatrix(position, 0, 0, 0, scale), model, map);
	}
	
	public GUIModel(Matrix4f transform, CubeModel m, Texture map)
	{
		this(transform, m.createMesh(0, 0, 0, 1.0f, BlockSide.FRONT), map);
	}
	
	public GUIModel(Vec3 position, float scale, Mesh m, Texture map)
	{
		this(Maths.createTransformationMatrix(position, 0, 0, 0, scale), m, map);
	}
	
	public GUIModel(Matrix4f transform, Mesh m, Texture map)
	{
		super(transform);
		this.mesh = m;
		this.map = map;
	}
	
	@Override
	public Mesh guiMesh()
	{
		return mesh;
	}
	
	@Override
	public void prepare(GUI gui)
	{
		map.bind();
		super.prepare(gui);
	}
	
	@Override
	public void finish(GUI gui)
	{
		super.finish(gui);
		gui.texture().bind();
	}
}

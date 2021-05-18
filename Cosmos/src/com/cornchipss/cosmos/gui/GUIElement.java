package com.cornchipss.cosmos.gui;

import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.joml.Vector3f;

import com.cornchipss.cosmos.gui.measurement.MeasurementPair;
import com.cornchipss.cosmos.material.Material;
import com.cornchipss.cosmos.material.Materials;
import com.cornchipss.cosmos.rendering.Mesh;
import com.cornchipss.cosmos.rendering.Window;
import com.cornchipss.cosmos.utils.Maths;

public abstract class GUIElement
{
	protected Matrix4f transform;
	private MeasurementPair position;
	private Vector3f rotation;
	private float scale;
	
	private Vector3f positionVector;
	
	protected void createMatrix()
	{
		positionVector.set(position.x().actualValue(Window.instance().getWidth()),
				position.y().actualValue(Window.instance().getHeight()),
				0);
		
		Maths.createTransformationMatrix(positionVector, rotation.x, rotation.y, rotation.z, scale, transform);
	}
	
	public GUIElement(MeasurementPair position, float rx, float ry, float rz, float scale)
	{
		this.position = position;
		this.rotation = new Vector3f(rx, ry, rz);
		this.scale = scale;
		transform = new Matrix4f();
		positionVector = new Vector3f();
		
		createMatrix();
	}
	
	public void onResize(float w, float h)
	{
		createMatrix();
	}
	
	public GUIElement(MeasurementPair position, float scale)
	{
		this(position, 0, 0, 0, 1);
	}
	
	public GUIElement(MeasurementPair position)
	{
		this(position, 1);
	}
	
	public void prepare(GUI gui)
	{
		guiMesh().prepare();
	}
	
	public void draw(GUI gui)
	{
		guiMesh().draw();
	}
	
	public void finish(GUI gui)
	{
		guiMesh().finish();
	}
	
	public abstract Mesh guiMesh();

	public Matrix4fc transform()
	{
		return transform;
	}
	
	public void delete()
	{
		guiMesh().delete();
	}

	public Material material()
	{
		return Materials.GUI_MATERIAL;
	}
	
	public MeasurementPair position()
	{
		return position;
	}
	
	public void position(MeasurementPair position)
	{
		this.position = position;
		createMatrix();
	}
}

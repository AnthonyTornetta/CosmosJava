package com.cornchipss.cosmos.gui;

import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.joml.Vector3f;
import org.joml.Vector3fc;

import com.cornchipss.cosmos.material.Material;
import com.cornchipss.cosmos.material.Materials;
import com.cornchipss.cosmos.rendering.Mesh;
import com.cornchipss.cosmos.utils.Maths;

public abstract class GUIElement
{
	protected Matrix4f transform;
	private Vector3f position;
	private Vector3f rotation;
	private float scale;
	
	private void createMatrix()
	{
		Maths.createTransformationMatrix(position, rotation.x, rotation.y, rotation.z, scale, transform);
	}
	
	public GUIElement(Vector3fc position, float rx, float ry, float rz, float scale)
	{
		this.position = new Vector3f().set(position);
		this.rotation = new Vector3f(rx, ry, rz);
		this.scale = scale;
		transform = new Matrix4f();
		
		createMatrix();
	}
	
	public GUIElement(Vector3fc position, float scale)
	{
		this(position, 0, 0, 0, 1);
	}
	
	public GUIElement(Vector3fc position)
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
	
	public Vector3fc position()
	{
		return position;
	}
	
	public void position(Vector3fc p)
	{
		this.position.set(p);
		createMatrix();
	}
}

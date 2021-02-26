package com.cornchipss.cosmos.physx;

import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.joml.Quaternionf;
import org.joml.Quaternionfc;
import org.joml.Vector3f;
import org.joml.Vector3fc;

import com.cornchipss.cosmos.utils.Maths;

public class Transform
{
	private Vector3f position;
	private Quaternionf rotation;
	
	private Matrix4f transMatrix;
	private Matrix4f invertedMatirx;
	
	public Transform()
	{
		this(0, 0, 0);
	}

	public Transform(float x, float y, float z)
	{
		position = new Vector3f(x, y, z);
		rotation = Maths.blankQuaternion();
		
		transMatrix = new Matrix4f();
		invertedMatirx = new Matrix4f();
		
		updateMatrix();
	}
	
	private void updateMatrix()
	{
		transMatrix.identity();
		transMatrix.translate(position);
		transMatrix.rotate(rotation);
		
		transMatrix.invert(invertedMatirx);
	}
	
	public Transform(Vector3fc pos)
	{
		this(pos.x(), pos.y(), pos.z());
	}
	
	public void position(Vector3fc p)
	{
		position.set(p);
		updateMatrix();
	}
	
	public Vector3fc position()
	{
		return position;
	}
	
	public Quaternionfc rotation()
	{
		return rotation;
	}

	public Matrix4fc matrix()
	{
		return transMatrix;
	}

	public Matrix4f invertedMatrix()
	{
		return invertedMatirx;
	}
}

package com.cornchipss.cosmos.physx;

import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.joml.Vector3f;
import org.joml.Vector3fc;

public class Transform
{
	private Vector3f position;

	private Matrix4f transMatrix;
	private Matrix4f invertedMatirx;

	private Orientation orientation;

	public Transform()
	{
		this(0, 0, 0);
	}

	public Transform(float x, float y, float z)
	{
		position = new Vector3f(x, y, z);
		orientation = new Orientation();

		transMatrix = new Matrix4f();
		invertedMatirx = new Matrix4f();

		updateMatrix();
	}

	private void updateMatrix()
	{
		transMatrix.identity();

		transMatrix.translate(position);

		orientation.applyRotation(transMatrix);

		transMatrix.invert(invertedMatirx);
	}

	public void rotateRelative(Vector3fc dRot)
	{
		rotateRelative(dRot, right(), up(), forward());
	}

	public void rotateRelative(Vector3fc dRot, Vector3fc right, Vector3fc up, Vector3fc forward)
	{
		orientation.rotateRelative(dRot, right, up, forward);
		updateMatrix();
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

	public Orientation orientation()
	{
		return orientation;
	}

	public void orientation(Orientation o)
	{
		this.orientation = o.clone();
		updateMatrix();
	}

	public Matrix4fc matrix()
	{
		return transMatrix;
	}

	public Matrix4f invertedMatrix()
	{
		return invertedMatirx;
	}

	public Vector3fc forward()
	{
		return orientation.forward();
	}

	public Vector3fc right()
	{
		return orientation.right();
	}

	public Vector3fc up()
	{
		return orientation.up();
	}
}

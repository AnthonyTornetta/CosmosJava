package com.cornchipss.physics;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector3fc;
import org.joml.Vector4f;

import com.cornchipss.utils.Maths;
import com.cornchipss.utils.Utils;

public class Transform
{
	private Matrix4f rotationX = new Matrix4f().identity(), rotationY = new Matrix4f().identity(), rotationZ = new Matrix4f().identity();
	
	private Vector3f position;
	private Vector3fc rotation;
	
	private Matrix4f transform = new Matrix4f();
	
	public Transform()
	{
		this(Maths.zero(), Maths.zero());
	}
	
	public Transform(Vector3fc position)
	{
		this(position, Maths.zero());
	}
	
	public Transform(Vector3fc position, Vector3fc rotation)
	{
		transform = new Matrix4f();
		
		setPosition(new Vector3f(position));
		setRotation(rotation);
	}
	
	public Vector3f getPosition() { return position; }
	public void setPosition(Vector3fc position)
	{
		this.position = new Vector3f(position);
		transform.transform(new Vector4f(position, 0));
	}

	public void setX(float x) { position.x = x; }
	public void setY(float y) { position.y = y; }
	public void setZ(float z) { position.z = z; }
	
	public float getX() { return position.x; }
	public float getY() { return position.y; }
	public float getZ() { return position.z; }
	
	public void translate(Vector3fc amt)
	{
		setX(amt.x() + getX());
		setY(amt.y() + getY());
		setZ(amt.z() + getZ());
	}
	
	public Vector3fc getRotation() { return rotation; }
	public void setRotation(Vector3fc rotation)
	{
		setRotation(rotation.x(), rotation.y(), rotation.z());
	}
	public void setRotation(float rx, float ry, float rz)
	{
		/*
		 * https://open.gl/transformations
		 */
		
		if(this.rotation == null || rx != this.rotation.x())
			rotationX = Maths.createRotationMatrix(Utils.x(), rx);
		if(this.rotation == null || ry != this.rotation.y())
			rotationY = Maths.createRotationMatrix(Utils.y(), ry);
		if(this.rotation == null || rz != this.rotation.z())
			rotationZ = Maths.createRotationMatrix(Utils.z(), rz);
		
		this.rotation = new Vector3f(rx, ry, rz);	
	}
	
	public void rotate(float rx, float ry, float rz)
	{
		setRotation(Maths.add(rotation, rx, ry, rz));
	}
	
	public void rotate(Vector3fc delta)
	{
		setRotation(Maths.add(rotation, delta));
	}

	public void setRotationX(float rx)
	{
		setRotation(rx, getRotationY(), getRotationZ());
	}
	
	public void setRotationY(float ry)
	{
		setRotation(getRotationX(), ry, getRotationZ());
	}
	
	public void setRotationZ(float rz)
	{
		setRotation(getRotationX(), getRotationY(), rz);
	}
	
	public float getRotationX() { return rotation.x(); }
	public float getRotationY() { return rotation.y(); }
	public float getRotationZ() { return rotation.z(); }
	
	public Matrix4f getRotationMatrixX() { return rotationX; }
	public Matrix4f getRotationMatrixY() { return rotationY; }
	public Matrix4f getRotationMatrixZ() { return rotationZ; }
	
	public Matrix4f getCombinedRotation()
	{
		return Maths.mul(rotationX, rotationY).mul(rotationZ);
	}
}

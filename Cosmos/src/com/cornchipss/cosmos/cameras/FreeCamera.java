package com.cornchipss.cosmos.cameras;

import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.joml.Vector3f;
import org.joml.Vector3fc;

import com.cornchipss.cosmos.physx.PhysicalObject;
import com.cornchipss.cosmos.physx.Transform;
import com.cornchipss.cosmos.utils.Maths;

public class FreeCamera extends Camera
{
	private PhysicalObject parent;
	
	private Transform trans;
	
	/**
	 * <p>A camera that treats every rotation as an absolute rotation.  This also suffers from the gimbal lock most first person cameras suffer from.</p>
	 * <p>See <a href="https://en.wikipedia.org/wiki/Gimbal_lock">https://en.wikipedia.org/wiki/Gimbal_lock</a></p>
	 * @param parent The parent this camera sits on
	 */
	public FreeCamera(PhysicalObject parent)
	{
		this.parent = parent;
		
		trans = new Transform();
	}
	
	@Override
	public Matrix4fc viewMatrix()
	{
		Matrix4f mat = new Matrix4f();
		Maths.createViewMatrix(position(), trans.rotation(), mat);
		return mat;
	}

	@Override
	public Vector3fc forward()
	{
		return trans.forward();
	}

	@Override
	public Vector3fc right()
	{
		return trans.right();
	}

	@Override
	public Vector3fc up()
	{
		return trans.up();
	}

	/**
	 * Sets the camera's parent
	 * @param transform The camera's new parent
	 */
	@Override
	public void parent(PhysicalObject transform)
	{
		this.parent = transform;
	}
	
	/**
	 * The camera's parent
	 * @return The camera's parent
	 */
	@Override
	public PhysicalObject parent()
	{
		return parent;
	}

	@Override
	public Vector3fc position()
	{
		return new Vector3f(parent.position()).add(new Vector3f(0, 0.4f, 0));
	}

	@Override
	public void zeroRotation()
	{
		trans.rotation(Maths.blankQuaternion());
	}

	@Override
	public void update()
	{
		
	}

	@Override
	public void rotate(Vector3fc dRot)
	{
		trans.rotateRelative(dRot);
	}
}

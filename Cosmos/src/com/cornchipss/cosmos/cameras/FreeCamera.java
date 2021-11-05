package com.cornchipss.cosmos.cameras;

import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.joml.Vector3f;
import org.joml.Vector3fc;

import com.cornchipss.cosmos.physx.Orientation;
import com.cornchipss.cosmos.physx.Transform;
import com.cornchipss.cosmos.utils.Maths;

public class FreeCamera extends Camera
{
	private Transform parent;

	private Orientation orientation;

	/**
	 * <p>
	 * A camera that treats every rotation as an absolute rotation. This also
	 * suffers from the gimbal lock most first person cameras suffer from.
	 * </p>
	 * <p>
	 * See <a href=
	 * "https://en.wikipedia.org/wiki/Gimbal_lock">https://en.wikipedia.org/wiki/Gimbal_lock</a>
	 * </p>
	 * 
	 * @param parent The parent this camera sits on
	 */
	public FreeCamera(Transform parent)
	{
		this.parent = parent;

		orientation = new Orientation();
	}

	@Override
	public Matrix4fc viewMatrix()
	{
		Matrix4f mat = new Matrix4f();
		Maths.createViewMatrix(position(), orientation.quaternion(), mat);
		return mat;
	}

	@Override
	public Vector3fc forward()
	{
		return orientation.forward();
	}

	@Override
	public Vector3fc right()
	{
		return orientation.right();
	}

	@Override
	public Vector3fc up()
	{
		return orientation.up();
	}

	/**
	 * Sets the camera's parent
	 * 
	 * @param transform The camera's new parent
	 */
	@Override
	public void parent(Transform transform)
	{
		this.parent = transform;
	}

	/**
	 * The camera's parent
	 * 
	 * @return The camera's parent
	 */
	@Override
	public Transform parent()
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
		orientation.zero();
	}

	@Override
	public void update()
	{

	}

	@Override
	public void rotate(Vector3fc dRot)
	{
		orientation.rotateRelative(dRot);
	}
}

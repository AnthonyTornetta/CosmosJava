package com.cornchipss.cosmos.cameras;

import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.joml.Vector3fc;

import com.cornchipss.cosmos.physx.Transform;
import com.cornchipss.cosmos.structures.Ship;
import com.cornchipss.cosmos.utils.Maths;

public class ShipCamera extends Camera
{
	private Ship ship;
	
	private Matrix4f view;
	
	public ShipCamera(Ship ship)
	{
		this.ship = ship;
		
		view = new Matrix4f();
		
		update();
	}
	
	@Override
	public Matrix4fc viewMatrix()
	{
		return view;
	}

	@Override
	public Vector3fc forward()
	{
		return ship.body().transform().forward();
	}

	@Override
	public Vector3fc right()
	{
		return ship.body().transform().right();
	}

	@Override
	public Vector3fc up()
	{
		return ship.body().transform().up();
	}

	@Override
	public Vector3fc position()
	{
		return ship.shipCoreWorldPosition();
	}

	@Override
	public void zeroRotation()
	{
		throw new IllegalStateException("A ship camera cannot be manually rotated.");
	}

	@Override
	public void update()
	{
		// Inverted because of OpenGL
		Maths.createViewMatrix(position(), ship.body().transform().orientation().inverseQuaternion(), view);
	}

	@Override
	public void rotate(Vector3fc dRot)
	{
		throw new IllegalStateException("A ship camera cannot be manually rotated.");
	}

	@Override
	public void parent(Transform parent)
	{
		throw new IllegalStateException("A ship camera cannot have its parent changed.");
	}

	@Override
	public Transform parent()
	{
		return ship.body().transform();
	}
}

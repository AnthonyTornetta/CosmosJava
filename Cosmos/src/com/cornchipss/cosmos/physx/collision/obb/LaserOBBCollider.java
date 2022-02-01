package com.cornchipss.cosmos.physx.collision.obb;

import java.util.Iterator;

import org.joml.Vector3f;
import org.joml.Vector3fc;

import com.cornchipss.cosmos.physx.Orientation;

public class LaserOBBCollider extends OBBCollider
{
	private static final Vector3f halfwidths = new Vector3f(0.1f, 0.1f, 10.0f);

	/**
	 * Call {@link LaserOBBCollider#set(Vector3fc, Orientation)}
	 * before use
	 */
	public LaserOBBCollider()
	{
		super();
	}

	public LaserOBBCollider(Vector3fc center, Orientation orientation)
	{
		super(center, orientation, halfwidths);
	}
	
	public LaserOBBCollider set(Vector3fc center, Orientation o)
	{
		super.set(center, o, halfwidths);
		return this;
	}

	private static class CornerIterator implements Iterator<Vector3fc>
	{
		LaserOBBCollider c;
		
		int i = 0;

		CornerIterator(LaserOBBCollider c)
		{
			this.c = c;
		}

		@Override
		public boolean hasNext()
		{
			return i == 0;
		}

		@Override
		public Vector3fc next()
		{
			i++;
			return c.center();
		}
	}

	@Override
	public Iterator<Vector3fc> iterator()
	{
		return new CornerIterator(this);
	}
}

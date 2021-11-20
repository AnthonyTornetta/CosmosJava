package com.cornchipss.cosmos.physx.collision.obb;

import java.util.Iterator;

import org.joml.Vector3f;
import org.joml.Vector3fc;

import com.cornchipss.cosmos.physx.Orientation;
import com.cornchipss.cosmos.utils.Utils;

public class OBBCollider implements Iterable<Vector3fc>
{
	private Vector3f center;
	private Vector3f[] localAxis = new Vector3f[3];
	private Vector3f halfwidths;
	private Orientation or;

	public OBBCollider(Vector3fc center, Orientation orientation, Vector3fc halfwidths)
	{
		this.center = new Vector3f(center);
		localAxis[0] = new Vector3f(orientation.right());
		localAxis[1] = new Vector3f(orientation.up());
		localAxis[2] = new Vector3f(orientation.forward());
		this.halfwidths = new Vector3f(halfwidths);
		this.or = orientation;
	}

	private static class CornerIterator implements Iterator<Vector3fc>
	{
		int z = -1, y = -1, x = -1;

		boolean first = true;

		OBBCollider obc;
		Vector3f temp = new Vector3f();

		CornerIterator(OBBCollider obc)
		{
			this.obc = obc;
		}

		@Override
		public boolean hasNext()
		{
			return x + y + z != 3;
		}

		@Override
		public Vector3fc next()
		{
			if (first)
			{
				first = false;
				return new Vector3f(obc.center);
			}

			Vector3f corner = new Vector3f(obc.center);
			corner.add(obc.localAxis[2].mul(obc.halfwidths.z * z, temp));
			corner.add(obc.localAxis[1].mul(obc.halfwidths.y * y, temp));
			corner.add(obc.localAxis[0].mul(obc.halfwidths.x * x, temp));

			if (x == -1)
				x = 1;
			else if (y == -1)
			{
				y = 1;
				x = -1;
			}
			else if (z == -1)
			{
				z = 1;
				y = -1;
				x = -1;
			}

			return corner;
		}
	}

	public Vector3fc center()
	{
		return center;
	}

	public Vector3fc[] localAxis()
	{
		return localAxis;
	}

	public Vector3fc halfwidths()
	{
		return halfwidths;
	}

	public Orientation orientation()
	{
		return or;
	}

	@Override
	public boolean equals(Object other)
	{
		if (other instanceof OBBCollider)
		{
			OBBCollider c = (OBBCollider) other;
			return c.center().equals(center()) && c.localAxis()[0].equals(localAxis()[0])
				&& c.localAxis()[1].equals(localAxis()[1]) && c.localAxis()[2].equals(localAxis()[2])
				&& c.halfwidths().equals(this.halfwidths());
		}

		return false;
	}

	@Override
	public int hashCode()
	{
		return center().hashCode() * halfwidths().hashCode() * localAxis()[0].hashCode() * localAxis()[1].hashCode()
			* localAxis()[2].hashCode();
	}

	@Override
	public Iterator<Vector3fc> iterator()
	{
		return new CornerIterator(this);
	}

	@Override
	public String toString()
	{
		return "{" + Utils.toEasyString(center) + " +/- " + Utils.toEasyString(halfwidths) + "; X: "
			+ Utils.toEasyString(localAxis[0]) + "; Y: " + Utils.toEasyString(localAxis[1]) + "; Z: "
			+ Utils.toEasyString(localAxis[2]) + "}";
	}
}

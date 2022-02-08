package com.cornchipss.cosmos.physx.collision.obb;

import java.util.Iterator;

import org.joml.Vector3f;
import org.joml.Vector3fc;

import com.cornchipss.cosmos.memory.IReusable;
import com.cornchipss.cosmos.memory.MemoryPool;
import com.cornchipss.cosmos.physx.Orientation;
import com.cornchipss.cosmos.utils.Utils;

public class OBBCollider implements Iterable<Vector3fc>, IReusable
{
	private Vector3f center;
	private Vector3f[] localAxis = new Vector3f[3];
	private Vector3f halfwidths;
	private Orientation or;

	private CornerIterable cornerIterable;

	private int cornerCountX, cornerCountY, cornerCountZ;

	/**
	 * Call {@link OBBCollider#set(Vector3fc, Orientation, Vector3fc)} before
	 * use
	 */
	public OBBCollider()
	{
	}

	public OBBCollider(Vector3fc center, Orientation orientation,
		Vector3fc halfwidths)
	{
		set(center, orientation, halfwidths);
	}

	public OBBCollider set(Vector3fc center, Orientation orientation,
		Vector3fc halfwidths)
	{
		if (this.center == null)
		{
			this.center = new Vector3f();
			localAxis[0] = new Vector3f();
			localAxis[1] = new Vector3f();
			localAxis[2] = new Vector3f();
			this.halfwidths = new Vector3f();
			
			cornerIterable = new CornerIterable(this);
		}
		
		this.center.set(center);
		localAxis[0].set(orientation.right());
		localAxis[1].set(orientation.up());
		localAxis[2].set(orientation.forward());
		this.halfwidths.set(halfwidths);

		cornerCountX = (int) Math.ceil(halfwidths.x() * 2);
		cornerCountY = (int) Math.ceil(halfwidths.y() * 2);
		cornerCountZ = (int) Math.ceil(halfwidths.z() * 2);

		this.or = orientation;

		return this;
	}

	private static class OldCornerIterator implements Iterator<Vector3fc>
	{
		int z = -1, y = -1, x = -1;

		boolean first = true;

		OBBCollider obc;
		Vector3f temp = new Vector3f();

		OldCornerIterator(OBBCollider obc)
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

	private static class CornerIterable implements Iterable<Vector3fc>
	{
		OBBCollider c;

		private CornerIterable(OBBCollider c)
		{
			this.c = c;
		}

		@Override
		public Iterator<Vector3fc> iterator()
		{
			return new OldCornerIterator(c);
		}

	}

	public Iterable<Vector3fc> cornerIterator()
	{
		return cornerIterable;
	}

	private static class CornerIterator implements Iterator<Vector3fc>
	{
		int cx = 0, cy = 0, cz = 0;

		OBBCollider obc;
		Vector3f temp;

		CornerIterator(OBBCollider obc)
		{
			this.obc = obc;

			temp = MemoryPool.getInstance(Vector3f.class);
			if (temp == null)
				temp = new Vector3f();
		}

		@Override
		public void finalize()
		{
			MemoryPool.addToPool(temp);
		}

		@Override
		public boolean hasNext()
		{
			return cz <= obc.cornerCountZ;
		}

		@Override
		public Vector3fc next()
		{
			float x = 2 * (cx / (float) obc.cornerCountX) - 1.0f;
			float y = 2 * (cy / (float) obc.cornerCountY) - 1.0f;
			float z = 2 * (cz / (float) obc.cornerCountZ) - 1.0f;

			Vector3f corner = new Vector3f(obc.center);

			corner.add(obc.localAxis[2].mul(obc.halfwidths.z * z, temp));
			corner.add(obc.localAxis[1].mul(obc.halfwidths.y * y, temp));
			corner.add(obc.localAxis[0].mul(obc.halfwidths.x * x, temp));

			cx++;
			if (cx > obc.cornerCountX)
			{
				cx = 0;
				cy++;
				if (cy > obc.cornerCountY)
				{
					cy = 0;
					cz++;
				}
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
			return c.center().equals(center())
				&& c.localAxis()[0].equals(localAxis()[0])
				&& c.localAxis()[1].equals(localAxis()[1])
				&& c.localAxis()[2].equals(localAxis()[2])
				&& c.halfwidths().equals(this.halfwidths());
		}

		return false;
	}

	@Override
	public int hashCode()
	{
		return center().hashCode() * halfwidths().hashCode()
			* localAxis()[0].hashCode() * localAxis()[1].hashCode()
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
		return "{" + Utils.toEasyString(center) + " +/- "
			+ Utils.toEasyString(halfwidths) + "; X: "
			+ Utils.toEasyString(localAxis[0]) + "; Y: "
			+ Utils.toEasyString(localAxis[1]) + "; Z: "
			+ Utils.toEasyString(localAxis[2]) + "}";
	}

	@Override
	public void reuse()
	{
		center.set(0);
		localAxis[0].set(0);
		localAxis[1].set(0);
		localAxis[2].set(0);
		halfwidths.set(0);
		or = null;

		cornerCountX = 0;
		cornerCountY = 0;
		cornerCountZ = 0;
	}
}

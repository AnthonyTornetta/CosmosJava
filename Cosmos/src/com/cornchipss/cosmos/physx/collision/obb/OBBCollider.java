package com.cornchipss.cosmos.physx.collision.obb;

import java.util.Iterator;

import org.joml.Vector3f;
import org.joml.Vector3fc;

import com.cornchipss.cosmos.physx.Orientation;

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
			if(first)
			{
				first = false;
				return new Vector3f(obc.center);
			}
			
			Vector3f corner = new Vector3f(obc.center);
			corner.add(obc.localAxis[2].mul(obc.halfwidths.z * z, temp));
			corner.add(obc.localAxis[1].mul(obc.halfwidths.y * y, temp));
			corner.add(obc.localAxis[0].mul(obc.halfwidths.x * x, temp));
			
			if(x == -1)
				x = 1;
			else if(y == -1)
			{
				y = 1;
				x = -1;
			}
			else if(z == -1)
			{
				z = 1;
				y = -1;
				x = -1;
			}
			
			return corner;
		}
	}
	
	public Vector3f center() { return center; }
	public Vector3f[] localAxis() { return localAxis; }
	public Vector3f halfwidths() { return halfwidths; }
	public Orientation orientation() { return or; }
	
	@Override
	public Iterator<Vector3fc> iterator()
	{
		return new CornerIterator(this);
	}
}

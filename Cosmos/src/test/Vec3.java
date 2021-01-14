package test;

public class Vec3
{
	private org.joml.Vector3f joml;
	private javax.vecmath.Vector3f java;
	
	public Vec3()
	{
		this(0, 0, 0);
	}
	
	public Vec3(float s)
	{
		this(s, s, s);
	}
	
	public Vec3(Vec3 v)
	{
		this(v.x(), v.y(), v.z());
	}
	
	public Vec3(float x, float y, float z)
	{
		set(new org.joml.Vector3f(x, y, z));
	}
	
	public Vec3(org.joml.Vector3f vec)
	{
		set(vec);
	}
	
	public Vec3(javax.vecmath.Vector3f vec)
	{
		set(vec);
	}
	
	public void set(org.joml.Vector3f vec)
	{
		if(vec != null)
		{
			joml = vec;
			java = new javax.vecmath.Vector3f(vec.x, vec.y, vec.z);
		}
		else
		{
			joml = null;
			java = null;
		}
	}
	
	public void set(javax.vecmath.Vector3f vec)
	{
		if(vec != null)
		{
			joml = new org.joml.Vector3f(vec.x, vec.y, vec.z);
			java = vec;
		}
		else
		{
			joml = null;
			java = null;
		}
	}
	
	public org.joml.Vector3f joml()
	{
		return joml;
	}
	
	public javax.vecmath.Vector3f jx()
	{
		return java;
	}
	
	public float x() { return joml.x; }
	public void x(float x)
	{
		joml.x = x;
		java.x = x;
	}
	public float y() { return joml.y; }
	public void y(float y)
	{
		joml.y = y;
		java.y = y;
	}
	public float z() { return joml.z; }
	public void z(float z)
	{
		joml.z = z;
		java.z = z;
	}

	public Vec3 add(Vec3 c)
	{
		joml.add(c.joml);
		set(joml);
		return this;
	}
	
	public Vec3 sub(Vec3 c)
	{
		joml.sub(c.joml);
		set(joml);
		return this;
	}
	
	public Vec3 mul(Vec3 c)
	{
		joml.mul(c.joml);
		set(joml);
		return this;
	}
	
	public Vec3 div(Vec3 c)
	{
		joml.div(c.joml);
		set(joml);
		return this;
	}
	
	public Vec3 normalize(float len)
	{
		joml.normalize(len);
		set(joml);
		return this;
	}
	
	public Vec3 normalize()
	{
		joml.normalize();
		set(joml);
		return this;
	}
}

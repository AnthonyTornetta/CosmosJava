package com.cornchipss.cosmos.utils;

public class VecUtils
{
	public static org.joml.Vector3f convert(javax.vecmath.Vector3f v)
	{
		return new org.joml.Vector3f(v.x, v.y, v.z);
	}
	
	public static javax.vecmath.Vector3f convert(org.joml.Vector3fc v)
	{
		return new javax.vecmath.Vector3f(v.x(), v.y(), v.z());
	}

	public static javax.vecmath.Matrix4f convert(org.joml.Matrix4fc matrix)
	{
		javax.vecmath.Matrix4f ret = new javax.vecmath.Matrix4f();
		
		ret.m00 = matrix.m00();
		ret.m01 = matrix.m01();
		ret.m02 = matrix.m02();
		ret.m03 = matrix.m03();
		
		ret.m10 = matrix.m10();
		ret.m11 = matrix.m11();
		ret.m12 = matrix.m12();
		ret.m13 = matrix.m13();
		
		ret.m20 = matrix.m20();
		ret.m21 = matrix.m21();
		ret.m22 = matrix.m22();
		ret.m23 = matrix.m23();
		
		ret.m30 = matrix.m30();
		ret.m31 = matrix.m31();
		ret.m32 = matrix.m32();
		ret.m33 = matrix.m33();
		
		return ret;
	}
}

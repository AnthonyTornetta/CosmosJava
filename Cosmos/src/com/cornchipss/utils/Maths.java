package com.cornchipss.utils;

import org.joml.Matrix4f;

public class Maths
{
	public static void createViewMatrix(float x, float y, float z, float rx, float ry, float rz, Matrix4f dest)
	{
		dest.identity();
		
		dest.rotate(rx, 1, 0, 0);
		dest.rotate(ry, 0, 1, 0);
		dest.rotate(rz, 0, 0, 1);
		
		dest.translate(-x, -y, -z);
	}
}

package com.cornchipss.cosmos.cameras;

import org.joml.Matrix4fc;

import com.cornchipss.cosmos.Vec3;

public abstract class Camera
{
	public abstract Matrix4fc viewMatrix();
	
	public abstract Vec3 forward();
	public abstract Vec3 right();
	public abstract Vec3 up();

	public abstract Vec3 position();
}

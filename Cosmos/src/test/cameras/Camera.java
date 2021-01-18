package test.cameras;

import org.joml.Matrix4fc;

import test.Vec3;

public abstract class Camera
{
	public abstract Matrix4fc viewMatrix();
	
	public abstract Vec3 forward();
	public abstract Vec3 right();
	public abstract Vec3 up();

	public abstract Vec3 position();
}

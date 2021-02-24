package com.cornchipss.cosmos.cameras;

import org.joml.Matrix4fc;
import org.joml.Vector3fc;

public abstract class Camera
{
	public abstract Matrix4fc viewMatrix();
	
	public abstract Vector3fc forward();
	public abstract Vector3fc right();
	public abstract Vector3fc up();

	public abstract Vector3fc position();
}

package com.cornchipss.cosmos.material;

import org.joml.Matrix4fc;

public class DefaultMaterial extends Material
{
	private int projLoc, camLoc, transLoc;
	
	public DefaultMaterial()
	{
		super("assets/shaders/chunk", "assets/images/atlas/main");
	}

	@Override
	public void initUniforms(Matrix4fc projectionMatrix, Matrix4fc camera, Matrix4fc transform)
	{
		shader().setUniformMatrix(projLoc, projectionMatrix);
		shader().setUniformMatrix(camLoc, camera);
		shader().setUniformMatrix(transLoc, transform);
	}

	@Override
	protected void initShader()
	{
		projLoc = shader().uniformLocation("u_proj");
		camLoc = shader().uniformLocation("u_camera");
		transLoc = shader().uniformLocation("u_transform");
	}
}

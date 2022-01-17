package com.cornchipss.cosmos.material.types;

import org.joml.Matrix4fc;

import com.cornchipss.cosmos.material.Material;

public class DebugMaterial extends Material
{
	private int projLoc, camLoc, transLoc;

	public DebugMaterial()
	{
		super("assets/shaders/debug");
	}

	@Override
	public void initUniforms(Matrix4fc projectionMatrix, Matrix4fc camera,
		Matrix4fc transform, boolean inGUI)
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

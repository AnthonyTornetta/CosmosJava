package com.cornchipss.cosmos.material;

import org.joml.Matrix4fc;

public class GuiMaterial extends Material
{
	public GuiMaterial()
	{
		super("assets/shaders/gui", "assets/images/atlas/gui");
	}

	@Override
	public void initUniforms(Matrix4fc projectionMatrix, Matrix4fc matrix4fc, Matrix4fc transform)
	{
		
	}

	@Override
	protected void initShader()
	{
		
	}
}
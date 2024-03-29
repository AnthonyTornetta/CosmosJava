package com.cornchipss.cosmos.material.types;

import org.joml.Matrix4fc;

import com.cornchipss.cosmos.material.TexturedMaterial;

public class GuiMaterial extends TexturedMaterial
{
	private int guiProjLoc, guiTransLoc;

	public GuiMaterial()
	{
		super("assets/shaders/gui", "assets/images/atlas/gui");
	}

	public GuiMaterial(String atlas)
	{
		super("assets/shaders/gui", atlas);
	}

	@Override
	public void initUniforms(Matrix4fc projectionMatrix, Matrix4fc cam,
		Matrix4fc transform, boolean isGUI)
	{
		shader().setUniformMatrix(guiTransLoc, transform);
		shader().setUniformMatrix(guiProjLoc, projectionMatrix);
	}

	@Override
	protected void initShader()
	{
		guiTransLoc = shader().uniformLocation("u_transform");
		guiProjLoc = shader().uniformLocation("u_projection");
	}

	@Override
	public float uLength()
	{
		return 16.0f / 64.0f;
	}

	@Override
	public float vLength()
	{
		return 16.0f / 64.0f;
	}
}